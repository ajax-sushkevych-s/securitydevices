package com.sushkevych.securitydevices.device.infrastructure.adapters.rest

import com.sushkevych.securitydevices.device.application.port.DeviceOperationsInPort
import com.sushkevych.securitydevices.device.infrastructure.dto.request.DeviceRequest
import com.sushkevych.securitydevices.device.infrastructure.dto.response.DeviceResponse
import com.sushkevych.securitydevices.device.infrastructure.mapper.toDevice
import com.sushkevych.securitydevices.device.infrastructure.mapper.toDeviceResponse
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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/devices")
class DeviceController(private val deviceOperations: DeviceOperationsInPort) {
    @GetMapping("/{deviceId}")
    fun getDeviceById(@PathVariable deviceId: String): Mono<DeviceResponse> =
        deviceOperations.getById(deviceId)
            .map { it.toDeviceResponse() }

    @GetMapping
    fun findAllDevices(): Flux<DeviceResponse> =
        deviceOperations.findAll()
            .map { it.toDeviceResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createDevice(@Valid @RequestBody deviceRequest: DeviceRequest): Mono<DeviceResponse> =
        deviceOperations.save(deviceRequest.toDevice())
            .map { it.toDeviceResponse() }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    fun updateDevice(
        @Valid @RequestBody deviceRequest: DeviceRequest
    ): Mono<DeviceResponse> = deviceOperations.update(deviceRequest.toDevice())
        .map { it.toDeviceResponse() }

    @DeleteMapping("/{deviceId}")
    fun deleteDevice(@PathVariable deviceId: String): Mono<Unit> = deviceOperations.delete(deviceId)
}
