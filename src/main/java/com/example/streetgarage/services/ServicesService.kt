package com.example.streetgarage.services

import com.example.streetgarage.models.*
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

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


    fun getUserCars(userId: Long?): List<Car> {
        return try {
            val response = restTemplate.exchange(
                "http://localhost:8081/api/cars?userId=$userId",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<Car>>() {}
            )
            response.body ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    fun getCarById(carId: Long?): Car? {
        return try {
            restTemplate.getForObject("http://localhost:8081/api/cars/$carId", Car::class.java)
        } catch (e: Exception) {
            null // Возвращаем null, если автомобиль не найден
        }
    }
    // Получить тип работы по ID
    fun getWorkTypeById(workTypeId: Long): WorkType? {
        return try {
            restTemplate.getForObject("http://localhost:8081/api/work-types/$workTypeId", WorkType::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Создать заявку
    fun createRequest(request: Request, workType: WorkType, comment:String?): Result {
        try {
            // Создаем заявку (Request)
            val createdRequest = restTemplate.postForEntity(
                "http://localhost:8081/api/requests",
                request,
                Request::class.java
            ).body ?: return Result(false, "Ошибка при создании заявки")

            // Создаем объект RequestWork
            val requestWork = RequestWork(
                request = createdRequest,
                workType = workType,
                part = null, // Запчасть может быть null
                completionDate = null, // Дата завершения не указывается
                workComment = comment // Комментарий из заявки
            )

            // Отправляем POST-запрос для создания RequestWork
            restTemplate.postForEntity(
                "http://localhost:8081/api/request-works",
                requestWork,
                RequestWork::class.java
            )

            return Result(true, null)
        } catch (e: Exception) {
            return Result(false, "Ошибка при создании заявки: ${e.message}")
        }
    }

}