package com.sushkevych.securitydevices.controller.nats.device

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.Device.UPDATE
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.controller.nats.NatsController
import com.sushkevych.securitydevices.dto.request.toDeviceRequest
import com.sushkevych.securitydevices.dto.response.toProtoDevice
import com.sushkevych.securitydevices.request.device.update.proto.UpdateDeviceRequest
import com.sushkevych.securitydevices.request.device.update.proto.UpdateDeviceResponse
import com.sushkevych.securitydevices.service.DeviceService
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class UpdateDeviceNatsController(
    override val connection: Connection,
    private val deviceService: DeviceService
) : NatsController<UpdateDeviceRequest, UpdateDeviceResponse> {

    override val subject = UPDATE
    override val parser: Parser<UpdateDeviceRequest> = UpdateDeviceRequest.parser()

    override fun handle(request: UpdateDeviceRequest): UpdateDeviceResponse = runCatching {
        val device = request.device.toDeviceRequest()
        val updatedDevice = device.id?.let { deviceId ->
            deviceService.updateDevice(deviceId, device)
        } ?: throw IllegalArgumentException("Device ID is null")
        buildSuccessResponse(updatedDevice.toProtoDevice())
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(device: Device): UpdateDeviceResponse =
        UpdateDeviceResponse.newBuilder().apply {
            successBuilder.setDevice(device)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): UpdateDeviceResponse =
        UpdateDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage("Device update failed by $exception: $message")
        }.build()
}
