package com.sushkevych.securitydevices.controller

import com.sushkevych.securitydevices.dto.request.DeviceRequest
import com.sushkevych.securitydevices.dto.response.DeviceResponse
import com.sushkevych.securitydevices.service.DeviceService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping

@RestController
@RequestMapping("/api/devices")
class DeviceController(private val deviceService: DeviceService) {
    @GetMapping("/{deviceId}")
    fun getDeviceById(@PathVariable deviceId: String): DeviceResponse = deviceService.getDeviceById(deviceId)

    @GetMapping
    fun getAllDevices(): List<DeviceResponse> = deviceService.getAllDevices()

    @PostMapping
    fun createDevice(@Valid @RequestBody device: DeviceRequest): ResponseEntity<DeviceResponse> =
        ResponseEntity(
            deviceService.saveDevice(device),
            HttpStatus.OK
        )

    @PutMapping("/{deviceId}")
    fun updateDevice(
        @PathVariable deviceId: String,
        @Valid @RequestBody device: DeviceRequest
    ): ResponseEntity<DeviceResponse> =
        ResponseEntity(
            deviceService.updateDevice(deviceId, device),
            HttpStatus.OK
        )

    @DeleteMapping("/{deviceId}")
    fun deleteDevice(@PathVariable deviceId: String) = deviceService.deleteDevice(deviceId)
}
