package com.sushkevych.securitydevices.kafka

import com.sushkevych.internalapi.DeviceEvent
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class DeviceKafkaProducer(
    private val kafkaSenderDeviceUpdatedEvent: KafkaSender<String, DeviceUpdatedEvent>
) {
    fun sendDeviceUpdatedEventToKafka(deviceProto: Device) {
        val deviceUpdatedEvent = DeviceUpdatedEvent.newBuilder().apply {
            device = deviceProto
        }.build()
        val senderRecord = SenderRecord.create(
            ProducerRecord(
                DeviceEvent.createDeviceEventSubject(deviceProto.id, DeviceEvent.UPDATED),
                deviceProto.id,
                deviceUpdatedEvent
            ),
            null
        )
        kafkaSenderDeviceUpdatedEvent.send(senderRecord.toMono()).subscribe()
    }
}
