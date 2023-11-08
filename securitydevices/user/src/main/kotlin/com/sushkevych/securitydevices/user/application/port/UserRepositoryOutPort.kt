package com.sushkevych.securitydevices.user.application.port

import com.sushkevych.securitydevices.core.application.port.CoreRepository
import com.sushkevych.securitydevices.user.domain.User
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserRepositoryOutPort : CoreRepository<User, String> {
    fun getUserByUserName(username: String): Mono<User>

    fun findUsersWithSpecificDevice(deviceId: ObjectId): Flux<User>

    fun findUsersWithSpecificRole(role: User.UserRole): Flux<User>

    fun findUsersWithoutDevices(): Flux<User>

    fun getUsersByOffsetPagination(offset: Int, limit: Int): Mono<Pair<List<User>, Long>>

    fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Mono<Pair<List<User>, Long>>
}
