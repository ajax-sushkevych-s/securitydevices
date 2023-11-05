package com.sushkevych.securitydevices.user.application.port

import com.sushkevych.securitydevices.core.application.port.CoreService
import com.sushkevych.securitydevices.user.domain.User
import com.sushkevych.securitydevices.user.infrastructure.dto.response.CursorPaginateResponse
import com.sushkevych.securitydevices.user.infrastructure.dto.response.OffsetPaginateResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserService : CoreService<User, String> {
    fun findUsersWithoutDevices(): Flux<User>

    fun findsUsersWithSpecificDevice(deviceId: String): Flux<User>

    fun findUsersWithSpecificRole(role: User.UserRole): Flux<User>

    fun getUsersByOffsetPagination(offset: Int, limit: Int): Mono<Pair<List<User>, Long>>

    fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Mono<Pair<List<User>, Long>>
}
