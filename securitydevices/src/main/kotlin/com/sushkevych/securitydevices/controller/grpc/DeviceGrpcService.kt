package com.sushkevych.securitydevices.controller.grpc

import com.sushkevych.internalapi.DeviceEvent
import com.sushkevych.securitydevices.ReactorDeviceServiceGrpc
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.service.DeviceEventNatsService
import com.sushkevych.securitydevices.dto.response.toProtoDevice
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
import com.sushkevych.securitydevices.request.device.get_all.proto.GetAllDevicesRequest
import com.sushkevych.securitydevices.request.device.get_all.proto.GetAllDevicesResponse
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceRequest
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceResponse
import com.sushkevych.securitydevices.request.device.get_by_id_updated.proto.GetByIdUpdatedRequest
import com.sushkevych.securitydevices.request.device.get_by_id_updated.proto.GetByIdUpdatedResponse
import com.sushkevych.securitydevices.service.DeviceService
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@GrpcService
@Suppress("TooManyFunctions")
class DeviceGrpcService(
    private val deviceService: DeviceService,
    private val deviceEventNatsService: DeviceEventNatsService<DeviceUpdatedEvent>
) : ReactorDeviceServiceGrpc.DeviceServiceImplBase() {

    override fun getAll(request: Mono<GetAllDevicesRequest>): Mono<GetAllDevicesResponse> =
        request.flatMap { handleGetAll(it) }

    override fun getById(request: Mono<GetByIdDeviceRequest>): Mono<GetByIdDeviceResponse> =
        request.flatMap { handleGetById(it) }

    override fun getByIdUpdated(request: Mono<GetByIdUpdatedRequest>): Flux<GetByIdUpdatedResponse> =
        request.flatMapMany { handleGetByIdUpdated(it) }

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
    private fun handleGetAll(request: GetAllDevicesRequest): Mono<GetAllDevicesResponse> =
        deviceService.getAllDevices()
            .collectList()
            .map { devices -> buildSuccessResponseGetAll(devices.map { it.toProtoDevice() }) }
            .onErrorResume { exception ->
                buildFailureResponseGetAll(
                    exception.javaClass.simpleName,
                    exception.toString()
                ).toMono()
            }

    private fun handleGetByIdUpdated(request: GetByIdUpdatedRequest): Flux<GetByIdUpdatedResponse> =
        deviceEventNatsService.handleEvent(request.deviceId, DeviceEvent.UPDATED)
            .map { buildSuccessResponseGetByIdEvent(it.device) }
            .onErrorResume { exception ->
                buildFailureResponseGetByIdEvent(
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

    private fun buildSuccessResponseGetByIdEvent(device: Device): GetByIdUpdatedResponse =
        GetByIdUpdatedResponse.newBuilder().apply {
            successBuilder.setDevice(device)
        }.build()

    private fun buildFailureResponseGetByIdEvent(exception: String, message: String): GetByIdUpdatedResponse =
        GetByIdUpdatedResponse.newBuilder().apply {
            failureBuilder.setMessage("Device find by id failed by $exception: $message")
        }.build()
}
