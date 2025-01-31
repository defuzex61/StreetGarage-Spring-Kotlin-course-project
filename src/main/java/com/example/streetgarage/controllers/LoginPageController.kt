package com.example.streetgarage.controllers

import com.example.streetgarage.dto.UserDTO
import com.example.streetgarage.models.UserLoginRequest
import com.example.streetgarage.services.AuthService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
class LoginPageController(private val authService: AuthService) {

    @GetMapping("/login")
    fun loginPage(model: Model): String {
        model.addAttribute("navbar", "nav") // Указываем фрагмент
        model.addAttribute("footer", "footer") // Указываем фрагмент
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

        // Используем AuthService для авторизации
        val user = authService.authenticate(requestBody)

        return if (user != null) {
            session.setAttribute("user", user)
            "redirect:/dashboard"
        } else {
            model.addAttribute("error", "Неверный логин или пароль")
            "authPage"
        }
    }
}