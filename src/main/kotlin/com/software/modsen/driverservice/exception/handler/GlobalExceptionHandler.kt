package com.software.modsen.driverservice.exception.handler

import com.software.modsen.driverservice.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(
        CarAlreadyExistException::class,
        EmailAlreadyExistException::class,
        PhoneAlreadyExistException::class
    )
    fun handlerCarAlreadyExistException(e: Exception): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(e.message.toString())
    }

    @ExceptionHandler(CarNotFoundException::class, DriverNotFoundException::class)
    fun handlerCarNotFoundException(e: Exception): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(e.message.toString())
    }
}
