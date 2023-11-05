package com.sushkevych.securitydevices.device.application.port

import com.google.protobuf.GeneratedMessageV3
import com.sushkevych.securitydevices.commonmodels.device.Device
import com.sushkevych.securitydevices.core.infrastructure.adapters.nats.EventNatsSubscriber

interface DeviceEventNatsSubscriber<EventT : GeneratedMessageV3> : EventNatsSubscriber<EventT> {
    fun publishEvent(updatedDevice: Device)
}
