package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.dto.response.CursorPaginateResponse
import com.sushkevych.securitydevices.dto.response.OffsetPaginateResponse
import com.sushkevych.securitydevices.dto.response.UserResponse
import com.sushkevych.securitydevices.model.MongoUser
import reactor.core.publisher.Mono

interface UserService {
    fun getUserById(userId: String): Mono<UserResponse>

    fun findAllUsers(): Mono<List<UserResponse>>

    fun saveUser(userRequest: UserRequest): Mono<UserResponse>

    fun updateUser(userRequest: UserRequest): Mono<UserResponse>

    fun deleteUser(userId: String): Mono<Unit>

    fun findUsersWithoutDevices(): Mono<List<UserResponse>>

    fun findsUsersWithSpecificDevice(deviceId: String): Mono<List<UserResponse>>

    fun findUsersWithSpecificRole(role: MongoUser.MongoUserRole): Mono<List<UserResponse>>

    fun getUsersByOffsetPagination(offset: Int, limit: Int): Mono<OffsetPaginateResponse>

    fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Mono<CursorPaginateResponse>
}
