package com.software.modsen.driverservice.util

object ExceptionMessages {
    const val DRIVER_NOT_FOUND_EXCEPTION: String = "driver with id '%s' not found"
    const val CAR_NOT_FOUND_EXCEPTION: String = "car with id '%s' not found"
    const val DRIVER_WITH_EMAIL_ALREADY_EXIST_EXCEPTION: String =
        "driver with email '%s' already exists"
    const val DRIVER_WITH_PHONE_ALREADY_EXIST_EXCEPTION: String =
        "driver with phone '%s' already exists"
    const val CAR_ALREADY_EXIST: String = "car with number '%s' already exists"
    const val SERVICE_UNAVAILABLE = "Service unavailable. Try again later"
}
