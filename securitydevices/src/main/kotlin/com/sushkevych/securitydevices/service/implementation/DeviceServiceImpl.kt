package com.sushkevych.securitydevices.service.implementation

import com.sushkevych.securitydevices.dto.request.DeviceRequest
import com.sushkevych.securitydevices.dto.request.toEntity
import com.sushkevych.securitydevices.dto.response.DeviceResponse
import com.sushkevych.securitydevices.dto.response.toProtoDevice
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.exception.NotFoundException
import com.sushkevych.securitydevices.kafka.DeviceKafkaProducer
import com.sushkevych.securitydevices.model.MongoDevice
import com.sushkevych.securitydevices.repository.DeviceCacheableRepository
import com.sushkevych.securitydevices.service.DeviceService
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DeviceServiceImpl(
    private val deviceCacheableRepository: DeviceCacheableRepository,
    private val deviceKafkaProducer: DeviceKafkaProducer
) : DeviceService {
    override fun getDeviceById(deviceId: String): Mono<DeviceResponse> =
        deviceCacheableRepository.getDeviceById(ObjectId(deviceId))
            .switchIfEmpty(Mono.error(NotFoundException(message = "Device with ID $deviceId not found")))
            .map { it.toResponse() }

    override fun getAllDevices(): Flux<DeviceResponse> =
        deviceCacheableRepository.findAll()
            .map (MongoDevice::toResponse)

    override fun saveDevice(deviceRequest: DeviceRequest): Mono<DeviceResponse> =
        deviceCacheableRepository.save(deviceRequest.toEntity())
            .map { it.toResponse() }

    override fun updateDevice(deviceRequest: DeviceRequest): Mono<DeviceResponse> =
        deviceCacheableRepository.update(deviceRequest.toEntity())
            .doOnNext {
                deviceKafkaProducer.sendDeviceUpdatedEventToKafka(it.toResponse().toProtoDevice())
            }
            .switchIfEmpty(Mono.error(NotFoundException(message = "Device with ID ${deviceRequest.id} not found")))
            .map { it.toResponse() }

    override fun deleteDevice(deviceId: String) =
        deviceCacheableRepository.deleteById(ObjectId(deviceId))
            .thenReturn(Unit)
}
