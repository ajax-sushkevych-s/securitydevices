package com.sushkevych.securitydevices.kafka

import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
import com.sushkevych.securitydevices.service.DeviceEventNatsService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver

@Component
class DeviceKafkaReceiver(
    private val deviceKafkaConsumer: KafkaReceiver<String, DeviceUpdatedEvent>,
    private val deviceEventNatsService: DeviceEventNatsService<DeviceUpdatedEvent>
) {

    @PostConstruct
    fun initialize() {
        deviceKafkaConsumer.receiveAutoAck()
            .flatMap { fluxRecord ->
                fluxRecord
                    .map {
                        deviceEventNatsService.publishEvent(it.value().device)
                    }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}
