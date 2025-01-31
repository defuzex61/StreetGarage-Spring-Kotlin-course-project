package com.example.streetgarage.services

import com.example.streetgarage.dto.UserDTO
import com.example.streetgarage.models.UserLoginRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class AuthService(private val restTemplate: RestTemplate) {

    fun authenticate(requestBody: UserLoginRequest): UserDTO? {
        return try {
            // Отправляем запрос на авторизацию
            val response: ResponseEntity<String> = restTemplate.postForEntity(
                "http://localhost:8081/api/users/login",
                requestBody,
                String::class.java
            )

            // Обработка успешного ответа
            if (response.statusCode.is2xxSuccessful) {
                parseResponse(response)
            } else {
                null
            }
        } catch (e: HttpClientErrorException.Unauthorized) {
            // Обработка ошибки 401
            null
        } catch (e: Exception) {
            // Обработка других ошибок
            null
        }
    }

    private fun parseResponse(response: ResponseEntity<String>?): UserDTO {
        val objectMapper = jacksonObjectMapper()
        val body = response?.body ?: throw IllegalStateException("Ответ пустой")
        return objectMapper.readValue(body, UserDTO::class.java)
    }
}