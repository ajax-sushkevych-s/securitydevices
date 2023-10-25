package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoDevice
import org.bson.types.ObjectId
import reactor.core.publisher.Mono

interface DeviceCacheableRepository {
    fun getDeviceById(deviceId: ObjectId): Mono<MongoDevice>

    fun findAll(): Mono<List<MongoDevice>>

    fun save(device: MongoDevice): Mono<MongoDevice>

    fun update(device: MongoDevice): Mono<MongoDevice>

    fun deleteById(deviceId: ObjectId): Mono<Unit>
}
