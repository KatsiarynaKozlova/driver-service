package com.software.modsen.driverservice.service

import com.software.modsen.driverservice.dto.request.DriverRequest
import com.software.modsen.driverservice.dto.request.toDriver
import com.software.modsen.driverservice.dto.response.DriverListResponse
import com.software.modsen.driverservice.dto.response.DriverResponse
import com.software.modsen.driverservice.dto.response.DriverWithCarResponse
import com.software.modsen.driverservice.exception.CarNotFoundException
import com.software.modsen.driverservice.exception.DriverNotFoundException
import com.software.modsen.driverservice.exception.EmailAlreadyExistException
import com.software.modsen.driverservice.exception.PhoneAlreadyExistException
import com.software.modsen.driverservice.model.Car
import com.software.modsen.driverservice.model.Driver
import com.software.modsen.driverservice.model.toDriverResponse
import com.software.modsen.driverservice.model.toDriverWithCarResponse
import com.software.modsen.driverservice.repository.CarRepository
import com.software.modsen.driverservice.repository.DriverRepository
import com.software.modsen.driverservice.util.ExceptionMessages
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class DriverService(
    private val driverRepository: DriverRepository,
    private val carRepository: CarRepository
) {
    fun getDriverById(id: Long): DriverWithCarResponse {
        return getByIdOrElseThrow(id).toDriverWithCarResponse()
    }

    fun getAllDrivers(): DriverListResponse {
        return DriverListResponse(driverRepository.findAll()
            .map { it.toDriverResponse() })
    }

    fun createDriver(driverRequest: DriverRequest): DriverResponse {
        preCreateValidateDriver(driverRequest)
        val driver: Driver = driverRequest.toDriver()
        driver.car = getCarByIdOrElseThrow(driverRequest.carId)
        return driverRepository.save(driver).toDriverResponse()
    }

    fun updateDriver(id: Long, driverRequest: DriverRequest): DriverResponse {
        val driverOptional: Driver = getByIdOrElseThrow(id)
        preUpdateValidateCar(driverRequest, driverOptional)
        val driver: Driver = driverRequest.toDriver()
        driver.driverId = id
        return driverRepository.save(driver).toDriverResponse()
    }

    fun deleteDriver(id: Long) {
        driverRepository.deleteById(id)
    }

    private fun getByIdOrElseThrow(id: Long): Driver {
        return driverRepository.findById(id)
            .orElseThrow { DriverNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND_EXCEPTION.format(id)) }
    }

    private fun checkEmailExist(email: String) {
        if (driverRepository.existsByEmail(email)) {
            throw EmailAlreadyExistException(ExceptionMessages.DRIVER_WITH_EMAIL_ALREADY_EXIST_EXCEPTION.format(email))
        }
    }

    private fun checkPhoneExist(phone: String) {
        if (driverRepository.existsByPhone(phone)) {
            throw PhoneAlreadyExistException(ExceptionMessages.DRIVER_WITH_PHONE_ALREADY_EXIST_EXCEPTION.format(phone))
        }
    }

    private fun preCreateValidateDriver(driverRequest: DriverRequest) {
        checkPhoneExist(driverRequest.phone)
        checkEmailExist(driverRequest.email)
    }

    private fun preUpdateValidateCar(driverRequest: DriverRequest, driver: Driver) {
        if (!driver.email.equals(driverRequest.email)) {
            checkEmailExist(driverRequest.email)
        }
        if (!driver.phone.equals(driverRequest.phone)) {
            checkPhoneExist(driverRequest.phone)
        }
    }

    private fun getCarByIdOrElseThrow(id: Long): Car {
        return carRepository.findById(id)
            .orElseThrow { CarNotFoundException(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION.format(id)) }
    }
}
