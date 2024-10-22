package com.software.modsen.driverservice.service

import com.software.modsen.driverservice.exception.CarAlreadyExistException
import com.software.modsen.driverservice.exception.CarNotFoundException
import com.software.modsen.driverservice.model.Car
import com.software.modsen.driverservice.repository.CarRepository
import com.software.modsen.driverservice.util.ExceptionMessages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class CarService(
    private val carRepository: CarRepository
) {
    suspend fun getCarById(id: Long): Car = getByIdOrElseThrow(id)

    suspend fun getAllCars(): List<Car> = withContext(Dispatchers.IO) { carRepository.findAll() }

    suspend fun createCar(newCar: Car): Car = withContext(Dispatchers.IO) {
        checkCarExist(newCar.licensePlate)
        carRepository.save(newCar)
    }

    suspend fun updateCar(id: Long, updatedCar: Car): Car = withContext(Dispatchers.IO) {
        val carOptional: Car = getByIdOrElseThrow(id)
        validateCarUpdate(updatedCar, carOptional)
        updatedCar.carId = id
        carRepository.save(updatedCar)
    }

    suspend fun deleteCar(id: Long) = withContext(Dispatchers.IO) { carRepository.deleteById(id) }

    private suspend fun getByIdOrElseThrow(id: Long): Car = carRepository.findById(id)
        .orElseThrow { CarNotFoundException(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION.format(id)) }

    private suspend fun checkCarExist(licensePlate: String) {
        if (carRepository.existsByLicensePlate(licensePlate)) {
            throw CarAlreadyExistException(ExceptionMessages.CAR_ALREADY_EXIST.format(licensePlate))
        }
    }

    private suspend fun validateCarUpdate(updatedCar: Car, car: Car) {
        if (updatedCar.licensePlate != car.licensePlate) {
            checkCarExist(updatedCar.licensePlate)
        }
    }
}
