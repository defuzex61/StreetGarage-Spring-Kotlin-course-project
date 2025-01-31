package com.example.streetgarage.models

data class Request(
    val idRequest: Long? = null,
    val creationDate: String,
    val plannedDate: String?,
    val completionDate: String?,
    val idClient: Long,
    val idCar: Long,
    val idMechanic: Long?,
    val status: String
)