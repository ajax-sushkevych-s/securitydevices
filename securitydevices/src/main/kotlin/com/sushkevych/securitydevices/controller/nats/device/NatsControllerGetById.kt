package com.sushkevych.securitydevices.controller.nats.device

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.Device.GET_BY_ID
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.controller.nats.NatsController
import com.sushkevych.securitydevices.dto.response.toDevice
import com.sushkevych.securitydevices.input.request.device.get_by_id.proto.GetByIdDeviceRequest
import com.sushkevych.securitydevices.input.request.device.get_by_id.proto.GetByIdDeviceResponse
import com.sushkevych.securitydevices.service.DeviceService
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsControllerGetById(
    override val connection: Connection,
    private val deviceService: DeviceService
) : NatsController<GetByIdDeviceRequest, GetByIdDeviceResponse> {

    override val subject = GET_BY_ID
    override val parser: Parser<GetByIdDeviceRequest> = GetByIdDeviceRequest.parser()

    override fun handle(request: GetByIdDeviceRequest): GetByIdDeviceResponse = runCatching {
        val deviceId = request.request.deviceId
        val getDeviceById = deviceService.getDeviceById(deviceId)
        buildSuccessResponse(getDeviceById.toDevice())
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(device: Device): GetByIdDeviceResponse =
        GetByIdDeviceResponse.newBuilder().apply {
            responseBuilder.successBuilder
                .setMessage("Device found by id successfully")
                .setDevice(device)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetByIdDeviceResponse =
        GetByIdDeviceResponse.newBuilder().apply {
            responseBuilder.failureBuilder
                .setMessage("Device find by id failed")
                .errBuilder
                .setException(exception)
                .setErrorMessage(message)
        }.build()
}
