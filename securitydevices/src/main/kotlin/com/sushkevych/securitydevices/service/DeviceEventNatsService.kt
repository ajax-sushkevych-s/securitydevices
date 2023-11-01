package com.sushkevych.securitydevices.service

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import com.sushkevych.securitydevices.commonmodels.device.Device
import reactor.core.publisher.Flux

interface DeviceEventNatsService<EventT : GeneratedMessageV3> {

    val parser: Parser<EventT>

    fun handleEvent(deviceId: String, eventType: String): Flux<EventT>

    fun publishEvent(updatedDevice: Device)
}
