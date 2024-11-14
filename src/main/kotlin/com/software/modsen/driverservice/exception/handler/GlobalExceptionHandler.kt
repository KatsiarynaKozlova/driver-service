package com.software.modsen.driverservice.exception.handler

import com.software.modsen.driverservice.exception.*
import com.software.modsen.driverservice.exception.message.ErrorMessage
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(
        CarAlreadyExistException::class,
        EmailAlreadyExistException::class,
        PhoneAlreadyExistException::class
    )
    fun handlerCarAlreadyExistException(e: RuntimeException): ResponseEntity<ErrorMessage> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorMessage(e.message.toString()))
    }

    @ExceptionHandler(CarNotFoundException::class, DriverNotFoundException::class)
    fun handlerCarNotFoundException(e: RuntimeException): ResponseEntity<ErrorMessage> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorMessage(e.message.toString()))
    }

    @ExceptionHandler(ServiceUnAvailableException::class)
    fun handlerServiceUnAvailableException(e: RuntimeException): ResponseEntity<ErrorMessage> {
        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(ErrorMessage(e.message.toString()))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ResponseEntity<ErrorMessage> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorMessage(e.message.toString()))
    }
}
