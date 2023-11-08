package com.sushkevych.securitydevices.user.application.port

import com.sushkevych.securitydevices.user.domain.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserOperationsInPort {
    fun getById(id: String): Mono<User>

    fun findAll(): Flux<User>

    fun save(entity: User): Mono<User>

    fun update(entity: User): Mono<User>

    fun delete(id: String): Mono<Unit>

    fun findUsersWithoutDevices(): Flux<User>

    fun findsUsersWithSpecificDevice(deviceId: String): Flux<User>

    fun findUsersWithSpecificRole(role: User.UserRole): Flux<User>

    fun getUsersByOffsetPagination(offset: Int, limit: Int): Mono<Pair<List<User>, Long>>

    fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Mono<Pair<List<User>, Long>>
}
