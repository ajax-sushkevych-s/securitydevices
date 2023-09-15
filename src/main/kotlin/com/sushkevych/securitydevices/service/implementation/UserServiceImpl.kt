package com.sushkevych.securitydevices.service.implementation

import com.sushkevych.securitydevices.annotation.DeviceAuthorization
import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.dto.request.toEntity
import com.sushkevych.securitydevices.dto.response.UserResponse
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.exception.NotFoundException
import com.sushkevych.securitydevices.model.MongoUser
import com.sushkevych.securitydevices.repository.UserRepository
import com.sushkevych.securitydevices.service.UserService
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun getUserById(userId: String): UserResponse = userRepository.getUserById(ObjectId(userId))
        ?.toResponse() ?: throw NotFoundException(message = "User with ID $userId not found")

    override fun findAllUsers(): List<UserResponse> = userRepository.findAll().map { it.toResponse() }

    @DeviceAuthorization
    override fun saveUser(userRequest: UserRequest): UserResponse {
        val user = userRequest.toEntity()
        userRepository.save(user)
        return user.toResponse()
    }

    @DeviceAuthorization
    override fun updateUser(id: String, userRequest: UserRequest): UserResponse {
        val existingUser = getUserById(id)
        existingUser.let {
            val updatedUser = userRequest.toEntity().copy(id = ObjectId(it.id))
            userRepository.save(updatedUser)
            return updatedUser.toResponse()
        }
    }

    override fun deleteUser(userId: String) = userRepository.deleteById(ObjectId(userId))

    override fun findUsersWithoutDevices(): List<UserResponse> =
        userRepository.findUsersWithoutDevices().map { it.toResponse() }

    override fun findsUsersWithSpecificDevice(deviceId: String): List<UserResponse> =
        userRepository.findUsersWithSpecificDevice(ObjectId(deviceId)).map { it.toResponse() }

    override fun findUsersWithSpecificRole(role: MongoUser.MongoUserRole): List<UserResponse> =
        userRepository.findUsersWithSpecificRole(role).map { it.toResponse() }
}
