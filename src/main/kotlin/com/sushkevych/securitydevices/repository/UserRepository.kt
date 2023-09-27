package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoUser
import org.bson.types.ObjectId

interface UserRepository {
    fun getUserById(id: ObjectId): MongoUser?

    fun findAll(): List<MongoUser>

    fun save(user: MongoUser): MongoUser?

    fun deleteById(userId: ObjectId)

    fun getUserByUserName(username: String): MongoUser?

    fun findUsersWithSpecificDevice(deviceId: ObjectId): List<MongoUser>

    fun findUsersWithSpecificRole(role: MongoUser.MongoUserRole): List<MongoUser>

    fun findUsersWithoutDevices(): List<MongoUser>

    fun getUsersByOffsetPagination(offset: Int, limit: Int): Pair<List<MongoUser>, Long>

    fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Pair<List<MongoUser>, Long>
}
