package com.example.streetgarage.models

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

data class RequestWork(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idWork: Long? = null,
    val request: Request,
    val workType: WorkType,
    var part: Part? = null,
    var completionDate: String? = null,
    var workComment: String? = null
)