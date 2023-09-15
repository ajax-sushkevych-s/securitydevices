package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.dto.response.UserResponse
import com.sushkevych.securitydevices.model.MongoUser

interface UserService {
    fun getUserById(userId: String): UserResponse

    fun findAllUsers(): List<UserResponse>

    fun saveUser(userRequest: UserRequest): UserResponse

    fun updateUser(id: String, userRequest: UserRequest): UserResponse

    fun deleteUser(userId: String)

    fun findUsersWithoutDevices(): List<UserResponse>

    fun findsUsersWithSpecificDevice(deviceId: String): List<UserResponse>

    fun findUsersWithSpecificRole(role: MongoUser.MongoUserRole): List<UserResponse>
}
