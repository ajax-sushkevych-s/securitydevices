package com.sushkevych.securitydevices.controller

import com.sushkevych.securitydevices.dto.request.UserDtoRequest
import com.sushkevych.securitydevices.dto.response.UserDtoResponse
import com.sushkevych.securitydevices.model.User
import com.sushkevych.securitydevices.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: Long): User = userService.getUserById(userId)

    @GetMapping
    fun getAllUsers(): List<UserDtoResponse> = userService.getAllUsers()

    @PostMapping
    fun createUser(@Valid @RequestBody user: UserDtoRequest): ResponseEntity<UserDtoResponse> =
        ResponseEntity(
            userService.saveUser(user),
            HttpStatus.OK
        )

    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: Long,
        @Valid @RequestBody user: UserDtoRequest
    ): ResponseEntity<UserDtoResponse> =
        ResponseEntity(
            userService.updateDevice(userId, user),
            HttpStatus.OK
        )

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: Long) = userService.deleteUser(userId)
}
