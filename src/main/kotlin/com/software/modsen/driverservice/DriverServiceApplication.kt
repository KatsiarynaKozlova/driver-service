package com.software.modsen.driverservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class DriverServiceApplication

fun main(args: Array<String>) {
    runApplication<DriverServiceApplication>(*args)
}
