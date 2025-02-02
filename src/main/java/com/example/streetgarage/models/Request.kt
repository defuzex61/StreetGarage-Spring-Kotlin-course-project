package com.example.streetgarage.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Request(
    @JsonProperty("idRequest") val idRequest: Long? = null,
    @JsonProperty("creationDate") val creationDate: String,
    @JsonProperty("plannedDate") val plannedDate: String?,
    @JsonProperty("completionDate") val completionDate: String?,
    @JsonProperty("client") val client: Users,
    @JsonProperty("car") val car: Car,
    @JsonProperty("mechanic") var mechanic: Users?,
    @JsonProperty("status") var status: RequestStatus?
)