package com.sushkevych.securitydevices.controller.nats.device

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.DeviceRequest.GET_BY_ID
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.controller.nats.NatsController
import com.sushkevych.securitydevices.dto.response.toProtoDevice
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceRequest
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceResponse
import com.sushkevych.securitydevices.service.DeviceService
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class GetDeviceByIdNatsController(
    override val connection: Connection,
    private val deviceService: DeviceService
) : NatsController<GetByIdDeviceRequest, GetByIdDeviceResponse> {

    override val subject = GET_BY_ID
    override val parser: Parser<GetByIdDeviceRequest> = GetByIdDeviceRequest.parser()

    override fun handle(request: GetByIdDeviceRequest): GetByIdDeviceResponse = runCatching {
        val deviceId = request.deviceId
        val getDeviceById = deviceService.getDeviceById(deviceId)
        buildSuccessResponse(getDeviceById.toProtoDevice())
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(device: Device): GetByIdDeviceResponse =
        GetByIdDeviceResponse.newBuilder().apply {
            successBuilder.setDevice(device)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetByIdDeviceResponse =
        GetByIdDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage("Device find by id failed by $exception: $message")
        }.build()
}
