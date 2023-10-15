package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoDevice
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DeviceRepository {
    fun getDeviceById(deviceId: ObjectId): Mono<MongoDevice>

    fun findAll(): Flux<MongoDevice>

    fun save(device: MongoDevice): Mono<MongoDevice>

    fun update(device: MongoDevice): Mono<MongoDevice>

    fun deleteById(deviceId: ObjectId): Mono<Unit>
}
