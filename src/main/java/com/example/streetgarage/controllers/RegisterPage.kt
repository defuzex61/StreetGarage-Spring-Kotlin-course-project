package com.example.streetgarage.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class RegisterPage {
    @GetMapping("/register")
    fun registerPage(model: Model): String {
        model.addAttribute("navbar", "nav") // Указываем фрагмент
        model.addAttribute("footer", "footer")// Указываем фрагмент
        return "registerPage" // Возвращаем базовый шаблон
    }
}