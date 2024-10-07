package com.software.modsen.driverservice.model

import jakarta.persistence.*
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
    @Enumerated(EnumType.STRING)
    var sex: DriverSex,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    var car: Car?,
    @Column(name = "is_deleted")
    var isDeleted: Boolean = false
)
