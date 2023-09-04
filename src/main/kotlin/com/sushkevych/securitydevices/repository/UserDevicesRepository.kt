package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.UserDevices
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@Suppress("EmptyClassBlock")
interface UserDevicesRepository : JpaRepository<UserDevices, Long> {
}
