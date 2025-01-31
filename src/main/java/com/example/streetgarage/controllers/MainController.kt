package com.example.streetgarage.controllers

import com.example.streetgarage.dto.UserDTO
import com.example.streetgarage.models.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpSession
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate


@Controller
class MainController {
    companion object{
        //lateinit var user: UserDTO
    }
    private val restTemplate = RestTemplate()

    @GetMapping("/")
    fun mainPage(model: Model): String {
        model.addAttribute("title", "Главная страница автосервиса")
        val responseEntity = restTemplate.exchange(
            "http://localhost:8081/api/work-types",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Array<WorkType>>() {}
        )

        val services = responseEntity.body ?: emptyArray()

        model.addAttribute("navbar", "nav") // Указываем фрагмент
        model.addAttribute("services", services)
        model.addAttribute("footer", "footer")
        return "mainPage" // Возвращаем базовый шаблон
    }
//СИТУАЦИЯ ПО АВТОРИЗАЦИИ НАДО СДЕЛАТЬ СЕРВИС КОТОРЫЙ ОБРАБАТЫВАЕТ АВТОРИЗАЦИЮ. ПОЛУЧИТЬ ЛОГИН И ПАРОЛЬ И КИНУТЬ В АПИ
    // АПИ ВОЗВРАЩАЕТ АПИ ПОЛЬЗОВАТЕЛЯ
    @GetMapping("/login")
    fun loginPage(model: Model): String {
        model.addAttribute("navbar", "nav") // Указываем фрагмент
        model.addAttribute("footer", "footer")// Указываем фрагмент
        return "authPage" // Возвращаем базовый шаблон
    }

    @PostMapping("/login")
    fun login(
        @RequestParam username: String,
        @RequestParam password: String,
        session: HttpSession,
        model: Model
    ): String {

        val requestBody = UserLoginRequest(username, password)

        println("work")
        println(requestBody.login)

        // Отправляем данные в API

        try {
            // Отправляем запрос
            val response: ResponseEntity<String> = restTemplate.postForEntity(
                "http://localhost:8081/api/users/login",
                requestBody,
                String::class.java
            )

            // Обработка успешного ответа
            return if (response.statusCode.is2xxSuccessful) {
                val user = parseResponse(response)
                session.setAttribute("user", user)
                "redirect:/dashboard"
            } else {
                // Обработка ошибки
                model.addAttribute("error", "Неверный логин или пароль")
                "authPage"
            }
        } catch (e: HttpClientErrorException.Unauthorized) {
            // Обработка ошибки 401
            model.addAttribute("error", "Неверный логин или пароль")
            return "authPage"
        } catch (e: Exception) {
            // Обработка других ошибок
            model.addAttribute("error", "Произошла ошибка. Попробуйте позже.")
            return "authPage"
        }

    }
    private fun parseResponse(response: ResponseEntity<String>?): UserDTO {//ТАМ НАДО ЧЕТО
        val objectMapper = jacksonObjectMapper()
        val user: UserDTO
        // Проверяем, что ответ не null

        val body = response?.body ?: throw IllegalStateException("Ответ пустой")

        try {
            // Парсим JSON-строку в объект UserDTO
             user = objectMapper.readValue(body, UserDTO::class.java)
        } catch (e: Exception) {
            throw IllegalStateException("Ошибка при парсинге JSON: ${e.message}")
        }

        println(user)
        return user
    }
// ТУТ НАДО ПЕРЕДЕЛАТЬ СТРАНИЦУ АВТОРИЗАЦИИ И СДЕЛАТЬ СОХРАНЕНИЕ СОСТОЯНИЯ АВТОРИЗАЦИИ
    @GetMapping("/register")
    fun registerPage(model: Model): String {
        model.addAttribute("navbar", "nav") // Указываем фрагмент
        model.addAttribute("footer", "footer")// Указываем фрагмент
        return "registerPage" // Возвращаем базовый шаблон
    }

    @GetMapping("/services")
    fun servicesPage(model: Model): String {
        val responseEntity = restTemplate.exchange(
            "http://localhost:8081/api/work-types",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Array<WorkType>>() {}
        )
        val responseEntityCar = restTemplate.exchange(
            "http://localhost:8081/api/cars",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Array<Car>>() {}
        )
        val car = responseEntityCar.body ?: emptyArray()
        val services = responseEntity.body ?: emptyArray()
        model.addAttribute("workTypes", services)
        model.addAttribute("cars", car)
        return "servicesPage"
    }

    @GetMapping("/dashboard")//НА ДАННЫЙ МОМЕНТ ВМЕСТЕ С ПОЛЬЗОВАТЕЛЕМ ВЫВОДЯТСЯ НЕ ТОЛЬКО ЕГО АВТОМОБИЛИ
    fun dashboardPage(model: Model, @AuthenticationPrincipal userDetails: UserDetails?, session: HttpSession,): String {
        /*
        if (userDetails == null) {
            // Если userDetails null, возвращаем сообщение об ошибке или перенаправляем на страницу входа
            return "redirect:/login"
        }

         */
        val user = session.getAttribute("user") as? UserDTO
            ?: return "redirect:/login" // Если данные отсутствуют, перенаправляем на страницу входа
        val userId = user.idUser
        println("User id $userId")
            // Получаем данные пользователя
            //val user = restTemplate.getForObject("http://localhost:8081/api/users/$userId", Users::class.java)
            model.addAttribute("user", user)

            // Получаем заказы пользователя с использованием ParameterizedTypeReference
            try {
                val requests = restTemplate.exchange(
                    "http://localhost:8081/api/requests/user/$userId",
                    HttpMethod.GET,
                    null,
                    object : ParameterizedTypeReference<List<Request>>() {}
                ).body ?: emptyList()

                if (requests.isEmpty()) {
                    model.addAttribute("requestMessage", "У вас нет заказов.")
                } else {
                    model.addAttribute("requests", requests)
                }
            } catch (e: HttpClientErrorException) {
                model.addAttribute("requestMessage", "У вас нет заказов")
            } catch (e: Exception) {
                model.addAttribute("requestMessage", "Произошла ошибка: ${e.message}")
            }

            // Получаем информацию о автомобиле пользователя
            try {
                val cars = restTemplate.exchange(
                    "http://localhost:8081/api/cars/user/$userId",
                    HttpMethod.GET,
                    null,
                    object : ParameterizedTypeReference<List<Car>>() {}
                ).body ?: emptyList()

                if (cars.isEmpty()) {
                    model.addAttribute("carMessage", "У вас нет автомобилей.")
                } else {
                    model.addAttribute("cars", cars)
                }
            } catch (e: HttpClientErrorException) {
                model.addAttribute("carMessage", "У вас нет автомобилей.")
            } catch (e: Exception) {
                model.addAttribute("carMessage", "Произошла ошибка: ${e.message}")
            }
            return "dashboard" // Возвращаем шаблон профиля

    }

    @PostMapping("/logout")
    fun logout(session: HttpSession): String {
        // Завершаем сессию
        session.invalidate()

        // Очищаем контекст безопасности
        SecurityContextHolder.clearContext()

        // Перенаправляем на страницу входа
        return "redirect:/login"
    }



}