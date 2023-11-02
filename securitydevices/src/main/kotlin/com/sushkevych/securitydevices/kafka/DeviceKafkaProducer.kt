package com.sushkevych.securitydevices.kafka

import com.sushkevych.internalapi.DeviceEvent
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.mappers.mapToDeviceUpdatedEvent
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class DeviceKafkaProducer(
    private val kafkaSenderDeviceUpdatedEvent: KafkaSender<String, DeviceUpdatedEvent>
) {
    fun sendDeviceUpdatedEventToKafka(deviceProto: Device): Mono<Unit> =
        Mono.fromSupplier { deviceProto.mapToDeviceUpdatedEvent() }
            .flatMap {
                kafkaSenderDeviceUpdatedEvent.send(buildKafkaUpdatedMessage(it)).next()
            }
            .thenReturn(Unit)

    private fun buildKafkaUpdatedMessage(event: DeviceUpdatedEvent) =
        SenderRecord.create(
            ProducerRecord(
                DeviceEvent.createDeviceEventKafkaTopic(DeviceEvent.UPDATED),
                event.device.id,
                event
            ),
            null
        ).toMono()
}
