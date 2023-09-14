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

@Service
class DeviceServiceImpl(private val deviceRepository: DeviceRepository) : DeviceService {
    override fun getDeviceById(deviceId: String): DeviceResponse = deviceRepository.getDeviceById(ObjectId(deviceId))
        ?.toResponse()
        ?: throw NotFoundException(message = "Device with ID $deviceId not found")

    override fun getAllDevices(): List<DeviceResponse> = deviceRepository.findAll().map { it.toResponse() }

    override fun saveDevice(deviceRequest: DeviceRequest): DeviceResponse {
        val device = deviceRequest.toEntity()
        deviceRepository.save(device)
        return device.toResponse()
    }

    override fun updateDevice(deviceId: String, deviceRequest: DeviceRequest): DeviceResponse {
        val existingDevice = getDeviceById(deviceId)
        val updatedDevice = deviceRequest.toEntity().copy(id = ObjectId(existingDevice.id))
        deviceRepository.save(updatedDevice)
        return updatedDevice.toResponse()
    }

    override fun deleteDevice(deviceId: String) = deviceRepository.deleteById(ObjectId(deviceId))
}
