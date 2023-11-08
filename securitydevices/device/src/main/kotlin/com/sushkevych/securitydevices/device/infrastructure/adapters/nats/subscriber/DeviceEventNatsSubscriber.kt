package com.sushkevych.securitydevices.device.infrastructure.adapters.nats.subscriber

import com.google.protobuf.GeneratedMessageV3
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.core.infrastructure.adapters.nats.EventNatsSubscriber
import reactor.core.publisher.Mono

interface DeviceEventNatsSubscriber<EventT : GeneratedMessageV3> : EventNatsSubscriber<EventT> {
    fun publishEvent(updatedDevice: Device) : Mono<Unit>
}
