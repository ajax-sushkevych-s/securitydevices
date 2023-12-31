package com.sushkevych.securitydevices.device.infrastructure.adapters.nats.subscriber

import com.google.protobuf.Parser
import com.sushkevych.internalapi.DeviceEvent
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.device.infrastructure.mapper.mapToDeviceUpdatedEvent
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class DeviceUpdatedNatsSubscriber(
    private val connection: Connection
) : DeviceEventNatsSubscriber<DeviceUpdatedEvent> {

    override val parser: Parser<DeviceUpdatedEvent> = DeviceUpdatedEvent.parser()

    private val dispatcher = connection.createDispatcher()

    override fun subscribeToEvents(deviceId: String, eventType: String): Flux<DeviceUpdatedEvent> =
        Flux.create { sink ->
            dispatcher.apply {
                subscribe(DeviceEvent.createDeviceEventNatsSubject(deviceId, eventType))
                { message ->
                    val parsedData = parser.parseFrom(message.data)
                    sink.next(parsedData)
                }
            }
        }

    override fun publishEvent(updatedDevice: Device): Mono<Unit> {
        val updateEventSubject = DeviceEvent.createDeviceEventNatsSubject(updatedDevice.id, DeviceEvent.UPDATED)
        val eventMessage = updatedDevice.mapToDeviceUpdatedEvent()

        return Mono.fromSupplier { connection.publish(updateEventSubject, eventMessage.toByteArray()) }
    }
}
