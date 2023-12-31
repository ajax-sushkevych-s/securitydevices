package com.sushkevych.securitydevices.device.infrastructure.adapters.nats.controller

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.DeviceRequest.GET_BY_ID
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.device.application.port.DeviceOperationsInPort
import com.sushkevych.securitydevices.device.infrastructure.mapper.toProtoDevice
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceRequest
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceResponse
import com.sushkevych.securitydevices.core.infrastructure.adapters.nats.NatsController
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetDeviceByIdNatsController(
    override val connection: Connection,
    private val deviceOperations: DeviceOperationsInPort
) : NatsController<GetByIdDeviceRequest, GetByIdDeviceResponse> {

    override val subject = GET_BY_ID
    override val parser: Parser<GetByIdDeviceRequest> = GetByIdDeviceRequest.parser()

    override fun handle(request: GetByIdDeviceRequest): Mono<GetByIdDeviceResponse> {
        val deviceId = request.deviceId
        return deviceOperations.getById(deviceId)
            .map { buildSuccessResponse(it.toProtoDevice()) }
            .onErrorResume { exception ->
                buildFailureResponse(
                    exception.javaClass.simpleName,
                    exception.toString()
                ).toMono()
            }
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
