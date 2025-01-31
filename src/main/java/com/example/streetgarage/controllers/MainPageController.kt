package com.example.streetgarage.controllers

import com.example.streetgarage.models.WorkType
import com.example.streetgarage.services.ServicesService
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainPageController(private val servicesService: ServicesService) {

    @GetMapping("/")
    fun mainPage(model: Model): String {
        model.addAttribute("title", "Главная страница автосервиса")
        val services = servicesService.getAllWorkTypes()
        model.addAttribute("navbar", "nav") // Указываем фрагмент
        model.addAttribute("services", services)
        model.addAttribute("footer", "footer")
        return "mainPage" // Возвращаем базовый шаблон
    }
}