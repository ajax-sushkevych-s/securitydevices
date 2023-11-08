package com.sushkevych.securitydevices.device.infrastructure.adapters.kafka.consumer

import com.sushkevych.securitydevices.device.infrastructure.adapters.nats.subscriber.DeviceEventNatsSubscriber
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver

@Component
class DeviceKafkaConsumer(
    private val deviceKafkaConsumer: KafkaReceiver<String, DeviceUpdatedEvent>,
    private val eventNatsSubscriber: DeviceEventNatsSubscriber<DeviceUpdatedEvent>
) {

    @PostConstruct
    fun initialize() {
        deviceKafkaConsumer.receiveAutoAck()
            .flatMap { fluxRecord ->
                fluxRecord
                    .flatMap {
                        eventNatsSubscriber.publishEvent(it.value().device)
                    }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}
