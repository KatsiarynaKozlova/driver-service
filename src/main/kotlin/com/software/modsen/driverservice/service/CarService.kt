package com.software.modsen.driverservice.service

import com.software.modsen.driverservice.dto.request.CarRequest
import com.software.modsen.driverservice.dto.response.CarListResponse
import com.software.modsen.driverservice.dto.response.CarResponse
import com.software.modsen.driverservice.exception.CarAlreadyExistException
import com.software.modsen.driverservice.exception.CarNotFoundException
import com.software.modsen.driverservice.mapper.toCar
import com.software.modsen.driverservice.mapper.toCarResponse
import com.software.modsen.driverservice.model.Car
import com.software.modsen.driverservice.repository.CarRepository
import com.software.modsen.driverservice.util.ExceptionMessages
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class CarService(
    private val carRepository: CarRepository
) {
    fun getCarById(id: Long): Car = getByIdOrElseThrow(id)

    fun getAllCars(): List<Car> = carRepository.findAll()

    fun createCar(carRequest: CarRequest): Car {
        checkCarExist(carRequest.licensePlate)
        return carRepository.save(carRequest.toCar())
    }

    fun updateCar(id: Long, carRequest: CarRequest): Car {
        val carOptional: Car = getByIdOrElseThrow(id)
        validateCarUpdate(carRequest, carOptional)
        val car: Car = carRequest.toCar()
        car.carId = id
        return carRepository.save(car)
    }

    fun deleteCar(id: Long) = carRepository.deleteById(id)

    private fun getByIdOrElseThrow(id: Long): Car = carRepository.findById(id)
        .orElseThrow { CarNotFoundException(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION.format(id)) }

    private fun checkCarExist(licensePlate: String) {
        if (carRepository.existsByLicensePlate(licensePlate)) {
            throw CarAlreadyExistException(ExceptionMessages.CAR_ALREADY_EXIST.format(licensePlate))
        }
    }

    private fun validateCarUpdate(carRequest: CarRequest, car: Car) {
        if (!carRequest.licensePlate.equals(car.licensePlate)) {
            checkCarExist(carRequest.licensePlate)
        }
    }
}
