package com.example.streetgarage.models

data class RequestWork(
    val idWork: Long? = null,
    val idRequest: Long,
    val idWorkType: Long,
    val idPart: Long? = null,
    var completionDate: String? = null,
    val workComment: String? = null
)