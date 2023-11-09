package com.sushkevych.securitydevices.device.application.port

import com.sushkevych.securitydevices.device.domain.Device
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DeviceOperationsInPort {
    fun getById(id: String): Mono<Device>

    fun findAll(): Flux<Device>

    fun save(entity: Device): Mono<Device>

    fun update(entity: Device): Mono<Device>

    fun delete(id: String): Mono<Unit>
}
