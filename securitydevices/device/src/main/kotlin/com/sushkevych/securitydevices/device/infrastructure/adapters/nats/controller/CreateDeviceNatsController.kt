package com.sushkevych.securitydevices.device.infrastructure.adapters.nats.controller

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.DeviceRequest.CREATE
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.device.application.port.DeviceOperationsInPort
import com.sushkevych.securitydevices.device.infrastructure.mapper.toDevice
import com.sushkevych.securitydevices.device.infrastructure.mapper.toProtoDevice
import com.sushkevych.securitydevices.request.device.create.proto.CreateDeviceRequest
import com.sushkevych.securitydevices.request.device.create.proto.CreateDeviceResponse
import com.sushkevych.securitydevices.core.infrastructure.adapters.nats.NatsController
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CreateDeviceNatsController(
    override val connection: Connection,
    private val deviceOperations: DeviceOperationsInPort
) : NatsController<CreateDeviceRequest, CreateDeviceResponse> {

    override val subject = CREATE
    override val parser: Parser<CreateDeviceRequest> = CreateDeviceRequest.parser()

    override fun handle(request: CreateDeviceRequest): Mono<CreateDeviceResponse> {
        val device = request.device.toDevice()
        return deviceOperations.save(device)
            .map {buildSuccessResponse(it.toProtoDevice()) }
            .onErrorResume { exception ->
                buildFailureResponse(
                    exception.javaClass.simpleName,
                    exception.toString()
                ).toMono()
            }
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
