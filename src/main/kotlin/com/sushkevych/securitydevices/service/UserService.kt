package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.dto.request.UserDtoRequest
import com.sushkevych.securitydevices.dto.request.toEntity
import com.sushkevych.securitydevices.dto.response.UserDtoResponse
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.exception.NotFoundException
import com.sushkevych.securitydevices.model.MongoUser
import com.sushkevych.securitydevices.repository.UserRepositoryCustom
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepositoryCustom) {
    fun getUserById(userId: String): UserDtoResponse = userRepository.getUserById(ObjectId(userId))
        ?.toResponse() ?: throw NotFoundException(message = "User with ID $userId not found")

    fun getAllUsers(): List<UserDtoResponse> = userRepository.findAll().map { it.toResponse() }

    fun saveUser(userDtoRequest: UserDtoRequest): UserDtoResponse {
        val user = userDtoRequest.toEntity()
        userRepository.save(user)
        return user.toResponse()
    }

    fun updateUser(id: String, userDtoRequest: UserDtoRequest): UserDtoResponse {
        val existingUser = getUserById(id)
        existingUser.let {
            val updatedUser = userDtoRequest.toEntity().copy(id = ObjectId(it.id))
            userRepository.save(updatedUser)
            return updatedUser.toResponse()
        }
    }

    fun deleteUser(userId: String) = userRepository.deleteById(ObjectId(userId))

    fun findUsersWithoutDevices(): List<UserDtoResponse> =
        userRepository.findUsersWithoutDevices().map { it.toResponse() }

    fun findsUsersWithSpecificDevice(deviceId: String): List<UserDtoResponse> =
        userRepository.findUsersWithSpecificDevice(ObjectId(deviceId)).map { it.toResponse() }

    fun findUsersWithSpecificRole(role: String): List<UserDtoResponse> =
        userRepository.findUsersWithSpecificRole(role).map { it.toResponse() }
}
