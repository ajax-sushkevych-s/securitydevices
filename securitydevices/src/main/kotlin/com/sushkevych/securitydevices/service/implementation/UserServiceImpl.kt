package com.sushkevych.securitydevices.service.implementation

import com.sushkevych.securitydevices.annotation.DeviceAuthorization
import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.dto.request.toEntity
import com.sushkevych.securitydevices.dto.response.CursorPaginateResponse
import com.sushkevych.securitydevices.dto.response.OffsetPaginateResponse
import com.sushkevych.securitydevices.dto.response.UserResponse
import com.sushkevych.securitydevices.dto.response.toResponse
import com.sushkevych.securitydevices.exception.NotFoundException
import com.sushkevych.securitydevices.model.MongoUser
import com.sushkevych.securitydevices.repository.UserRepository
import com.sushkevych.securitydevices.service.UserService
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun getUserById(userId: String): Mono<UserResponse> =
        userRepository.getUserById(ObjectId(userId))
            .switchIfEmpty(Mono.error(NotFoundException(message = "User with ID $userId not found")))
            .map { it.toResponse() }

    override fun findAllUsers(): Flux<UserResponse> =
        userRepository.findAll()
            .map { it.toResponse() }

    @DeviceAuthorization
    override fun saveUser(userRequest: UserRequest): Mono<UserResponse> =
        userRepository.save(userRequest.toEntity())
            .map { it.toResponse() }

    @DeviceAuthorization
    override fun updateUser(userRequest: UserRequest): Mono<UserResponse> =
        userRepository.update(userRequest.toEntity())
            .switchIfEmpty(Mono.error(NotFoundException(message = "User with ID ${userRequest.id} not found")))
            .map { it.toResponse() }

    override fun deleteUser(userId: String) = userRepository.deleteById(ObjectId(userId))

    override fun findUsersWithoutDevices(): Flux<UserResponse> =
        userRepository.findUsersWithoutDevices()
            .map { it.toResponse() }

    override fun findsUsersWithSpecificDevice(deviceId: String): Flux<UserResponse> =
        userRepository.findUsersWithSpecificDevice(ObjectId(deviceId))
            .map { it.toResponse() }

    override fun findUsersWithSpecificRole(role: MongoUser.MongoUserRole): Flux<UserResponse> =
        userRepository.findUsersWithSpecificRole(role)
            .map { it.toResponse() }

    override fun getUsersByOffsetPagination(offset: Int, limit: Int): Mono<OffsetPaginateResponse> =
        userRepository.getUsersByOffsetPagination(offset, limit)
            .flatMap { usersByOffsetPagination ->
                val mappedUsersToResponse = usersByOffsetPagination.first.map { it.toResponse() }
                val totalDocuments = usersByOffsetPagination.second
                OffsetPaginateResponse(mappedUsersToResponse, totalDocuments).toMono()
            }

    override fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Mono<CursorPaginateResponse> =
        userRepository.getUsersByCursorBasedPagination(pageSize, cursor)
            .flatMap { usersByCursorBasedPagination ->
                val mappedUsersToResponse = usersByCursorBasedPagination.first.map { it.toResponse() }
                val totalDocuments = usersByCursorBasedPagination.second
                val nextCursor =
                    if (mappedUsersToResponse.size == pageSize) mappedUsersToResponse.last().id.toString() else null
                CursorPaginateResponse(mappedUsersToResponse, nextCursor, totalDocuments).toMono()
            }
}
