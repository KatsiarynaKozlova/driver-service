package com.software.modsen.driverservice.service

import com.software.modsen.driverservice.dto.request.DriverForRating
import com.software.modsen.driverservice.exception.DriverNotFoundException
import com.software.modsen.driverservice.exception.EmailAlreadyExistException
import com.software.modsen.driverservice.exception.PhoneAlreadyExistException
import com.software.modsen.driverservice.kafka.producer.DriverProducer
import com.software.modsen.driverservice.model.Car
import com.software.modsen.driverservice.model.Driver
import com.software.modsen.driverservice.model.Sex
import com.software.modsen.driverservice.repository.CarRepository
import com.software.modsen.driverservice.repository.DriverRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class DriverServiceTest {
    @MockK
    private lateinit var driverRepository: DriverRepository

    @MockK
    private lateinit var carRepository: CarRepository

    @MockK
    private lateinit var driverProducer: DriverProducer

    @InjectMockKs
    private lateinit var driverService: DriverService

    @Test
    fun `should return driver by id`() {
        val expectedDriver = getDefaultDriver()
        every { driverRepository.findById(any()) } returns Optional.of(expectedDriver)

        val result = driverService.getDriverById(DEFAULT_ID)

        assertEquals(expectedDriver, result)
        verify {
            driverRepository.findById(DEFAULT_ID)
        }
    }

    @Test
    fun `should return driver not found exception on get by id`() {
        every { driverRepository.findById(any()) } returns Optional.empty()
        assertThrows<DriverNotFoundException> {
            driverService.getDriverById(DEFAULT_ID)
        }
    }

    @Test
    fun `should return list of Drivers`() {
        val driverList: List<Driver> = listOf(getDefaultDriver())
        every { driverRepository.findAll() } returns driverList

        val result = driverService.getAllDrivers()
        assertEquals(driverList, result)
    }

    @Test
    fun `should return new Driver`() {
        val newDriver = getDefaultDriver()

        every { driverRepository.existsByPhone(any()) } returns false
        every { driverRepository.existsByEmail(any()) } returns false
        every { carRepository.findById(any()) } returns Optional.of(getDefaultCar())
        every { driverRepository.save(any()) } returns newDriver
        every { driverProducer.sendDriver(any()) } just Runs

        val result = driverService.createDriver(newDriver.car!!.carId!!, newDriver)

        assertEquals(newDriver, result)
        verify {
            driverRepository.existsByPhone(newDriver.phone)
            driverRepository.existsByEmail(newDriver.email)
            driverRepository.save(newDriver)
            carRepository.findById(newDriver.car!!.carId!!)
            driverProducer.sendDriver(getDefaultDriverForRating())
        }
    }

    @Test
    fun `should return phone already exist exception on create driver`() {
        val newDriver = getDefaultDriver()
        every { driverRepository.existsByPhone(any()) } returns true

        assertThrows<PhoneAlreadyExistException> {
            driverService.createDriver(newDriver.car!!.carId!!, newDriver)
        }

        verify { driverRepository.existsByPhone(newDriver.phone) }
    }

    @Test
    fun `should return email already exist exception on create driver`() {
        val newDriver = getDefaultDriver()

        every { driverRepository.existsByPhone(any()) } returns false
        every { driverRepository.existsByEmail(any()) } returns true

        assertThrows<EmailAlreadyExistException> {
            driverService.createDriver(newDriver.car!!.carId!!, newDriver)
        }
        verify {
            driverRepository.existsByPhone(newDriver.phone)
            driverRepository.existsByEmail(newDriver.email)
        }
    }

    @Test
    fun `should return updated Driver`() {
        val updatedDriver = getDefaultUpdatedDriver()
        val driver = getDefaultDriver()

        every { driverRepository.findById(any()) } returns Optional.of(driver)
        every { driverRepository.save(any()) } returns updatedDriver
        every { driverRepository.existsByPhone(any()) } returns false
        every { driverRepository.existsByEmail(any()) } returns false

        val result = driverService.updateDriver(DEFAULT_ID, updatedDriver)

        assertEquals(updatedDriver, result)
        verify {
            driverRepository.findById(DEFAULT_ID)
            driverRepository.save(updatedDriver)
            driverRepository.existsByEmail(updatedDriver.email)
            driverRepository.existsByPhone(updatedDriver.phone)
        }
    }

    @Test
    fun `should return email already exist exception on update driver`() {
        val updatedDriver = getDefaultUpdatedDriver()
        val driver = getDefaultDriver()

        every { driverRepository.findById(any()) } returns Optional.of(driver)
        every { driverRepository.existsByEmail(any()) } returns true

        assertThrows<EmailAlreadyExistException> {
            driverService.updateDriver(DEFAULT_ID, updatedDriver)
        }

        verify {
            driverRepository.findById(DEFAULT_ID)
            driverRepository.existsByEmail(updatedDriver.email)
        }
    }

    @Test
    fun `should return phone already exist exception on update driver`() {
        val updatedDriver = getDefaultUpdatedDriver()
        val driver = getDefaultDriver()

        every { driverRepository.findById(any()) } returns Optional.of(driver)
        every { driverRepository.existsByEmail(any()) } returns false
        every { driverRepository.existsByPhone(any()) } returns true

        assertThrows<PhoneAlreadyExistException> {
            driverService.updateDriver(DEFAULT_ID, updatedDriver)
        }

        verify {
            driverRepository.findById(DEFAULT_ID)
            driverRepository.existsByEmail(updatedDriver.email)
            driverRepository.existsByPhone(updatedDriver.phone)
        }
    }

    @Test
    fun `should return driver not found exception on update driver`() {
        val updatedDriver = getDefaultUpdatedDriver()

        every { driverRepository.findById(any()) } returns Optional.empty()

        assertThrows<DriverNotFoundException> {
            driverService.updateDriver(DEFAULT_ID, updatedDriver)
        }

        verify { driverRepository.findById(DEFAULT_ID) }
    }

    private val DEFAULT_ID: Long = 1L

    private fun getDefaultDriver() =
        Driver(
            driverId = 1L,
            name = "alex",
            email = "email@mail.ru",
            phone = "1234567890",
            sex = Sex.M,
            car = getDefaultCar()
        )

    private fun getDefaultUpdatedDriver() =
        Driver(
            driverId = 1L,
            name = "Alex",
            email = "new_email@mail.ru",
            phone = "0987654321",
            sex = Sex.M,
            car = getDefaultCar()
        )

    private fun getDefaultCar() =
        Car(
            carId = 1L,
            model = "Tesla Model S",
            year = 2022,
            licensePlate = "1234AA",
            color = "Black"
        )

    private fun getDefaultDriverForRating() = DriverForRating(id = 1L)
}
