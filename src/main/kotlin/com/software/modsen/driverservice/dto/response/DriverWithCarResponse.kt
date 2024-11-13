package com.software.modsen.driverservice.dto.response

import com.software.modsen.driverservice.model.Sex

data class DriverWithCarResponse(
    val id: Long?,
    val name: String,
    val email: String,
    val phone: String,
    val sex: Sex,
    val car: CarResponse
)
