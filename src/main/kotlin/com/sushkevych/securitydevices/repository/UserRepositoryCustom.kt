package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoUser
import org.bson.types.ObjectId

interface UserRepositoryCustom {
    fun getUserById(id: ObjectId): MongoUser?

    fun findAll(): List<MongoUser>

    fun save(user: MongoUser): MongoUser?

    fun deleteById(id: ObjectId)

    fun getUserByUserName(username: String): MongoUser?

    fun findUsersWithSpecificDevice(deviceId: ObjectId): List<MongoUser>

    fun findUsersWithSpecificRole(role: String): List<MongoUser>

    fun findUsersWithoutDevices(): List<MongoUser>
}
