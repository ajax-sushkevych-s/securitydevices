package com.sushkevych.securitydevices.controller.rest.nats

import com.sushkevych.securitydevices.commonmodels.device.DeviceRequest
import com.sushkevych.securitydevices.controller.nats.device.NatsControllerGetById
import com.sushkevych.securitydevices.dto.response.toDeviceResponse
import com.sushkevych.securitydevices.input.request.device.get_by_id.proto.GetByIdDeviceRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/natsdevices")
class GetByIdController(
    private val natsControllerGetById: NatsControllerGetById
) {
    @GetMapping("/{deviceId}")
    fun getDeviceById(@PathVariable deviceId: String): ResponseEntity<Any> {
        val request = GetByIdDeviceRequest.newBuilder()
            .setRequest(DeviceRequest.newBuilder().setDeviceId(deviceId).build())
            .build()

        val response = natsControllerGetById.handle(request)

        return if (response.response.hasSuccess()) {
            val device = response.response.success.device.toDeviceResponse()
            val message = response.response.success.message
            ResponseEntity(Pair(device, message), HttpStatus.OK)
        } else {
            val errorMessage = response.response.failure.message
            ResponseEntity(mapOf("error" to errorMessage), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
