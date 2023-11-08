package com.sushkevych.securitydevices.device.application.port

import com.sushkevych.securitydevices.device.domain.Device
import reactor.core.publisher.Mono

interface DeviceEventProducerOutPort {
    fun sendDeviceUpdatedEvent(device: Device): Mono<Unit>
}
