package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.dto.response.CursorPaginateResponse
import com.sushkevych.securitydevices.dto.response.OffsetPaginateResponse
import com.sushkevych.securitydevices.dto.response.UserResponse
import com.sushkevych.securitydevices.model.MongoUser
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserService {
    fun getUserById(userId: String): Mono<UserResponse>

    fun findAllUsers(): Flux<UserResponse>

    fun saveUser(userRequest: UserRequest): Mono<UserResponse>

    fun updateUser(userRequest: UserRequest): Mono<UserResponse>

    fun deleteUser(userId: String): Mono<Unit>

    fun findUsersWithoutDevices(): Flux<UserResponse>

    fun findsUsersWithSpecificDevice(deviceId: String): Flux<UserResponse>

    fun findUsersWithSpecificRole(role: MongoUser.MongoUserRole): Flux<UserResponse>

    fun getUsersByOffsetPagination(offset: Int, limit: Int): Mono<OffsetPaginateResponse>

    fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Mono<CursorPaginateResponse>
}
