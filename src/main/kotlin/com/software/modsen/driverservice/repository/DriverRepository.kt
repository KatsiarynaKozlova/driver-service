package com.software.modsen.driverservice.repository

import com.software.modsen.driverservice.model.Driver
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DriverRepository : JpaRepository<Driver, Long> {
    fun existsByEmail(email: String): Boolean
    fun existsByPhone(phone: String): Boolean
}
