package com.sushkevych.securitydevices.controller.nats.device

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject
import com.sushkevych.internalapi.NatsSubject.DeviceRequest.UPDATE
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.controller.nats.NatsController
import com.sushkevych.securitydevices.dto.request.toDeviceRequest
import com.sushkevych.securitydevices.dto.response.toProtoDevice
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
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
        val updatedDevice = deviceService.updateDevice(request.deviceId, device).toProtoDevice()
        publishUpdatedEvent(updatedDevice)
        buildSuccessResponse(updatedDevice)
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

    private fun publishUpdatedEvent(updatedDevice: Device) {
        val updateEventSubject = "${NatsSubject.DeviceEvent.UPDATED}.${
            updatedDevice.name.replace(" ", "_")
                .lowercase()
        }"

        val eventMessage = DeviceUpdatedEvent.newBuilder().apply {
            deviceId = updatedDevice.id
            deviceName = updatedDevice.name
        }.build()

        connection.publish(updateEventSubject, eventMessage.toByteArray())
    }
}
