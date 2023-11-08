package com.sushkevych.securitydevices.device.infrastructure.adapters.nats.controller

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.DeviceRequest.UPDATE
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.device.application.port.DeviceOperationsInPort
import com.sushkevych.securitydevices.device.infrastructure.mapper.toDevice
import com.sushkevych.securitydevices.device.infrastructure.mapper.toProtoDevice
import com.sushkevych.securitydevices.request.device.update.proto.UpdateDeviceRequest
import com.sushkevych.securitydevices.request.device.update.proto.UpdateDeviceResponse
import com.sushkevych.securitydevices.core.infrastructure.adapters.nats.NatsController
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UpdateDeviceNatsController(
    override val connection: Connection,
    private val deviceOperations: DeviceOperationsInPort
) : NatsController<UpdateDeviceRequest, UpdateDeviceResponse> {

    override val subject = UPDATE
    override val parser: Parser<UpdateDeviceRequest> = UpdateDeviceRequest.parser()

    override fun handle(request: UpdateDeviceRequest): Mono<UpdateDeviceResponse> {
        val device = request.device.toDevice()
        return deviceOperations.update(device)
            .map {
                buildSuccessResponse(it.toProtoDevice())
            }
            .onErrorResume { exception ->
                Mono.just(buildFailureResponse(exception.javaClass.simpleName, exception.toString()))
            }
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
