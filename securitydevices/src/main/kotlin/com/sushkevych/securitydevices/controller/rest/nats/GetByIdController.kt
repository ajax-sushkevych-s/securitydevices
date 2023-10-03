package com.sushkevych.securitydevices.controller.rest.nats

import com.sushkevych.securitydevices.controller.nats.device.GetDeviceByIdNatsController
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
    private val getDeviceByIdNatsController: GetDeviceByIdNatsController
) {
    @GetMapping("/{deviceId}")
    fun getDeviceById(@PathVariable deviceId: String): ResponseEntity<Any> {
        val request = GetByIdDeviceRequest.newBuilder()
            .setRequest(
                com.sushkevych.securitydevices.commonmodels.device.GetByIdDeviceRequest.newBuilder()
                    .setDeviceId(deviceId).build()
            )
            .build()

        val response = getDeviceByIdNatsController.handle(request)

        return if (response.response.hasSuccess()) {
            val device = response.response.success.device.toDeviceResponse()
            ResponseEntity(device, HttpStatus.OK)
        } else {
            val errorMessage = response.response.failure.message
            ResponseEntity(mapOf("error" to errorMessage), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
