package com.software.modsen.driverservice.service

import com.software.modsen.driverservice.dto.request.CarRequest
import com.software.modsen.driverservice.exception.CarAlreadyExistException
import com.software.modsen.driverservice.exception.CarNotFoundException
import com.software.modsen.driverservice.mapper.toCar
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

    fun createCar(newCar: Car): Car {
        checkCarExist(newCar.licensePlate)
        return carRepository.save(newCar)
    }

    fun updateCar(id: Long, updatedCar: Car): Car {
        val carOptional: Car = getByIdOrElseThrow(id)
        validateCarUpdate(updatedCar, carOptional)
        updatedCar.carId = id
        return carRepository.save(updatedCar)
    }

    fun deleteCar(id: Long) = carRepository.deleteById(id)

    private fun getByIdOrElseThrow(id: Long): Car = carRepository.findById(id)
        .orElseThrow { CarNotFoundException(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION.format(id)) }

    private fun checkCarExist(licensePlate: String) {
        if (carRepository.existsByLicensePlate(licensePlate)) {
            throw CarAlreadyExistException(ExceptionMessages.CAR_ALREADY_EXIST.format(licensePlate))
        }
    }

    private fun validateCarUpdate(updatedCar: Car, car: Car) {
        if (!updatedCar.licensePlate.equals(car.licensePlate)) {
            checkCarExist(updatedCar.licensePlate)
        }
    }
}
