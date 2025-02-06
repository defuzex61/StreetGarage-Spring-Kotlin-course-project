package com.example.streetgarage.controllers

import com.example.streetgarage.dto.UserDTO
import com.example.streetgarage.models.UserRole
import com.example.streetgarage.models.Users
import com.example.streetgarage.services.AuthService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/register")
class RegisterPage(private val authService: AuthService) {
    // Отображение формы регистрации
    @GetMapping
    fun showRegistrationForm(): String {
        return "registerPage" // Имя шаблона Thymeleaf
    }

    // Обработка данных регистрации
    @PostMapping
    fun registerUser(
        @RequestParam lastName: String,
        @RequestParam firstName: String,
        @RequestParam middleName: String?,
        @RequestParam password: String,
        @RequestParam login: String,
        @RequestParam email: String,
        @RequestParam phone: String,
        model: Model,
        session: HttpSession
    ): String {
        // Хэшируем пароль (в реальном приложении используйте BCrypt)
        val passwordHash = password // Замените на реальное хэширование

        // Создаем объект пользователя
        val user = UserDTO(
            lastName = lastName,
            firstName = firstName,
            middleName = middleName,
            passwordHash = passwordHash,
            login = login,
            email = email,
            phone = phone,
            role = UserRole.client.name
        )

        // Регистрируем пользователя через API
        val savedUser = authService.registerUser(user)

        return if (savedUser != null) {
            // Передаем ID пользователя на второй этап
            session.setAttribute("user", user)
            model.addAttribute("user", savedUser)
            "redirect:/register/car" // Перенаправляем на второй этап
        } else {
            // Обработка ошибки регистрации
            model.addAttribute("error", "Ошибка регистрации")
            "registerPage"
        }
    }


}