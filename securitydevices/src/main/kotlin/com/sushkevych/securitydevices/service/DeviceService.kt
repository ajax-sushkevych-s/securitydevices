package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.dto.request.DeviceRequest
import com.sushkevych.securitydevices.dto.response.DeviceResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DeviceService {
    fun getDeviceById(deviceId: String): Mono<DeviceResponse>

    fun getAllDevices(): Flux<DeviceResponse>

    fun saveDevice(deviceRequest: DeviceRequest): Mono<DeviceResponse>

    fun updateDevice(deviceRequest: DeviceRequest): Mono<DeviceResponse>

    fun deleteDevice(deviceId: String): Mono<Unit>
}
