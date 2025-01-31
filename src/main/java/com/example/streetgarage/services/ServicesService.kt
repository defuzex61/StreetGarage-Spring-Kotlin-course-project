package com.example.streetgarage.services

import com.example.streetgarage.models.Car
import com.example.streetgarage.models.WorkType
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ServicesService(private val restTemplate: RestTemplate) {

    fun getAllWorkTypes(): Array<WorkType> {
        val responseEntity = restTemplate.exchange(
            "http://localhost:8081/api/work-types",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Array<WorkType>>() {}
        )
        return responseEntity.body ?: emptyArray()
    }

    fun getAllCars(): Array<Car> {
        val responseEntity = restTemplate.exchange(
            "http://localhost:8081/api/cars",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Array<Car>>() {}
        )
        return responseEntity.body ?: emptyArray()
    }
}