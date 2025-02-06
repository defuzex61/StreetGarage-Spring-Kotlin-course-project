package com.example.streetgarage.models

data class UserCarsData(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val email: String,
    val phone: String,
    val role: String,
    val carId: Long?,
    val regNumber: String?,
    val make: String?,
    val model: String?,
    val carYear: Int?,
    val vin: String?
)
