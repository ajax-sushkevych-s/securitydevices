package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.dto.request.DeviceDtoRequest
import com.sushkevych.securitydevices.dto.request.toEntity
import com.sushkevych.securitydevices.dto.response.DeviceDtoResponse
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.exception.NotFoundException
import com.sushkevych.securitydevices.repository.DeviceRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class DeviceService(private val deviceRepository: DeviceRepository) {
    fun getDeviceById(deviceId: String): DeviceDtoResponse = deviceRepository.getDeviceById(ObjectId(deviceId))
        ?.toResponse()
        ?: throw NotFoundException(message = "Device with ID $deviceId not found")

    fun getAllDevices(): List<DeviceDtoResponse> = deviceRepository.findAll().map { it.toResponse() }

    fun saveDevice(deviceDtoRequest: DeviceDtoRequest): DeviceDtoResponse {
        val device = deviceDtoRequest.toEntity()
        deviceRepository.save(device)
        return device.toResponse()
    }

    fun updateDevice(deviceId: String, deviceDtoRequest: DeviceDtoRequest): DeviceDtoResponse {
        val existingDevice = getDeviceById(deviceId)
        val updatedDevice = deviceDtoRequest.toEntity().copy(id = ObjectId(existingDevice.id))
        deviceRepository.save(updatedDevice)
        return updatedDevice.toResponse()
    }

    fun deleteDevice(deviceId: String) = deviceRepository.deleteById(ObjectId(deviceId))
}
