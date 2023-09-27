package com.sushkevych.securitydevices.controller

import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.dto.response.CursorPaginateResponse
import com.sushkevych.securitydevices.dto.response.OffsetPaginateResponse
import com.sushkevych.securitydevices.dto.response.UserResponse
import com.sushkevych.securitydevices.model.MongoUser
import com.sushkevych.securitydevices.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: String): UserResponse = userService.getUserById(userId)

    @GetMapping
    fun getAllUsers(): List<UserResponse> = userService.findAllUsers()

    @PostMapping
    fun createUser(@Valid @RequestBody user: UserRequest): ResponseEntity<UserResponse> =
        ResponseEntity(
            userService.saveUser(user),
            HttpStatus.OK
        )

    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: String,
        @Valid @RequestBody user: UserRequest
    ): ResponseEntity<UserResponse> =
        ResponseEntity(
            userService.updateUser(userId, user),
            HttpStatus.OK
        )

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String) = userService.deleteUser(userId)

    @GetMapping("/no-devices")
    fun findUsersWithoutDevices(): List<UserResponse> = userService.findUsersWithoutDevices()

    @GetMapping(params = ["deviceId"])
    fun findUsersWithSpecificDevice(@RequestParam("deviceId") deviceId: String): List<UserResponse> =
        userService.findsUsersWithSpecificDevice(deviceId)

    @GetMapping(params = ["role"])
    fun findUsersWithSpecificRole(@RequestParam("role") role: MongoUser.MongoUserRole): List<UserResponse> =
        userService.findUsersWithSpecificRole(role)

    @GetMapping(params = ["offset", "limit"], value = ["/offsetPagination"])
    fun getUsersByOffsetPagination(
        @RequestParam offset: Int,
        @RequestParam limit: Int
    ): OffsetPaginateResponse = userService.getUsersByOffsetPagination(offset, limit)

    @GetMapping("/cursorPagination")
    fun getUsersByCursorBasedPagination(
        @RequestParam(name = "pageSize") pageSize: Int,
        @RequestParam(name = "cursor", required = false) cursor: String?
    ): CursorPaginateResponse = userService.getUsersByCursorBasedPagination(pageSize, cursor)
}
