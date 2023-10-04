package com.sushkevych.securitydevices

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import com.sushkevych.internalapi.NatsSubject
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.commonmodels.device.DeviceList
import com.sushkevych.securitydevices.dto.response.toProtoDevice
import com.sushkevych.securitydevices.dto.response.toResponse
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
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration

@SpringBootTest
@ActiveProfiles("local")
class NatsControllersTest {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    private val protoDevice = Device.newBuilder().apply {
        setId("651be935ab3c731f6cc49364")
        setName("Test Device")
        setDescription("Test Description")
        setType("Test Type")
        addAllAttributes(emptyList())
    }.build()

    @Test
    fun `should return success response for get device by ID`() {
        // given
        val deviceId = protoDevice.id
        val request = GetByIdDeviceRequest.newBuilder().setDeviceId(deviceId).build()

        val expectedResponse = GetByIdDeviceResponse.newBuilder().apply {
            successBuilder.setDevice(protoDevice)
        }.build()

        // when
        val actual = doRequest(
            NatsSubject.Device.GET_BY_ID,
            request,
            GetByIdDeviceResponse.parser()
        )

        // then
        assertThat(actual).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return success response for get all devices`() {
        // given
        val request = GetAllDevicesRequest.newBuilder().build()

        val protoDeviceList = deviceRepository.findAll().map { it.toResponse().toProtoDevice() }

        val expectedResponse = GetAllDevicesResponse.newBuilder().apply {
            successBuilder.setDevices(
                DeviceList.newBuilder().addAllDevices(protoDeviceList)
            )
        }.build()

        // when
        val actual = doRequest(
            NatsSubject.Device.GET_ALL,
            request,
            GetAllDevicesResponse.parser()
        )

        // then
        assertThat(actual).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return success response for delete device`() {
        // given
        val request = DeleteDeviceRequest.newBuilder().apply {
            setDeviceId(protoDevice.id)
        }.build()

        val expectedResponse = DeleteDeviceResponse.newBuilder().apply {
            successBuilder.build()
        }.build()

        // when
        val actual = doRequest(
            NatsSubject.Device.DELETE,
            request,
            DeleteDeviceResponse.parser()
        )

        // then
        assertThat(actual).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return success response for create device`() {
        // given
        val request = CreateDeviceRequest.newBuilder().apply {
            setDevice(protoDevice)
        }.build()

        val expectedResponse = CreateDeviceResponse.newBuilder().apply {
            successBuilder.setDevice(protoDevice)
        }.build()

        // when
        val actual = doRequest(
            NatsSubject.Device.CREATE,
            request,
            CreateDeviceResponse.parser()
        )

        // then
        assertThat(actual).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return success response for update device`() {
        // given
        val updatedProtoDevice = protoDevice.toBuilder().apply {
            setName("Updated Test Device")
            setDescription("Updated Test Description")
            setType("Updated Test Type")
        }.build()
        val request = UpdateDeviceRequest.newBuilder().apply {
            setDeviceId(protoDevice.id)
            setDevice(updatedProtoDevice)
        }.build()

        val expectedResponse = UpdateDeviceResponse.newBuilder().apply {
            successBuilder.setDevice(updatedProtoDevice)
        }.build()

        // when
        val actual = doRequest(
            NatsSubject.Device.UPDATE,
            request,
            UpdateDeviceResponse.parser()
        )

        // then
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
