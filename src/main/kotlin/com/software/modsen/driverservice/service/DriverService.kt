package com.software.modsen.driverservice.service

import com.software.modsen.driverservice.dto.request.DriverForRating
import com.software.modsen.driverservice.dto.request.DriverRequest
import com.software.modsen.driverservice.exception.CarNotFoundException
import com.software.modsen.driverservice.exception.DriverNotFoundException
import com.software.modsen.driverservice.exception.EmailAlreadyExistException
import com.software.modsen.driverservice.exception.PhoneAlreadyExistException
import com.software.modsen.driverservice.kafka.producer.DriverProducer
import com.software.modsen.driverservice.mapper.toDriver
import com.software.modsen.driverservice.model.Car
import com.software.modsen.driverservice.model.Driver
import com.software.modsen.driverservice.repository.CarRepository
import com.software.modsen.driverservice.repository.DriverRepository
import com.software.modsen.driverservice.util.ExceptionMessages
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class DriverService(
    private val driverRepository: DriverRepository,
    private val carRepository: CarRepository,
    private val driverProducer: DriverProducer
) {
    fun getDriverById(id: Long): Driver = getByIdOrElseThrow(id)

    fun getAllDrivers(): List<Driver> = driverRepository.findAll()

    fun createDriver(newDriver: Driver): Driver {
        preCreateValidateDriver(newDriver)
        newDriver.car = getCarByIdOrElseThrow(newDriver.car!!.carId!!)
        val driver: Driver = driverRepository.save(newDriver)
        val driverForRating = DriverForRating(driver.driverId!!)
        driverProducer.sendDriver(driverForRating)
        return driver
    }

    fun updateDriver(id: Long, updatedDriver: Driver): Driver {
        val driverOptional: Driver = getByIdOrElseThrow(id)
        preUpdateValidateCar(updatedDriver, driverOptional)
        updatedDriver.driverId = id
        return driverRepository.save(updatedDriver)
    }

    fun deleteDriver(id: Long) = driverRepository.deleteById(id)

    private fun getByIdOrElseThrow(id: Long): Driver = driverRepository.findById(id)
        .orElseThrow { DriverNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND_EXCEPTION.format(id)) }

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

    private fun preCreateValidateDriver(driver: Driver) {
        checkPhoneExist(driver.phone)
        checkEmailExist(driver.email)
    }

    private fun preUpdateValidateCar(driverRequest: Driver, driver: Driver) {
        if (!driver.email.equals(driverRequest.email)) {
            checkEmailExist(driverRequest.email)
        }
        if (!driver.phone.equals(driverRequest.phone)) {
            checkPhoneExist(driverRequest.phone)
        }
    }

    private fun getCarByIdOrElseThrow(id: Long): Car = carRepository.findById(id)
        .orElseThrow { CarNotFoundException(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION.format(id)) }
}
