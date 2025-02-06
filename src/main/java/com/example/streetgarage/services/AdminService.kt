package com.example.streetgarage.services

import com.example.streetgarage.models.UserCarsData
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

@Service
class AdminService(private val restTemplate: RestTemplate) {

    // Получение данных через API
    fun findAllUserCarsData(): List<UserCarsData> {
        return try {
            val response = restTemplate.exchange(
                "http://localhost:8081/api/admin/user-cars", // URL API
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<UserCarsData>>() {}
            )
            response.body ?: emptyList()
        } catch (e: HttpClientErrorException) {
            emptyList() // Возвращаем пустой список, если произошла ошибка клиента
        } catch (e: Exception) {
            throw RuntimeException("Ошибка при получении данных: ${e.message}", e)
        }
    }

    // Экспорт данных в CSV
    fun exportUserCarsToCsv(): ByteArray {
        val userCarsData = findAllUserCarsData()

        // Создаем временный поток для записи CSV
        val outputStream = ByteArrayOutputStream()
        PrintWriter(outputStream).use { writer ->
            // Записываем заголовки CSV
            writer.println("user_id,first_name,last_name,middle_name,email,phone,role,car_id,reg_number,make,model,car_year,vin")

            // Записываем данные
            userCarsData.forEach { userCar ->
                writer.println(
                    "${userCar.userId}," +
                            "${userCar.firstName}," +
                            "${userCar.lastName}," +
                            "${userCar.middleName}," +
                            "${userCar.email}," +
                            "${userCar.phone}," +
                            "${userCar.role}," +
                            "${userCar.carId}," +
                            "${userCar.regNumber}," +
                            "${userCar.make}," +
                            "${userCar.model}," +
                            "${userCar.carYear}," +
                            "${userCar.vin}"
                )
            }
        }

        return outputStream.toByteArray()
    }
}