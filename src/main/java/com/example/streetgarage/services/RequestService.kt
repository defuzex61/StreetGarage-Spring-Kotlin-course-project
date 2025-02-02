package com.example.streetgarage.services

import com.example.streetgarage.models.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class RequestService(private val restTemplate: RestTemplate) {

    private val logger = LoggerFactory.getLogger(RequestService::class.java)
    private val objectMapper = ObjectMapper()

    // Получить все заказы со статусом New
    fun getNewRequests(): List<Request> {
        return try {
            val response = restTemplate.exchange(
                "http://localhost:8081/api/requests?status=New",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<Request>>() {}
            )
            response.body ?: emptyList()
        } catch (e: Exception) {
            logger.error("Ошибка при получении новых заказов: ${e.message}")
            emptyList()
        }
    }

    // Получить заказы, назначенные на механика
    fun getRequestsByMechanicId(mechanicId: Long): List<Request> {
        return try {
            val response = restTemplate.exchange(
                "http://localhost:8081/api/requests?mechanicId=$mechanicId",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<Request>>() {}
            )
            response.body ?: emptyList()
        } catch (e: Exception) {
            logger.error("Ошибка при получении заказов механика: ${e.message}")
            emptyList()
        }
    }

    // Взять заказ (назначить механика и изменить статус)
    fun takeRequest(requestId: Long, mechanicInfo: Users): Request {
        return try {
            // Получаем заказ по ID
            val request = restTemplate.getForEntity(
                "http://localhost:8081/api/requests/$requestId",
                Request::class.java
            ).body ?: throw IllegalStateException("Заказ не найден")
            logger.info("ПРИШЛО УШЛО: ${request.toString()}")
            // Обновляем статус и назначаем механика
            request.mechanic = Users(
                idUser = mechanicInfo.idUser,
                lastName = mechanicInfo.lastName, // Заполни эти поля, если они обязательны
                firstName = mechanicInfo.firstName,
                middleName = mechanicInfo.middleName,
                passwordHash = mechanicInfo.passwordHash,
                login = mechanicInfo.login,
                email = mechanicInfo.email,
                phone = mechanicInfo.phone,
                role = UserRole.mechanic
            )
            request.status = RequestStatus.In_progress


            // Логируем данные перед отправкой
            val requestJson = objectMapper.writeValueAsString(request)
            logger.info("Отправляем запрос на обновление заказа: ${request.toString()}")

            // Отправляем обновленный заказ
            val response = restTemplate.postForEntity(
                "http://localhost:8081/api/requests",
                request,
                Request::class.java
            )
            response.body ?: throw IllegalStateException("Ошибка при обновлении заказа")
        } catch (e: Exception) {
            logger.error("Ошибка при взятии заказа: ${e.message}")
            throw e
        }
    }

    // Завершить заказ
    fun completeRequest(requestId: Long, requestWork: RequestWork): Request {
        return try {
            // Добавляем информацию о выполненной работе
            val responseWork = restTemplate.postForEntity(
                "http://localhost:8081/api/request-works",
                requestWork,
                RequestWork::class.java
            )

            if (!responseWork.statusCode.is2xxSuccessful) {
                throw IllegalStateException("Ошибка при добавлении работы: ${responseWork.statusCode}")
            }

            // Обновляем статус заказа
            val requestResponse = restTemplate.getForEntity(
                "http://localhost:8081/api/requests/$requestId",
                Request::class.java
            ).body ?: throw IllegalStateException("Заказ не найден")

            // Обновляем статус на "Completed"
            requestResponse.status = RequestStatus.Completed

            // Логируем данные перед отправкой
            val requestJson = objectMapper.writeValueAsString(requestResponse)
            logger.info("Отправляем запрос на завершение заказа: $requestJson")

            // Отправляем обновленный заказ
            val response = restTemplate.exchange(
                "http://localhost:8081/api/requests/${requestResponse.idRequest}",
                HttpMethod.PUT,
                HttpEntity(requestResponse, null), // Передаем обновленный объект
                Request::class.java
            )

            response.body ?: throw IllegalStateException("Ошибка при завершении заказа")
        } catch (e: Exception) {
            logger.error("Ошибка при завершении заказа: ${e.message}")
            throw e
        }
    }
}