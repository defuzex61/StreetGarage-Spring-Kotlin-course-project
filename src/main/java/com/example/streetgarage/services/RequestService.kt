package com.example.streetgarage.services

import com.example.streetgarage.models.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
    fun completeRequest(
        requestId: Long,
        workComment: String?,
        newPartName: String,
        newPartArticle: String,
        newPartPrice: BigDecimal,
        newPartQuantity: Int,
        newWorkTypeName: String,
        newWorkTypeDescription: String?,
        newWorkTypePrice: Double?
    ): Result {
        // Получаем информацию о заказе через API
        val request = restTemplate.getForObject("http://localhost:8081/api/requests/$requestId", Request::class.java) ?: return Result(false, "Заказ не найден")
        val newWorkType = WorkType(name = newWorkTypeName, description = newWorkTypeDescription, price = newWorkTypePrice ?: 0.0)
        val newPart = Part(partName = newPartName, article = newPartArticle, price = newPartPrice, quantity = newPartQuantity)

        // Создаем новый тип работы, если указан
        newWorkTypeName.let {
            restTemplate.postForEntity("http://localhost:8081/api/work-types", newWorkType, WorkType::class.java)
        }

        // Создаем новую запчасть, если указана
        newPartName.let {
            restTemplate.postForEntity("http://localhost:8081/api/parts", newPart, Part::class.java)
        }

        // Обновляем статус заказа на "Completed" и устанавливаем дату завершения
        val updatedRequest = request.copy(
            status = RequestStatus.Completed,
            completionDate = LocalDateTime.now().toString()
        )

        // Отправляем PUT-запрос для обновления заказа
        restTemplate.put("http://localhost:8081/api/requests/${request.idRequest}", updatedRequest)

        // Создаем объект RequestWork
        val requestWork = RequestWork(
            request = request,
            workType = newWorkType,
            part = newPart,
            completionDate = LocalDateTime.now().toString(),
            workComment = workComment
        )

        // Отправляем PUT-запрос для обновления RequestWork
        val response = restTemplate.exchange("http://localhost:8081/api/request-works/${requestWork.idWork}", HttpMethod.PUT, HttpEntity(requestWork), RequestWork::class.java)

        return if (response.statusCode.is2xxSuccessful) {
            Result(true, null)
        } else {
            Result(false, "Ошибка при обновлении RequestWork: ${response.body}")
        }
    }
}
data class Result(val isSuccessful: Boolean, val errorMessage: String?)
