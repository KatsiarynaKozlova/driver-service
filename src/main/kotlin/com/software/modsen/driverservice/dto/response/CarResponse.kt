package com.software.modsen.driverservice.dto.response

data class CarResponse(
    val carId: Long?,
    val color: String,
    val model: String,
    val licensePlate: String,
    val year: Int,
)
