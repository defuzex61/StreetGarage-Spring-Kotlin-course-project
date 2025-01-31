package com.example.streetgarage.models

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

data class WorkType(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idWorkType: Long? = null,
    val name: String,
    val description: String?,
    val price: Double
)
