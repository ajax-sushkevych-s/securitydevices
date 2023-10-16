package com.sushkevych.securitydevices.service.implementation

import com.sushkevych.securitydevices.dto.request.DeviceRequest
import com.sushkevych.securitydevices.dto.request.toEntity
import com.sushkevych.securitydevices.dto.response.DeviceResponse
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.exception.NotFoundException
import com.sushkevych.securitydevices.repository.DeviceRepository
import com.sushkevych.securitydevices.service.DeviceService
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DeviceServiceImpl(private val deviceRepository: DeviceRepository) : DeviceService {
    override fun getDeviceById(deviceId: String): Mono<DeviceResponse> =
        deviceRepository.getDeviceById(ObjectId(deviceId))
            .switchIfEmpty(Mono.error(NotFoundException(message = "Device with ID $deviceId not found")))
            .map { it.toResponse() }

    override fun getAllDevices(): Mono<List<DeviceResponse>> =
        deviceRepository.findAll()
            .map { it.toResponse() }
            .collectList()

    override fun saveDevice(deviceRequest: DeviceRequest): Mono<DeviceResponse> =
        deviceRepository.save(deviceRequest.toEntity())
            .map { it.toResponse() }

    override fun updateDevice(deviceRequest: DeviceRequest): Mono<DeviceResponse> =
        deviceRepository.update(deviceRequest.toEntity())
            .switchIfEmpty(Mono.error(NotFoundException(message = "Device with ID ${deviceRequest.id} not found")))
            .map { it.toResponse() }

    override fun deleteDevice(deviceId: String) = deviceRepository.deleteById(ObjectId(deviceId))
}
