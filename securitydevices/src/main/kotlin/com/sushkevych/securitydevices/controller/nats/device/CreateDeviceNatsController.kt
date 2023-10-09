package com.sushkevych.securitydevices.controller.nats.device

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.DeviceRequest.CREATE
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.controller.nats.NatsController
import com.sushkevych.securitydevices.dto.request.toDeviceRequest
import com.sushkevych.securitydevices.dto.response.toProtoDevice
import com.sushkevych.securitydevices.request.device.create.proto.CreateDeviceRequest
import com.sushkevych.securitydevices.request.device.create.proto.CreateDeviceResponse
import com.sushkevych.securitydevices.service.DeviceService
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class CreateDeviceNatsController(
    override val connection: Connection,
    private val deviceService: DeviceService
) : NatsController<CreateDeviceRequest, CreateDeviceResponse> {

    override val subject = CREATE
    override val parser: Parser<CreateDeviceRequest> = CreateDeviceRequest.parser()

    override fun handle(request: CreateDeviceRequest): CreateDeviceResponse = runCatching {
        val device = request.device.toDeviceRequest()
        val saveDevice = deviceService.saveDevice(device)
        buildSuccessResponse(saveDevice.toProtoDevice())
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(device: Device): CreateDeviceResponse =
        CreateDeviceResponse.newBuilder().apply {
            successBuilder.setDevice(device)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): CreateDeviceResponse =
        CreateDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage("Device creation failed by $exception: $message")
        }.build()
}
