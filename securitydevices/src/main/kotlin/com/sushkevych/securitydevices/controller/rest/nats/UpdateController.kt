package com.sushkevych.securitydevices.controller.rest.nats

import com.sushkevych.securitydevices.controller.nats.device.NatsControllerUpdate
import com.sushkevych.securitydevices.dto.request.DeviceRequest
import com.sushkevych.securitydevices.dto.request.toDevice
import com.sushkevych.securitydevices.dto.response.toDeviceResponse
import com.sushkevych.securitydevices.input.request.device.update.proto.UpdateDeviceRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody

@RestController
@RequestMapping("/api/natsdevices")
class UpdateController(
    private val natsControllerUpdate: NatsControllerUpdate
) {

    @PutMapping("/{deviceId}")
    fun updateDevice(
        @PathVariable deviceId: String,
        @Valid @RequestBody device: DeviceRequest
    ): ResponseEntity<Any> {
        val request = UpdateDeviceRequest.newBuilder().setRequest(
            com.sushkevych.securitydevices.commonmodels.device.DeviceRequest.newBuilder()
                .setDevice(device.toDevice().toBuilder().setId(deviceId).build())
        ).build()

        val response = natsControllerUpdate.handle(request)

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
