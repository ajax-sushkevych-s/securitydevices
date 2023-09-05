package com.sushkevych.securitydevices.repository

import com.sushkevych.securitydevices.model.DeviceAttributeValue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceAttributeValueRepository : JpaRepository<DeviceAttributeValue, Long>
