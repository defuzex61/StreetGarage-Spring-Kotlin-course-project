package com.example.streetgarage.controllers

import com.example.streetgarage.dto.UserDTO
import com.example.streetgarage.models.Car
import com.example.streetgarage.models.Request
import com.example.streetgarage.services.DashboardService
import jakarta.servlet.http.HttpSession
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Controller
class DashboardPage(private val dashboardService: DashboardService) {

    @GetMapping("/dashboard")
    fun dashboardPage(
        model: Model,
        @AuthenticationPrincipal userDetails: UserDetails?,
        session: HttpSession
    ): String {
        val user = session.getAttribute("user") as? UserDTO
            ?: return "redirect:/login" // Если данные отсутствуют, перенаправляем на страницу входа

        val userId = user.idUser
        println("User id $userId")

        // Добавляем данные пользователя в модель
        model.addAttribute("user", user)

        // Получаем заказы пользователя
        val requests = dashboardService.getUserOrders(userId)

        if (requests.isEmpty()) {
            model.addAttribute("requestMessage", "У вас нет заказов.")
        } else {
            model.addAttribute("requests", requests)
        }

        // Получаем автомобили пользователя
        val cars = dashboardService.getUserCars(userId)
        if (cars.isEmpty()) {
            model.addAttribute("carMessage", "У вас нет автомобилей.")
        } else {
            model.addAttribute("cars", cars)
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