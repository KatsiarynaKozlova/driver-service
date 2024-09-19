package com.software.modsen.driverservice.model

import com.software.modsen.driverservice.dto.response.DriverResponse
import com.software.modsen.driverservice.dto.response.DriverWithCarResponse
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete

@Entity
@Table(name = "drivers")
@SQLDelete(sql = "UPDATE drivers SET is_deleted = true WHERE driver_id=?")
data class Driver(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    var driverId: Long?,
    @Column(name = "name")
    var name: String,
    @Column(name = "email")
    var email: String,
    @Column(name = "phone")
    var phone: String,
    @Column(name = "sex")
    var sex: String,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    var car: Car?,
    @Column(name = "is_deleted")
    var isDeleted: Boolean = false
)

fun Driver.toDriverResponse(): DriverResponse {
    return DriverResponse(
        driverId = this.driverId,
        name = this.name,
        email = this.email,
        phone = this.phone,
        sex = this.sex
    )
}

fun Driver.toDriverWithCarResponse(): DriverWithCarResponse {
    return DriverWithCarResponse(
        driverId = this.driverId,
        name = this.name,
        email = this.email,
        phone = this.phone,
        sex = this.sex,
        car = this.car!!.toCarResponse()
    )
}
