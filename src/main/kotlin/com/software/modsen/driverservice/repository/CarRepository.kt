package com.software.modsen.driverservice.repository

import com.software.modsen.driverservice.model.Car
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CarRepository : JpaRepository<Car, Long> {
    fun existsByLicensePlate(licensePlate: String): Boolean
}
