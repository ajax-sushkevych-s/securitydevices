package com.sushkevych.securitydevices.controller.nats.device

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.DeviceRequest.DELETE
import com.sushkevych.securitydevices.controller.nats.NatsController
import com.sushkevych.securitydevices.request.device.delete.proto.DeleteDeviceRequest
import com.sushkevych.securitydevices.request.device.delete.proto.DeleteDeviceResponse
import com.sushkevych.securitydevices.service.DeviceService
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class DeleteDeviceNatsController(
    override val connection: Connection,
    private val deviceService: DeviceService
) : NatsController<DeleteDeviceRequest, DeleteDeviceResponse> {

    override val subject = DELETE
    override val parser: Parser<DeleteDeviceRequest> = DeleteDeviceRequest.parser()

    override fun handle(request: DeleteDeviceRequest): DeleteDeviceResponse = runCatching {
        val deviceId = request.deviceId
        deviceService.deleteDevice(deviceId)
        buildSuccessResponse()
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(): DeleteDeviceResponse =
        DeleteDeviceResponse.newBuilder().apply {
            successBuilder.build()
        }.build()

    private fun buildFailureResponse(exception: String, message: String): DeleteDeviceResponse =
        DeleteDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage("Device delete failed by $exception: $message")
        }.build()
}
