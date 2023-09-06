package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.dto.request.DeviceDtoRequest
import com.sushkevych.securitydevices.dto.request.toEntity
import com.sushkevych.securitydevices.dto.response.DeviceDtoResponse
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.model.Device
import com.sushkevych.securitydevices.repository.DeviceRepository
import org.springframework.stereotype.Service

@Service
class DeviceService(private val deviceRepository: DeviceRepository) {
    fun getDeviceById(deviceId: Long): Device = deviceRepository.getReferenceById(deviceId)
    fun getAllDevices(): List<DeviceDtoResponse> = deviceRepository.findAll().map { it.toResponse() }
    fun saveDevice(deviceDtoRequest: DeviceDtoRequest): DeviceDtoResponse {
        val device = deviceDtoRequest.toEntity()
        deviceRepository.save(device)
        return device.toResponse()
    }

    fun updateDevice(id: Long, deviceDtoRequest: DeviceDtoRequest): DeviceDtoResponse {
        val device = deviceDtoRequest.toEntity()
        device.id = deviceRepository.getReferenceById(id).id
        deviceRepository.save(device)
        return device.toResponse()
    }

    fun deleteDevice(deviceId: Long) = deviceRepository.deleteById(deviceId)
}
