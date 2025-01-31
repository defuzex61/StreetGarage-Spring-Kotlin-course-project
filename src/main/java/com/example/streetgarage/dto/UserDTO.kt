package com.example.streetgarage.dto
import com.example.streetgarage.models.UserRole
import com.fasterxml.jackson.annotation.JsonProperty

data class UserDTO(
    @JsonProperty("idUser") val idUser: Long? = null,
    @JsonProperty("lastName") val lastName: String,
    @JsonProperty("firstName") val firstName: String,
    @JsonProperty("middleName") val middleName: String? = null,
    @JsonProperty("passwordHash") val passwordHash: String,
    @JsonProperty("login") val login: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("phone") val phone: String,
    @JsonProperty("role") val role: String
)
