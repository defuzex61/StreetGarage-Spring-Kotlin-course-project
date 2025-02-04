package com.example.streetgarage.models

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.math.BigDecimal


data class Part(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idPart: Long? = null,
    val partName: String,
    val article: String,
    val price: BigDecimal,
    val quantity: Int
)