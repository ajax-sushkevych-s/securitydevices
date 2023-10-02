package com.sushkevych.securitydevices.controller.nats.device

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.Device.CREATE
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.controller.nats.NatsController
import com.sushkevych.securitydevices.dto.request.toDeviceRequest
import com.sushkevych.securitydevices.dto.response.toDevice
import com.sushkevych.securitydevices.input.request.device.create.proto.CreateDeviceRequest
import com.sushkevych.securitydevices.input.request.device.create.proto.CreateDeviceResponse
import com.sushkevych.securitydevices.service.DeviceService
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsControllerCreate(
    override val connection: Connection,
    private val deviceService: DeviceService
) : NatsController<CreateDeviceRequest, CreateDeviceResponse> {

    override val subject = CREATE
    override val parser: Parser<CreateDeviceRequest> = CreateDeviceRequest.parser()

    override fun handle(request: CreateDeviceRequest): CreateDeviceResponse = runCatching {
        val device = request.request.device.toDeviceRequest()
        val saveDevice = deviceService.saveDevice(device)
        buildSuccessResponse(saveDevice.toDevice())
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(device: Device): CreateDeviceResponse =
        CreateDeviceResponse.newBuilder().apply {
            responseBuilder.successBuilder
                .setMessage("Device created successfully")
                .setDevice(device)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): CreateDeviceResponse =
        CreateDeviceResponse.newBuilder().apply {
            responseBuilder.failureBuilder
                .setMessage("Device creation failed")
                .errBuilder
                .setException(exception)
                .setErrorMessage(message)
        }.build()
}
