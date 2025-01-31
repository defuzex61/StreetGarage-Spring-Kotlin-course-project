package com.example.streetgarage.services

import com.example.streetgarage.models.*
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class DashboardService(private val restTemplate: RestTemplate) {

    fun getUserOrders(userId: Long?): List<Request> {
        return try {
            val response = restTemplate.exchange(
                "http://localhost:8081/api/requests/user/$userId",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<Request>>() {}
            )
            response.body ?: emptyList()
        } catch (e: HttpClientErrorException) {
            emptyList() // Возвращаем пустой список, если произошла ошибка клиента
        } catch (e: Exception) {
            throw RuntimeException("Ошибка при получении заказов: ${e.message}", e)
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