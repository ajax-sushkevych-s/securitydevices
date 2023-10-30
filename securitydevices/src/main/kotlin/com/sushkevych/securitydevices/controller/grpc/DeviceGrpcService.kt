package com.sushkevych.securitydevices.controller.grpc

import com.sushkevych.securitydevices.ReactorDeviceServiceGrpc
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.dto.response.toProtoDevice
import com.sushkevych.securitydevices.request.device.get_all_stream.proto.GetAllDeviceStreamRequest
import com.sushkevych.securitydevices.request.device.get_all_stream.proto.GetAllDeviceStreamResponse
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceRequest
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceResponse
import com.sushkevych.securitydevices.service.DeviceService
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@GrpcService
class DeviceGrpcService(private val deviceService: DeviceService) : ReactorDeviceServiceGrpc.DeviceServiceImplBase() {

    override fun getAll(request: Mono<GetAllDeviceStreamRequest>): Flux<GetAllDeviceStreamResponse> =
        request.flatMapMany { handleGetAll(it) }

    override fun getById(request: Mono<GetByIdDeviceRequest>): Mono<GetByIdDeviceResponse> =
        request.flatMap { handleGetById(it) }

    private fun handleGetById(request: GetByIdDeviceRequest): Mono<GetByIdDeviceResponse> =
        deviceService.getDeviceById(request.deviceId)
            .map { buildSuccessResponseGetById(it.toProtoDevice()) }
            .onErrorResume { exception ->
                buildFailureResponseGetById(
                    exception.javaClass.simpleName,
                    exception.toString()
                ).toMono()
            }

    @Suppress("UnusedParameter")
    private fun handleGetAll(request: GetAllDeviceStreamRequest): Flux<GetAllDeviceStreamResponse> =
        deviceService.getAllDevices()
            .map { device -> buildSuccessResponseGetAll(device.toProtoDevice()) }
            .onErrorResume { exception ->
                buildFailureResponseGetAll(
                    exception.javaClass.simpleName,
                    exception.toString()
                ).toMono()
            }

    private fun buildSuccessResponseGetById(device: Device): GetByIdDeviceResponse =
        GetByIdDeviceResponse.newBuilder().apply {
            successBuilder.setDevice(device)
        }.build()

    private fun buildFailureResponseGetById(exception: String, message: String): GetByIdDeviceResponse =
        GetByIdDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage("Device find by id failed by $exception: $message")
        }.build()

    private fun buildSuccessResponseGetAll(device: Device): GetAllDeviceStreamResponse =
        GetAllDeviceStreamResponse.newBuilder().apply {
            successBuilder.setDevice(device)
        }.build()

    private fun buildFailureResponseGetAll(exception: String, message: String): GetAllDeviceStreamResponse =
        GetAllDeviceStreamResponse.newBuilder().apply {
            failureBuilder.setMessage("Devices find failed by $exception: $message")
        }.build()
}
