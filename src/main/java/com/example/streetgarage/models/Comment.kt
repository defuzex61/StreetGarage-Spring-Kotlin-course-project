package com.example.streetgarage.models

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.security.core.userdetails.User

data class Comment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idComment: Long? = null,
    val idRequest: Long,
    val idUser: User,
    val commentText: String,
    val commentDate: String
)