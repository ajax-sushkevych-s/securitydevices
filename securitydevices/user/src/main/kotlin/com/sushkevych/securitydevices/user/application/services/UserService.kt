package com.sushkevych.securitydevices.user.application.services

import com.sushkevych.securitydevices.core.application.exception.NotFoundException
import com.sushkevych.securitydevices.core.infrastructure.annotation.DeviceAuthorization
import com.sushkevych.securitydevices.user.application.port.UserRepositoryOutPort
import com.sushkevych.securitydevices.user.application.port.UserOperationsInPort
import com.sushkevych.securitydevices.user.domain.User
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserService(private val userRepository: UserRepositoryOutPort) : UserOperationsInPort {
    override fun getById(id: String): Mono<User> =
        userRepository.getById(id)
            .switchIfEmpty(Mono.error(NotFoundException(message = "User with ID $id not found")))

    override fun findAll(): Flux<User> = userRepository.findAll()

    @DeviceAuthorization
    override fun save(entity: User): Mono<User> = userRepository.save(entity)

    @DeviceAuthorization
    override fun update(entity: User): Mono<User> =
        userRepository.update(entity)
            .switchIfEmpty(Mono.error(NotFoundException(message = "User with ID ${entity.id} not found")))

    override fun delete(id: String) = userRepository.deleteById(id)

    override fun findUsersWithoutDevices(): Flux<User> = userRepository.findUsersWithoutDevices()

    override fun findsUsersWithSpecificDevice(deviceId: String): Flux<User> =
        userRepository.findUsersWithSpecificDevice(ObjectId(deviceId))

    override fun findUsersWithSpecificRole(role: User.UserRole): Flux<User> =
        userRepository.findUsersWithSpecificRole(role)

    override fun getUsersByOffsetPagination(offset: Int, limit: Int): Mono<Pair<List<User>, Long>> =
        userRepository.getUsersByOffsetPagination(offset, limit)

    override fun getUsersByCursorBasedPagination(pageSize: Int, cursor: String?): Mono<Pair<List<User>, Long>> =
        userRepository.getUsersByCursorBasedPagination(pageSize, cursor)
}
