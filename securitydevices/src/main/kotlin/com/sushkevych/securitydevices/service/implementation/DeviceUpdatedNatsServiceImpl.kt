package com.sushkevych.securitydevices.service.implementation

import com.google.protobuf.Parser
import com.sushkevych.internalapi.DeviceEvent
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.mappers.mapToDeviceUpdatedEvent
import com.sushkevych.securitydevices.service.DeviceEventNatsService
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
import io.nats.client.Connection
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class DeviceUpdatedNatsServiceImpl(
    private val connection: Connection
) : DeviceEventNatsService<DeviceUpdatedEvent> {

    override val parser: Parser<DeviceUpdatedEvent> = DeviceUpdatedEvent.parser()

    override fun handleEvent(deviceId: String, eventType: String): Flux<DeviceUpdatedEvent> =
        Flux.create { sink ->
            connection.createDispatcher { message ->
                val parsedData = parser.parseFrom(message.data)
                sink.next(parsedData)
            }.apply { subscribe(DeviceEvent.createDeviceEventNatsSubject(deviceId, eventType)) }
        }

    override fun publishEvent(updatedDevice: Device) {
        val updateEventSubject = DeviceEvent.createDeviceEventNatsSubject(updatedDevice.id, DeviceEvent.UPDATED)
        val eventMessage = updatedDevice.mapToDeviceUpdatedEvent()

        connection.publish(updateEventSubject, eventMessage.toByteArray())
    }
}
