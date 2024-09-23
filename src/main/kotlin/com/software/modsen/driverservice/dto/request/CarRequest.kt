package com.software.modsen.driverservice.dto.request

import com.software.modsen.driverservice.model.Car

data class CarRequest(
    val color: String,
    val model: String,
    val licensePlate: String,
    val year: Int,
)

fun CarRequest.toCar(): Car {
    return Car(
        carId = null,
        color = this.color,
        model = this.model,
        licensePlate = this.licensePlate,
        year = this.year
    )
}
