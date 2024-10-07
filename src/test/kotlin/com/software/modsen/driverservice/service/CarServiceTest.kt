package com.software.modsen.driverservice.service

import com.software.modsen.driverservice.exception.CarAlreadyExistException
import com.software.modsen.driverservice.exception.CarNotFoundException
import com.software.modsen.driverservice.model.Car
import com.software.modsen.driverservice.repository.CarRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
class CarServiceTest {
    @MockK
    private lateinit var carRepository: CarRepository

    @InjectMockKs
    private lateinit var carService: CarService

    @Test
    fun `should return car by id`() {
        val expectedCar = getDefaultCar()
        every { carRepository.findById(any()) } returns Optional.of(expectedCar)

        val result = carService.getCarById(DEFAULT_ID)

        assertEquals(expectedCar, result)
        verify {
            carRepository.findById(DEFAULT_ID)
        }
    }

    @Test
    fun `should return car not found exception on get by id`() {
        every { carRepository.findById(any()) } returns Optional.empty()
        assertThrows<CarNotFoundException> {
            carService.getCarById(DEFAULT_ID)
        }
    }

    @Test
    fun `should return list of cars`() {
        every { carRepository.findAll() } returns listOf(getDefaultCar())

        val result = carService.getAllCars()

        assertNotNull(result)
        assertEquals(1, result.size)
    }

    @Test
    fun `should return new car`() {
        val expectedCar = getDefaultCar()
        every { carRepository.existsByLicensePlate(any()) } returns false
        every { carRepository.save(any()) } returns expectedCar

        val result = carService.createCar(expectedCar)

        assertEquals(expectedCar, result)

        verify {
            carRepository.existsByLicensePlate(expectedCar.licensePlate)
            carRepository.save(expectedCar)
        }
    }

    @Test
    fun `should return car already exist exception on create car`() {
        val expectedCar = getDefaultCar()
        every { carRepository.existsByLicensePlate(any()) } returns true

        assertThrows<CarAlreadyExistException> {
            carService.createCar(expectedCar)
        }

        verify { carRepository.existsByLicensePlate(expectedCar.licensePlate) }
    }

    @Test
    fun `should return updated car`() {
        val expectedCar = getDefaultUpdatedCar()
        every { carRepository.findById(any()) } returns Optional.of(getDefaultCar())
        every { carRepository.existsByLicensePlate(any()) } returns false
        every { carRepository.save(any()) } returns expectedCar

        val result = carService.updateCar(DEFAULT_ID, expectedCar)

        assertEquals(expectedCar, result)

        verify {
            carRepository.findById(DEFAULT_ID)
            carRepository.existsByLicensePlate(expectedCar.licensePlate)
            carRepository.save(expectedCar)
        }
    }

    @Test
    fun `should return car already exist exception on update car`() {
        val expectedCar = getDefaultUpdatedCar()
        every { carRepository.findById(any()) } returns Optional.of(getDefaultCar())
        every { carRepository.existsByLicensePlate(any()) } returns true

        assertThrows<CarAlreadyExistException> {
            carService.updateCar(DEFAULT_ID, expectedCar)
        }

        verify {
            carRepository.findById(DEFAULT_ID)
            carRepository.existsByLicensePlate(expectedCar.licensePlate)
        }
    }

    @Test
    fun `should return car not found exception on update car`() {
        val expectedCar = getDefaultUpdatedCar()
        every { carRepository.findById(any()) } returns Optional.empty()

        assertThrows<CarNotFoundException> {
            carService.updateCar(DEFAULT_ID, expectedCar)
        }

        verify { carRepository.findById(DEFAULT_ID) }
    }


    private val DEFAULT_ID = 1L

    private fun getDefaultCar() =
        Car(
            carId = 1L,
            model = "Tesla Model S",
            year = 2022,
            licensePlate = "1234AA",
            color = "Black"
        )

    private fun getDefaultUpdatedCar() =
        Car(
            carId = 1L,
            model = "Tesla Model S",
            year = 2022,
            licensePlate = "5678BB",
            color = "White"
        )
}
