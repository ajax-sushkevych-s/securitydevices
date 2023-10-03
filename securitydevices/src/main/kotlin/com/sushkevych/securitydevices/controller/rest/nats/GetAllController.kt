package com.sushkevych.securitydevices.controller.rest.nats

import com.sushkevych.securitydevices.controller.nats.device.GetAllDeviceNatsController
import com.sushkevych.securitydevices.dto.response.toDeviceResponse
import com.sushkevych.securitydevices.input.request.device.get_all.proto.GetAllDeviceRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/natsdevices")
class GetAllController(
    private val getAllDeviceNatsController: GetAllDeviceNatsController
) {
    @GetMapping("/")
    fun getAllDevices(): ResponseEntity<Any> {
        val request = GetAllDeviceRequest.newBuilder().build()
        val response = getAllDeviceNatsController.handle(request)

        return if (response.response.hasSuccess()) {
            val devices = response.response.success.devices.devicesList.map { it.toDeviceResponse() }
            ResponseEntity(devices, HttpStatus.OK)
        } else {
            val errorMessage = response.response.failure.message
            ResponseEntity(mapOf("error" to errorMessage), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
