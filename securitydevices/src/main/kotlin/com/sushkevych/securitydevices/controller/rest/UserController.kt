package com.sushkevych.securitydevices.controller.rest

import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.dto.response.CursorPaginateResponse
import com.sushkevych.securitydevices.dto.response.OffsetPaginateResponse
import com.sushkevych.securitydevices.dto.response.UserResponse
import com.sushkevych.securitydevices.model.MongoUser
import com.sushkevych.securitydevices.service.UserService
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
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: String): Mono<UserResponse> = userService.getUserById(userId)

    @GetMapping
    fun getAllUsers(): Mono<List<UserResponse>> = userService.findAllUsers()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody user: UserRequest): Mono<UserResponse> = userService.saveUser(user)

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    fun updateUser(@Valid @RequestBody user: UserRequest): Mono<UserResponse> = userService.updateUser(user)

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String): Mono<Unit> = userService.deleteUser(userId)

    @GetMapping("/no-devices")
    fun findUsersWithoutDevices(): Mono<List<UserResponse>> = userService.findUsersWithoutDevices()

    @GetMapping(params = ["deviceId"])
    fun findUsersWithSpecificDevice(@RequestParam("deviceId") deviceId: String): Mono<List<UserResponse>> =
        userService.findsUsersWithSpecificDevice(deviceId)

    @GetMapping(params = ["role"])
    fun findUsersWithSpecificRole(@RequestParam("role") role: MongoUser.MongoUserRole): Mono<List<UserResponse>> =
        userService.findUsersWithSpecificRole(role)

    @GetMapping(params = ["offset", "limit"], value = ["/offsetPagination"])
    fun getUsersByOffsetPagination(
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "50") limit: Int
    ): Mono<OffsetPaginateResponse> = userService.getUsersByOffsetPagination(offset, limit)

    @GetMapping("/cursorPagination")
    fun getUsersByCursorBasedPagination(
        @RequestParam(name = "pageSize", defaultValue = "50") pageSize: Int,
        @RequestParam(name = "cursor", required = false) cursor: String?
    ): Mono<CursorPaginateResponse> = userService.getUsersByCursorBasedPagination(pageSize, cursor)
}
