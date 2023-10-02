package com.sushkevych.securitydevices.controller.nats.device

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.Device.GET_ALL
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.controller.nats.NatsController
import com.sushkevych.securitydevices.dto.response.toDevice
import com.sushkevych.securitydevices.input.request.device.get_all.proto.GetAllDeviceRequest
import com.sushkevych.securitydevices.input.request.device.get_all.proto.GetAllDeviceResponse
import com.sushkevych.securitydevices.service.DeviceService
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsControllerGetAll(
    override val connection: Connection,
    private val deviceService: DeviceService
) : NatsController<GetAllDeviceRequest, GetAllDeviceResponse> {

    override val subject = GET_ALL
    override val parser: Parser<GetAllDeviceRequest> = GetAllDeviceRequest.parser()

    override fun handle(request: GetAllDeviceRequest): GetAllDeviceResponse = runCatching {
        buildSuccessResponse(deviceService.getAllDevices().map { it.toDevice() })
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(deviceList: List<Device>): GetAllDeviceResponse =
        GetAllDeviceResponse.newBuilder().apply {
            responseBuilder.successBuilder
                .setMessage("Devices found successfully")
                .devicesBuilder
                .addAllDevices(deviceList)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetAllDeviceResponse =
        GetAllDeviceResponse.newBuilder().apply {
            responseBuilder.failureBuilder
                .setMessage("Devices find failed")
                .errBuilder
                .setException(exception)
                .setErrorMessage(message)
        }.build()
}
