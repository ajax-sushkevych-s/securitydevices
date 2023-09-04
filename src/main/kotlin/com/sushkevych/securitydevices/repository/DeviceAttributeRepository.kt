package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.DeviceAttribute
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@Suppress("EmptyClassBlock")
interface DeviceAttributeRepository : JpaRepository<DeviceAttribute, Long> {
}
