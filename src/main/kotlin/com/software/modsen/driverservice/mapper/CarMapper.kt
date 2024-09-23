package com.software.modsen.driverservice.mapper

import com.software.modsen.driverservice.dto.request.CarRequest
import com.software.modsen.driverservice.dto.response.CarResponse
import com.software.modsen.driverservice.model.Car

fun Car.toCarResponse(): CarResponse {
    return CarResponse(
        carId,
        color,
        model,
        licensePlate,
        year
    )
}

fun CarRequest.toCar(): Car {
    return Car(
        null,
        color,
        model,
        licensePlate,
        year
    )
}
