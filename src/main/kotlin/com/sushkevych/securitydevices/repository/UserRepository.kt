package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@Suppress("EmptyClassBlock")
interface UserRepository : JpaRepository<User, Long> {
}
