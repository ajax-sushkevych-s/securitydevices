package com.sushkevych.securitydevices.controller.rest.nats

import com.sushkevych.securitydevices.controller.nats.device.NatsControllerCreate
import com.sushkevych.securitydevices.dto.request.DeviceRequest
import com.sushkevych.securitydevices.dto.request.toDevice
import com.sushkevych.securitydevices.dto.response.toDeviceResponse
import com.sushkevych.securitydevices.input.request.device.create.proto.CreateDeviceRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/natsdevices")
class CreateController(
    private val natsControllerCreate: NatsControllerCreate
) {

    @PostMapping
    fun createDevice(@Valid @RequestBody device: DeviceRequest): ResponseEntity<Any> {
        val request = CreateDeviceRequest.newBuilder().setRequest(
            com.sushkevych.securitydevices.commonmodels.device.DeviceRequest.newBuilder()
                .setDevice(device.toDevice())
                .build()
        ).build()

        val response = natsControllerCreate.handle(request)

        return if (response.response.hasSuccess()) {
            val deviceResponse = response.response.success.device.toDeviceResponse()
            val message = response.response.success.message
            ResponseEntity(Pair(deviceResponse, message), HttpStatus.OK)
        } else {
            val errorMessage = response.response.failure.message
            ResponseEntity(mapOf("error" to errorMessage), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
