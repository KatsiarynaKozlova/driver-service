package com.software.modsen.driverservice.model

import com.software.modsen.driverservice.dto.response.CarResponse
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.SQLDelete

@Entity
@Table(name = "cars")
@SQLDelete(sql = "UPDATE cars SET is_deleted = true WHERE car_id=?")
data class Car(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    var carId: Long?,
    @Column(name = "color")
    var color: String,
    @Column(name = "model")
    var model: String,
    @Column(name = "license_plate")
    var licensePlate: String,
    @Column(name = "year")
    var year: Int,
    @Column(name = "is_deleted")
    var isDeleted: Boolean = false
)
