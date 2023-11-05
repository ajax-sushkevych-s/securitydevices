package com.sushkevych.securitydevices.core.infrastructure.adapters.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import reactor.core.publisher.Flux

interface EventNatsSubscriber<EventT : GeneratedMessageV3> {

    val parser: Parser<EventT>

    fun subscribeToEvents(deviceId: String, eventType: String): Flux<EventT>
}
