package com.sushkevych.securitydevices.controller.rest.nats

import com.sushkevych.securitydevices.commonmodels.device.DeviceRequest
import com.sushkevych.securitydevices.controller.nats.device.NatsControllerDelete
import com.sushkevych.securitydevices.input.request.device.delete.proto.DeleteDeviceRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/natsdevices")
class DeleteController(
    private val natsControllerDelete: NatsControllerDelete
) {
    @DeleteMapping("/{deviceId}")
    fun deleteDevice(@PathVariable deviceId: String): ResponseEntity<Any> {
        val request = DeleteDeviceRequest.newBuilder()
            .setRequest(DeviceRequest.newBuilder().setDeviceId(deviceId).build())
            .build()

        val response = natsControllerDelete.handle(request)

        return if (response.response.hasSuccess()) {
            ResponseEntity(response.response.success.message, HttpStatus.OK)
        } else {
            val errorMessage = response.response.failure.message
            ResponseEntity(mapOf("error" to errorMessage), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
