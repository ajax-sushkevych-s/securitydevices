package com.sushkevych.securitydevices.device.infrastructure.adapters.nats.controller

import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject.DeviceRequest.DELETE
import com.sushkevych.securitydevices.device.application.port.DeviceService
import com.sushkevych.securitydevices.request.device.delete.proto.DeleteDeviceRequest
import com.sushkevych.securitydevices.request.device.delete.proto.DeleteDeviceResponse
import com.sushkevych.securitydevices.core.infrastructure.adapters.nats.NatsController
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class DeleteDeviceNatsController(
    override val connection: Connection,
    private val deviceService: DeviceService
) : NatsController<DeleteDeviceRequest, DeleteDeviceResponse> {

    override val subject = DELETE
    override val parser: Parser<DeleteDeviceRequest> = DeleteDeviceRequest.parser()

    override fun handle(request: DeleteDeviceRequest): Mono<DeleteDeviceResponse> {
        val deviceId = request.deviceId
        return deviceService.delete(deviceId)
            .map { buildSuccessResponse() }
            .onErrorResume { exception ->
                buildFailureResponse(
                    exception.javaClass.simpleName,
                    exception.toString()
                ).toMono()
            }
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
