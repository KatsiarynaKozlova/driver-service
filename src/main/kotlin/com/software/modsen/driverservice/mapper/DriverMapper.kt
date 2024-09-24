package com.software.modsen.driverservice.mapper

import com.software.modsen.driverservice.dto.request.DriverRequest
import com.software.modsen.driverservice.dto.response.DriverResponse
import com.software.modsen.driverservice.dto.response.DriverWithCarResponse
import com.software.modsen.driverservice.model.Driver

fun Driver.toDriverResponse(): DriverResponse {
    return DriverResponse(
        driverId,
        name,
        email,
        phone,
        sex
    )
}

fun Driver.toDriverWithCarResponse(): DriverWithCarResponse {
    return DriverWithCarResponse(
        driverId,
        name,
        email,
        phone,
        sex,
        car!!.toCarResponse()
    )
}

fun DriverRequest.toDriver(): Driver {
    return Driver(
        null,
        name,
        email,
        phone,
        sex,
        null
    )
}
