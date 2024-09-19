package com.software.modsen.driverservice.service

import com.software.modsen.driverservice.dto.request.CarRequest
import com.software.modsen.driverservice.dto.request.toCar
import com.software.modsen.driverservice.dto.response.CarListResponse
import com.software.modsen.driverservice.dto.response.CarResponse
import com.software.modsen.driverservice.exception.CarAlreadyExistException
import com.software.modsen.driverservice.exception.CarNotFoundException
import com.software.modsen.driverservice.model.Car
import com.software.modsen.driverservice.model.toCarResponse
import com.software.modsen.driverservice.repository.CarRepository
import com.software.modsen.driverservice.util.ExceptionMessages
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class CarService(
    private val carRepository: CarRepository
) {
    fun getCarById(id: Long): CarResponse {
        return getByIdOrElseThrow(id).toCarResponse()
    }

    fun getAllCars(): CarListResponse {
        return CarListResponse(carRepository.findAll()
            .map { it.toCarResponse() })
    }

    fun createCar(carRequest: CarRequest): CarResponse {
        checkCarExist(carRequest.licensePlate)
        return carRepository.save(carRequest.toCar()).toCarResponse()
    }

    fun updateCar(id: Long, carRequest: CarRequest): CarResponse {
        val car_opt: Car = getByIdOrElseThrow(id)
        validateCarUpdate(carRequest, car_opt)
        val car: Car = carRequest.toCar()
        car.carId = id
        return carRepository.save(car).toCarResponse()
    }

    fun deleteCar(id: Long) {
        carRepository.deleteById(id);
    }

    private fun getByIdOrElseThrow(id: Long): Car {
        return carRepository.findById(id)
            .orElseThrow { CarNotFoundException(ExceptionMessages.CAR_NOT_FOUND_EXCEPTION.format(id)) }
    }

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
