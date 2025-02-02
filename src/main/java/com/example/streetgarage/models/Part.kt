package com.example.streetgarage.models

import java.math.BigDecimal

data class Part(
    val idPart: Long? = null,
    val partName: String,
    val article: String,
    val price: BigDecimal,
    val quantity: Int
)