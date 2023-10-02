package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.dto.request.DeviceRequest
import com.sushkevych.securitydevices.dto.response.DeviceResponse

interface DeviceService {
    fun getDeviceById(deviceId: String): DeviceResponse

    fun getAllDevices(): List<DeviceResponse>

    fun saveDevice(deviceRequest: DeviceRequest): DeviceResponse

    fun updateDevice(deviceId: String, deviceRequest: DeviceRequest): DeviceResponse

    fun deleteDevice(deviceId: String)
}
