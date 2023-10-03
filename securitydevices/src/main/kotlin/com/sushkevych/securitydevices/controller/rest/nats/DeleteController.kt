package com.sushkevych.securitydevices.controller.rest.nats

import com.sushkevych.securitydevices.controller.nats.device.DeleteDeviceNatsController
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
    private val deleteDeviceNatsController: DeleteDeviceNatsController
) {
    @DeleteMapping("/{deviceId}")
    fun deleteDevice(@PathVariable deviceId: String): ResponseEntity<Any> {
        val request = DeleteDeviceRequest.newBuilder()
            .setRequest(
                com.sushkevych.securitydevices.commonmodels.device.DeleteDeviceRequest.newBuilder()
                    .setDeviceId(deviceId).build()
            )
            .build()

        val response = deleteDeviceNatsController.handle(request)

        return if (response.response.hasSuccess()) {
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            val errorMessage = response.response.failure.message
            ResponseEntity(mapOf("error" to errorMessage), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
