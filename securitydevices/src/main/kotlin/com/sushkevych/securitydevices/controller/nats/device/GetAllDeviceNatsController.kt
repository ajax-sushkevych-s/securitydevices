package com.sushkevych.securitydevices.controller.nats.device

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.DeviceRequest.GET_ALL
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.controller.nats.NatsController
import com.sushkevych.securitydevices.dto.response.toProtoDevice
import com.sushkevych.securitydevices.request.device.get_all.proto.GetAllDevicesRequest
import com.sushkevych.securitydevices.request.device.get_all.proto.GetAllDevicesResponse
import com.sushkevych.securitydevices.service.DeviceService
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class GetAllDeviceNatsController(
    override val connection: Connection,
    private val deviceService: DeviceService
) : NatsController<GetAllDevicesRequest, GetAllDevicesResponse> {

    override val subject = GET_ALL
    override val parser: Parser<GetAllDevicesRequest> = GetAllDevicesRequest.parser()

    override fun handle(request: GetAllDevicesRequest): GetAllDevicesResponse = runCatching {
        buildSuccessResponse(deviceService.getAllDevices().map { it.toProtoDevice() })
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(deviceList: List<Device>): GetAllDevicesResponse =
        GetAllDevicesResponse.newBuilder().apply {
            successBuilder.addAllDevices(deviceList)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetAllDevicesResponse =
        GetAllDevicesResponse.newBuilder().apply {
            failureBuilder
                .setMessage("Devices find failed by $exception: $message")
        }.build()
}
