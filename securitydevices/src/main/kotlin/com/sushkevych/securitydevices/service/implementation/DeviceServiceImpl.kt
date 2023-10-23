package com.sushkevych.securitydevices.service.implementation

import com.sushkevych.securitydevices.dto.request.DeviceRequest
import com.sushkevych.securitydevices.dto.request.toEntity
import com.sushkevych.securitydevices.dto.response.DeviceResponse
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.exception.NotFoundException
import com.sushkevych.securitydevices.model.MongoDevice
import com.sushkevych.securitydevices.repository.DeviceRepository
import com.sushkevych.securitydevices.service.DeviceService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class DeviceServiceImpl(
    @Qualifier("deviceQueryRepository") private val deviceMongoRepository: DeviceRepository,
    @Qualifier("deviceRedisRepository") private val deviceRedisRepository: DeviceRepository
) : DeviceService {
    override fun getDeviceById(deviceId: String): Mono<DeviceResponse> =
        deviceRedisRepository.getDeviceById(ObjectId(deviceId))
            .switchIfEmpty(
                deviceMongoRepository.getDeviceById(ObjectId(deviceId))
                    .flatMap { deviceRedisRepository.save(it) }
                    .switchIfEmpty(Mono.error(NotFoundException(message = "Device with ID $deviceId not found")))
            )
            .map { it.toResponse() }

    override fun getAllDevices(): Mono<List<DeviceResponse>> =
        deviceRedisRepository.findAll()
            .collectList()
            .flatMap { cachedDevices ->
                if (cachedDevices.isNotEmpty()) cachedDevices.toMono() else fetchFromMongoAndCache()
            }
            .map { devices -> devices.map(MongoDevice::toResponse) }

    override fun saveDevice(deviceRequest: DeviceRequest): Mono<DeviceResponse> =
        deviceMongoRepository.save(deviceRequest.toEntity())
            .flatMap { deviceRedisRepository.save(it) }
            .map { it.toResponse() }

    override fun updateDevice(deviceRequest: DeviceRequest): Mono<DeviceResponse> =
        deviceMongoRepository.update(deviceRequest.toEntity())
            .flatMap { deviceRedisRepository.save(it) }
            .switchIfEmpty(Mono.error(NotFoundException(message = "Device with ID ${deviceRequest.id} not found")))
            .map { it.toResponse() }

    override fun deleteDevice(deviceId: String) =
        deviceMongoRepository.deleteById(ObjectId(deviceId))
            .then(deviceRedisRepository.deleteById(ObjectId(deviceId)))
            .thenReturn(Unit)

    private fun fetchFromMongoAndCache(): Mono<List<MongoDevice>> {
        return deviceMongoRepository.findAll()
            .flatMap { deviceRedisRepository.save(it) }
            .collectList()
    }
}
