package com.example.streetgarage.models

import org.springframework.security.core.userdetails.User

data class Comment(
    val idComment: Long? = null,
    val idRequest: Long,
    val idUser: User,
    val commentText: String,
    val commentDate: String
)