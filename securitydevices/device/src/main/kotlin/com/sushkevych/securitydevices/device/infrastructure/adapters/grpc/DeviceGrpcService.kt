package com.sushkevych.securitydevices.device.infrastructure.adapters.grpc

import com.sushkevych.internalapi.DeviceEvent
import com.sushkevych.securitydevices.ReactorDeviceServiceGrpc
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.device.application.port.DeviceEventNatsSubscriber
import com.sushkevych.securitydevices.device.application.port.DeviceService
import com.sushkevych.securitydevices.device.infrastructure.mapper.toProtoDevice
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
import com.sushkevych.securitydevices.request.device.get_all.proto.GetAllDevicesRequest
import com.sushkevych.securitydevices.request.device.get_all.proto.GetAllDevicesResponse
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceRequest
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceResponse
import com.sushkevych.securitydevices.request.device.stream_by_id.proto.StreamByIdRequest
import com.sushkevych.securitydevices.request.device.stream_by_id.proto.StreamByIdResponse
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@GrpcService
@Suppress("TooManyFunctions")
class DeviceGrpcService(
    private val deviceService: DeviceService,
    private val deviceEventNatsSubscriber: DeviceEventNatsSubscriber<DeviceUpdatedEvent>
) : ReactorDeviceServiceGrpc.DeviceServiceImplBase() {

    override fun getAll(request: Mono<GetAllDevicesRequest>): Mono<GetAllDevicesResponse> =
        request.flatMap { handleGetAll(it) }

    override fun getById(request: Mono<GetByIdDeviceRequest>): Mono<GetByIdDeviceResponse> =
        request.flatMap { handleGetById(it) }

    override fun streamById(request: Mono<StreamByIdRequest>): Flux<StreamByIdResponse> =
        request.flatMapMany { handleStreamById(it) }

    private fun handleGetById(request: GetByIdDeviceRequest): Mono<GetByIdDeviceResponse> =
        deviceService.getById(request.deviceId)
            .map { buildSuccessResponseGetById(it.toProtoDevice()) }
            .onErrorResume { exception ->
                buildFailureResponseGetById(
                    exception.javaClass.simpleName,
                    exception.toString()
                ).toMono()
            }

    @Suppress("UnusedParameter")
    private fun handleGetAll(request: GetAllDevicesRequest): Mono<GetAllDevicesResponse> =
        deviceService.findAll()
            .collectList()
            .map { devices -> buildSuccessResponseGetAll(devices.map { it.toProtoDevice() }) }
            .onErrorResume { exception ->
                buildFailureResponseGetAll(
                    exception.javaClass.simpleName,
                    exception.toString()
                ).toMono()
            }

    private fun handleStreamById(request: StreamByIdRequest): Flux<StreamByIdResponse> =
        deviceService.getById(request.deviceId)
            .flatMapMany { initDeviceState ->
                deviceEventNatsSubscriber.subscribeToEvents(request.deviceId, DeviceEvent.UPDATED)
                    .map { buildSuccessResponseStreamById(it.device) }
                    .startWith(buildSuccessResponseStreamById(initDeviceState.toProtoDevice()))
            }
            .onErrorResume { exception ->
                buildFailureResponseStreamById(
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

    private fun buildSuccessResponseGetAll(devices: List<Device>): GetAllDevicesResponse =
        GetAllDevicesResponse.newBuilder().apply {
            successBuilder.addAllDevices(devices)
        }.build()

    private fun buildFailureResponseGetAll(exception: String, message: String): GetAllDevicesResponse =
        GetAllDevicesResponse.newBuilder().apply {
            failureBuilder.setMessage("Devices find failed by $exception: $message")
        }.build()

    private fun buildSuccessResponseStreamById(device: Device): StreamByIdResponse =
        StreamByIdResponse.newBuilder().apply {
            successBuilder.setDevice(device)
        }.build()

    private fun buildFailureResponseStreamById(exception: String, message: String): StreamByIdResponse =
        StreamByIdResponse.newBuilder().apply {
            failureBuilder.setMessage("Device find by id failed by $exception: $message")
        }.build()
}
