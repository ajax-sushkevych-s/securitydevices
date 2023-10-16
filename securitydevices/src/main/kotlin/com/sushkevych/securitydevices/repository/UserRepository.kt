package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.MongoUser
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Suppress("TooManyFunctions")
interface UserRepository {
    fun getUserById(id: ObjectId): Mono<MongoUser>

    fun findAll(): Flux<MongoUser>

    fun save(user: MongoUser): Mono<MongoUser>

    fun update(user: MongoUser): Mono<MongoUser>

    fun deleteById(userId: ObjectId): Mono<Unit>

    fun getUserByUserName(username: String): Mono<MongoUser>

    fun findUsersWithSpecificDevice(deviceId: ObjectId): Flux<MongoUser>

    fun findUsersWithSpecificRole(role: MongoUser.MongoUserRole): Flux<MongoUser>

    fun findUsersWithoutDevices(): Flux<MongoUser>

    fun getUsersByOffsetPagination(offset: Int, limit: Int): Mono<Pair<List<MongoUser>, Long>>

    fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Mono<Pair<List<MongoUser>, Long>>
}
