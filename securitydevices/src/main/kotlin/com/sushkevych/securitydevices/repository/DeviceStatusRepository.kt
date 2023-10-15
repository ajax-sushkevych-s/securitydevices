package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoDeviceStatus
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface DeviceStatusRepository : ReactiveMongoRepository<MongoDeviceStatus, ObjectId> {
    fun findByUserDeviceId(deviceId: String): Mono<MongoDeviceStatus>
}
