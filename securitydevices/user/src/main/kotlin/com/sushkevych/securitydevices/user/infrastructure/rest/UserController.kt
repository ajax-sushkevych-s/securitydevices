package com.sushkevych.securitydevices.user.infrastructure.rest

import com.sushkevych.securitydevices.user.application.port.UserService
import com.sushkevych.securitydevices.user.infrastructure.dto.request.UserRequest
import com.sushkevych.securitydevices.user.infrastructure.dto.request.UserRoleRequest
import com.sushkevych.securitydevices.user.infrastructure.dto.response.CursorPaginateResponse
import com.sushkevych.securitydevices.user.infrastructure.dto.response.OffsetPaginateResponse
import com.sushkevych.securitydevices.user.infrastructure.dto.response.UserResponse
import com.sushkevych.securitydevices.user.infrastructure.mapper.toUser
import com.sushkevych.securitydevices.user.infrastructure.mapper.toUserResponse
import com.sushkevych.securitydevices.user.infrastructure.mapper.toUserRole
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: String): Mono<UserResponse> =
        userService.getById(userId).map { it.toUserResponse() }

    @GetMapping
    fun findAllUsers(): Flux<UserResponse> = userService.findAll().map { it.toUserResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody userRequest: UserRequest): Mono<UserResponse> =
        userService.save(userRequest.toUser()).map { it.toUserResponse() }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    fun updateUser(@Valid @RequestBody userRequest: UserRequest): Mono<UserResponse> =
        userService.update(userRequest.toUser()).map { it.toUserResponse() }

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String): Mono<Unit> = userService.delete(userId)

    @GetMapping("/no-devices")
    fun findUsersWithoutDevices(): Flux<UserResponse> =
        userService.findUsersWithoutDevices().map { it.toUserResponse() }

    @GetMapping(params = ["deviceId"])
    fun findUsersWithSpecificDevice(@RequestParam("deviceId") deviceId: String): Flux<UserResponse> =
        userService.findsUsersWithSpecificDevice(deviceId).map { it.toUserResponse() }

    @GetMapping(params = ["role"])
    fun findUsersWithSpecificRole(@RequestParam("role") role: UserRoleRequest): Flux<UserResponse> =
        userService.findUsersWithSpecificRole(role.toUserRole()).map { it.toUserResponse() }

    @GetMapping(params = ["offset", "limit"], value = ["/offsetPagination"])
    fun getUsersByOffsetPagination(
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "50") limit: Int
    ): Mono<OffsetPaginateResponse> = userService.getUsersByOffsetPagination(offset, limit)
        .flatMap { usersByOffsetPagination ->
            val mappedUsersToResponse = usersByOffsetPagination.first.map { it.toUserResponse() }
            val totalDocuments = usersByOffsetPagination.second
            OffsetPaginateResponse(mappedUsersToResponse, totalDocuments).toMono()
        }

    @GetMapping("/cursorPagination")
    fun getUsersByCursorBasedPagination(
        @RequestParam(name = "pageSize", defaultValue = "50") pageSize: Int,
        @RequestParam(name = "cursor", required = false) cursor: String?
    ): Mono<CursorPaginateResponse> = userService.getUsersByCursorBasedPagination(pageSize, cursor)
        .flatMap { usersByCursorBasedPagination ->
            val mappedUsersToResponse = usersByCursorBasedPagination.first.map { it.toUserResponse() }
            val totalDocuments = usersByCursorBasedPagination.second
            val nextCursor =
                if (mappedUsersToResponse.size == pageSize) mappedUsersToResponse.last().id.toString() else null
            CursorPaginateResponse(mappedUsersToResponse, nextCursor, totalDocuments).toMono()
        }
}
