package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.dto.request.toEntity
import com.sushkevych.securitydevices.dto.response.UserResponse
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.exception.NotFoundException
import com.sushkevych.securitydevices.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun getUserById(userId: String): UserResponse = userRepository.getUserById(ObjectId(userId))
        ?.toResponse() ?: throw NotFoundException(message = "User with ID $userId not found")

    fun getAllUsers(): List<UserResponse> = userRepository.findAll().map { it.toResponse() }

    fun saveUser(userRequest: UserRequest): UserResponse {
        val user = userRequest.toEntity()
        userRepository.save(user)
        return user.toResponse()
    }

    fun updateUser(id: String, userRequest: UserRequest): UserResponse {
        val existingUser = getUserById(id)
        existingUser.let {
            val updatedUser = userRequest.toEntity().copy(id = ObjectId(it.id))
            userRepository.save(updatedUser)
            return updatedUser.toResponse()
        }
    }

    fun deleteUser(userId: String) = userRepository.deleteById(ObjectId(userId))

    fun findUsersWithoutDevices(): List<UserResponse> =
        userRepository.findUsersWithoutDevices().map { it.toResponse() }

    fun findsUsersWithSpecificDevice(deviceId: String): List<UserResponse> =
        userRepository.findUsersWithSpecificDevice(ObjectId(deviceId)).map { it.toResponse() }

    fun findUsersWithSpecificRole(role: String): List<UserResponse> =
        userRepository.findUsersWithSpecificRole(role).map { it.toResponse() }
}
