package com.example.streetgarage.models

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id


data class Part(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idPart: Long? = null,
    val partName: String,
    val article: String,
    val price: Double,
    val quantity: Int
)