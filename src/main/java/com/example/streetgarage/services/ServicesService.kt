package com.example.streetgarage.services

import com.example.streetgarage.models.Car
import com.example.streetgarage.models.Comment
import com.example.streetgarage.models.WorkType
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
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

    fun addComment(comment: Comment): Boolean {
        return try {
            val responseEntity = restTemplate.exchange(
                "http://localhost:8081/api/comments",
                HttpMethod.POST,
                HttpEntity(comment, HttpHeaders()),
                object : ParameterizedTypeReference<Void>() {}
            )
            responseEntity.statusCode.is2xxSuccessful
        } catch (e: Exception) {
            // Логирование ошибки или обработка
            false
        }
    }

    fun getUserCars(userId: Long?): List<Car> {
        return try {
            val response = restTemplate.exchange(
                "http://localhost:8081/api/cars/user/$userId",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<Car>>() {}
            )
            response.body ?: emptyList()
        } catch (e: HttpClientErrorException) {
            emptyList() // Возвращаем пустой список, если произошла ошибка клиента
        } catch (e: Exception) {
            throw RuntimeException("Ошибка при получении автомобилей: ${e.message}", e)
        }
    }
}