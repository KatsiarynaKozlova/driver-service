package com.software.modsen.driverservice.dto.response

import com.software.modsen.driverservice.model.DriverSex

data class DriverResponse(
    val driverId: Long?,
    val name: String,
    val email: String,
    val phone: String,
    val sex: DriverSex
)
