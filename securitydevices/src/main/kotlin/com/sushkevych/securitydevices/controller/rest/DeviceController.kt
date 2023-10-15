package com.sushkevych.securitydevices.controller.rest

import com.sushkevych.securitydevices.dto.request.DeviceRequest
import com.sushkevych.securitydevices.dto.response.DeviceResponse
import com.sushkevych.securitydevices.service.DeviceService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.ResponseStatus
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/devices")
class DeviceController(private val deviceService: DeviceService) {
    @GetMapping("/{deviceId}")
    fun getDeviceById(@PathVariable deviceId: String): Mono<DeviceResponse> = deviceService.getDeviceById(deviceId)

    @GetMapping
    fun getAllDevices(): Mono<List<DeviceResponse>> = deviceService.getAllDevices()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createDevice(@Valid @RequestBody device: DeviceRequest): Mono<DeviceResponse> =
        deviceService.saveDevice(device)

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    fun updateDevice(
        @Valid @RequestBody device: DeviceRequest
    ): Mono<DeviceResponse> = deviceService.updateDevice(device)

    @DeleteMapping("/{deviceId}")
    fun deleteDevice(@PathVariable deviceId: String): Mono<Unit> = deviceService.deleteDevice(deviceId)
}
