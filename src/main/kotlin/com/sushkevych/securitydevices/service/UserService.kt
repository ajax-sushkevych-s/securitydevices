package com.sushkevych.securitydevices.service

import com.sushkevych.securitydevices.dto.request.UserDtoRequest
import com.sushkevych.securitydevices.dto.request.toEntity
import com.sushkevych.securitydevices.dto.response.UserDtoResponse
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.model.User
import com.sushkevych.securitydevices.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun getUserById(userId: Long): User = userRepository.getReferenceById(userId)
    fun getAllUsers(): List<UserDtoResponse> = userRepository.findAll().map { it.toResponse() }
    fun saveUser(userDtoRequest: UserDtoRequest): UserDtoResponse {
        val user = userDtoRequest.toEntity()
        userRepository.save(user)
        return user.toResponse()
    }

    fun updateDevice(id: Long, userDtoRequest: UserDtoRequest): UserDtoResponse {
        val user = userDtoRequest.toEntity()
        user.id = userRepository.getReferenceById(id).id
        userRepository.save(user)
        return user.toResponse()
    }

    fun deleteUser(userId: Long) = userRepository.deleteById(userId)
}
