package com.sushkevych.securitydevices.device.infrastructure.adapters.kafka.configuration

import com.sushkevych.internalapi.DeviceEvent
import com.sushkevych.securitydevices.output.device.update.proto.DeviceUpdatedEvent
import com.sushkevych.securitydevices.core.infrastructure.configuration.kafka.CoreKafkaConfiguration
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.sender.KafkaSender

@Configuration
class KafkaConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") schemaRegistryUrl: String
) : CoreKafkaConfiguration(bootstrapServers, schemaRegistryUrl) {

    @Bean
    fun kafkaSenderDeviceUpdatedEvent(): KafkaSender<String, DeviceUpdatedEvent> =
        createKafkaSender(baseProducerProperties())

    @Bean
    fun kafkaReceiverDeviceUpdatedEvent(): KafkaReceiver<String, DeviceUpdatedEvent> {
        val customProperties: MutableMap<String, Any> = mutableMapOf(
            KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE to DeviceUpdatedEvent::class.java.name
        )
        return createKafkaReceiver(
            baseConsumerProperties(customProperties),
            DeviceEvent.createDeviceEventKafkaTopic(DeviceEvent.UPDATED),
            "device-group"
        )
    }
}
