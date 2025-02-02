package com.example.streetgarage.controllers

import com.example.streetgarage.dto.UserDTO
import com.example.streetgarage.models.Comment
import com.example.streetgarage.models.Request
import com.example.streetgarage.services.ServicesService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import java.time.LocalDateTime

@Controller
class ServicePageController(
    private val servicesService: ServicesService,
    private val session: HttpSession
) {

    @GetMapping("/services")
    fun servicesPage(model: Model): String {
        val services = servicesService.getAllWorkTypes()
        val user = session.getAttribute("user") as? UserDTO
            ?: return "redirect:/login" // Если данные отсутствуют, перенаправляем на страницу входа
        val userId = user.idUser
        val cars = servicesService.getUserCars(userId)
        model.addAttribute("workTypes", services)
        model.addAttribute("cars", cars)
        return "servicesPage"
    }

}