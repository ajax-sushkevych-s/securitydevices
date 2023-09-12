package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoDevice
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository : MongoRepository<MongoDevice, ObjectId> {
    fun getDeviceById(deviceId: ObjectId): MongoDevice?
}
