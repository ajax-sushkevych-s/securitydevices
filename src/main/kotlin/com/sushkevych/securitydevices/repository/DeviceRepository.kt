package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.Device
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@Suppress("EmptyClassBlock")
interface DeviceRepository : JpaRepository<Device, Long> {
}
