package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoDevice
import org.bson.types.ObjectId

interface DeviceRepository {
    fun getDeviceById(deviceId: ObjectId): MongoDevice?

    fun findAll(): List<MongoDevice>

    fun save(device: MongoDevice): MongoDevice?

    fun deleteById(deviceId: ObjectId)
}
