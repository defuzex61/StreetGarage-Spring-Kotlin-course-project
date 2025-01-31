package com.example.streetgarage.models



data class Car(
    val idCar: Long? = null,
    val regNumber: String,
    val make: String,
    val model: String,
    val carYear: Int,
    val vin: String,
    val idClient: Long
)