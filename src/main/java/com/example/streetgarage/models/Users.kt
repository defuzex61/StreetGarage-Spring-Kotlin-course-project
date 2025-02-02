package com.example.streetgarage.models

import jakarta.persistence.*

@Entity
data class Users(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idUser: Long? = null,
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val passwordHash: String,
    val login: String,
    val email: String,
    val phone: String,
    @Enumerated(EnumType.STRING)
    var role: UserRole?
){
    // No-arg constructor for JPA
    constructor() : this(
        idUser = null,
        lastName = "",
        firstName = "",
        middleName = null,
        passwordHash = "",
        login = "",
        email = "",
        phone = "",
        role = null
    )
}


