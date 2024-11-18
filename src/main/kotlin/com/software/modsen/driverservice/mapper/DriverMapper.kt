package com.software.modsen.driverservice.mapper

import com.software.modsen.driverservice.dto.request.DriverRequest
import com.software.modsen.driverservice.dto.request.InitDriverRequest
import com.software.modsen.driverservice.dto.response.DriverResponse
import com.software.modsen.driverservice.dto.response.DriverWithCarResponse
import com.software.modsen.driverservice.model.Driver
import com.software.modsen.driverservice.model.Sex

fun Driver.toDriverResponse(): DriverResponse {
    return DriverResponse(
        id!!,
        name,
        email,
        phone,
        sex
    )
}

fun Driver.toDriverWithCarResponse(): DriverWithCarResponse {
    return DriverWithCarResponse(
        id!!,
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
        Sex.valueOf(sex),
        null
    )
}

fun InitDriverRequest.toDriver(): Driver {
    return Driver(
        null,
        name,
        email,
        phone,
        Sex.M,
        null
    )
}
