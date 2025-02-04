package com.example.streetgarage.models

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id


data class Car(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idCar: Long? = null,
    val regNumber: String,
    val make: String,
    val model: String,
    val carYear: Int,
    val vin: String,
    val user: Users
)