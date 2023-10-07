package com.sushkevych.securitydevices

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.dto.response.toProtoDevice
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.model.MongoDevice
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
import com.sushkevych.securitydevices.repository.DeviceRepository
import com.sushkevych.securitydevices.request.device.create.proto.CreateDeviceRequest
import com.sushkevych.securitydevices.request.device.create.proto.CreateDeviceResponse
import com.sushkevych.securitydevices.request.device.delete.proto.DeleteDeviceRequest
import com.sushkevych.securitydevices.request.device.delete.proto.DeleteDeviceResponse
import com.sushkevych.securitydevices.request.device.get_all.proto.GetAllDevicesRequest
import com.sushkevych.securitydevices.request.device.get_all.proto.GetAllDevicesResponse
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceRequest
import com.sushkevych.securitydevices.request.device.get_by_id.proto.GetByIdDeviceResponse
import com.sushkevych.securitydevices.request.device.update.proto.UpdateDeviceRequest
import com.sushkevych.securitydevices.request.device.update.proto.UpdateDeviceResponse
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.remove
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.util.concurrent.CompletableFuture.runAsync

@SpringBootTest
@ActiveProfiles("local")
class NatsControllersTest {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @AfterEach
    fun cleanDB() {
        mongoTemplate.remove<MongoDevice>()
    }

    @Test
    fun `should return success response for get device by ID`() {
        // GIVEN
        val save = deviceRepository.save(
            MongoDevice(
                ObjectId(),
                name = "Test Device",
                description = "Test Description",
                type = "Test Type",
                attributes = emptyList()
            )
        )

        val deviceId = save?.id?.toHexString()

        val protoDevice = Device.newBuilder().apply {
            id = deviceId
            name = "Test Device"
            description = "Test Description"
            type = "Test Type"
            addAllAttributes(emptyList())
        }.build()

        val request = GetByIdDeviceRequest.newBuilder().setDeviceId(deviceId).build()

        val expectedResponse = GetByIdDeviceResponse.newBuilder().apply {
            successBuilder.setDevice(protoDevice)
        }.build()

        // WHEN
        val actual = doRequest(
            NatsSubject.DeviceRequest.GET_BY_ID,
            request,
            GetByIdDeviceResponse.parser()
        )

        // THEN
        assertThat(actual).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return success response for get all devices`() {
        // GIVEN
        val request = GetAllDevicesRequest.getDefaultInstance()

        val protoDeviceList = deviceRepository.findAll().map { it.toResponse().toProtoDevice() }

        val expectedResponse = GetAllDevicesResponse.newBuilder().apply {
            successBuilder.addAllDevices(protoDeviceList)
        }.build()

        // WHEN
        val actual = doRequest(
            NatsSubject.DeviceRequest.GET_ALL,
            request,
            GetAllDevicesResponse.parser()
        )

        // THEN
        assertThat(actual).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return success response for delete device`() {
        // GIVEN
        val save = deviceRepository.save(
            MongoDevice(
                id = ObjectId(),
                name = "Deleted Device",
                description = "Deleted Description",
                type = "Deleted Type",
                attributes = emptyList()
            )
        )

        val deviceId = save?.id?.toHexString()

        val request = DeleteDeviceRequest.newBuilder().apply {
            setDeviceId(deviceId)
        }.build()

        val expectedResponse = DeleteDeviceResponse.newBuilder().apply {
            successBuilder.build()
        }.build()

        // WHEN
        val actual = doRequest(
            NatsSubject.DeviceRequest.DELETE,
            request,
            DeleteDeviceResponse.parser()
        )

        // THEN
        assertThat(actual).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return success response for create device`() {
        // GIVEN
        val protoDevice = Device.newBuilder().apply {
            id = ObjectId().toHexString()
            name = "Created Device"
            description = "Created Description"
            type = "Created Type"
            addAllAttributes(emptyList())
        }.build()

        val request = CreateDeviceRequest.newBuilder().apply {
            setDevice(protoDevice)
        }.build()

        val expectedResponse = CreateDeviceResponse.newBuilder().apply {
            successBuilder.setDevice(protoDevice)
        }.build()

        // WHEN
        val actual = doRequest(
            NatsSubject.DeviceRequest.CREATE,
            request,
            CreateDeviceResponse.parser()
        )

        // THEN
        assertThat(actual).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return success response for update device`() {
        // GIVEN
        val save = deviceRepository.save(
            MongoDevice(
                id = ObjectId(),
                name = "Device",
                description = "Description",
                type = "Type",
                attributes = emptyList()
            )
        )

        val deviceId = save?.id?.toHexString()

        val updatedProtoDevice = Device.newBuilder().apply {
            name = "Updated Test Device"
            description = "Updated Test Description"
            type = "Updated Test Type"
            addAllAttributes(emptyList())
        }.build()

        val request = UpdateDeviceRequest.newBuilder().apply {
            setDeviceId(deviceId)
            setDevice(updatedProtoDevice)
        }.build()

        val expectedResponse = UpdateDeviceResponse.newBuilder().apply {
            successBuilder.setDevice(updatedProtoDevice.toBuilder().setId(deviceId).build())
        }.build()

        val expectedUpdatedEventMessage = DeviceUpdatedEvent.newBuilder()
            .setDeviceName(updatedProtoDevice.name)
            .setDeviceId(deviceId)
            .build()
            .toByteArray()

        val expectedUpdatedEventSubject = "${NatsSubject.DeviceEvent.UPDATED}.${
            updatedProtoDevice.name.replace(" ", "_")
                .lowercase()
        }"

        lateinit var actualUpdatedEventMessage : ByteArray

        // WHEN
        runAsync {
            actualUpdatedEventMessage = natsConnection.subscribe(expectedUpdatedEventSubject)
                .nextMessage(Duration.ofSeconds(10L))
                .data
        }

        val actual = doRequest(
            NatsSubject.DeviceRequest.UPDATE,
            request,
            UpdateDeviceResponse.parser()
        )

        // THEN
        assertThat(actualUpdatedEventMessage).isEqualTo(expectedUpdatedEventMessage)
        assertThat(actual).isEqualTo(expectedResponse)
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = natsConnection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(10L)
        )
        return parser.parseFrom(response.get().data)
    }
}
