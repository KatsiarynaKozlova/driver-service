package com.software.modsen.driverservice.service

import com.software.modsen.driverservice.dto.request.DriverForRating
import com.software.modsen.driverservice.exception.CarNotFoundException
import com.software.modsen.driverservice.exception.DriverNotFoundException
import com.software.modsen.driverservice.exception.EmailAlreadyExistException
import com.software.modsen.driverservice.exception.PhoneAlreadyExistException
import com.software.modsen.driverservice.kafka.producer.DriverProducer
import com.software.modsen.driverservice.model.Car
import com.software.modsen.driverservice.model.Driver
import com.software.modsen.driverservice.repository.CarRepository
import com.software.modsen.driverservice.repository.DriverRepository
import com.software.modsen.driverservice.util.ExceptionMessages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class DriverService(
    private val driverRepository: DriverRepository,
    private val carRepository: CarRepository,
    private val driverProducer: DriverProducer
) {
    suspend fun getDriverById(id: Long): Driver = withContext(Dispatchers.IO) { getByIdOrElseThrow(id) }

    suspend fun getAllDrivers(): List<Driver> = withContext(Dispatchers.IO) { driverRepository.findAll() }

    suspend fun createDriver(carId: Long, newDriver: Driver): Driver = withContext(Dispatchers.IO) {
        preCreateValidateDriver(newDriver)
        newDriver.car = getCarByIdOrElseThrow(carId)
        val driver = driverRepository.save(newDriver)
        coroutineScope {
            launch {
                val driverForRating = DriverForRating(driver.driverId!!)
                driverProducer.sendDriver(driverForRating)
            }
        }
        driver
    }

    suspend fun updateDriver(id: Long, updatedDriver: Driver): Driver = withContext(Dispatchers.IO) {
        val driverOptional: Driver = getByIdOrElseThrow(id)
        preUpdateValidateCar(updatedDriver, driverOptional)
        updatedDriver.driverId = id
        driverRepository.save(updatedDriver)
    }

    suspend fun deleteDriver(id: Long) = withContext(Dispatchers.IO) { driverRepository.deleteById(id) }

    private suspend fun getByIdOrElseThrow(id: Long): Driver = driverRepository.findById(id)
        .orElseThrow { DriverNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND_EXCEPTION.format(id)) }

    private suspend fun checkEmailExist(email: String) {
        if (driverRepository.existsByEmail(email)) {
            throw EmailAlreadyExistException(ExceptionMessages.DRIVER_WITH_EMAIL_ALREADY_EXIST_EXCEPTION.format(email))
        }
    }

    private suspend fun checkPhoneExist(phone: String) {
        if (driverRepository.existsByPhone(phone)) {
            throw PhoneAlreadyExistException(ExceptionMessages.DRIVER_WITH_PHONE_ALREADY_EXIST_EXCEPTION.format(phone))
        }
    }

    private suspend fun preCreateValidateDriver(driver: Driver) {
        checkPhoneExist(driver.phone)
        checkEmailExist(driver.email)
    }

    private suspend fun preUpdateValidateCar(driverRequest: Driver, driver: Driver) {
        if (driver.email != driverRequest.email) {
            checkEmailExist(driverRequest.email)
        }
        if (driver.phone != driverRequest.phone) {
            checkPhoneExist(driverRequest.phone)
        }
    }

    private suspend fun getCarByIdOrElseThrow(id: Long): Car = carRepository.findById(id)
        .orElseThrow { CarNotFoundException(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION.format(id)) }
}
