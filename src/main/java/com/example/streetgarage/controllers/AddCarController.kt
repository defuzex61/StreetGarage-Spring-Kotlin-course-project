package com.example.streetgarage.controllers

import com.example.streetgarage.dto.UserDTO
import com.example.streetgarage.models.Car
import com.example.streetgarage.models.UserRole
import com.example.streetgarage.models.Users
import com.example.streetgarage.services.AuthService
import jakarta.servlet.http.HttpSession
import org.h2.engine.User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/register/car")
class AddCarController (
    private val carService: AuthService
) {

    // Отображение формы для данных об автомобиле
    @GetMapping
    fun showCarForm(model: Model, session: HttpSession): String {
        val userDTO = session.getAttribute("user") as? UserDTO
            ?: return "redirect:/login"
        val user = Users(userDTO.idUser,userDTO.lastName,userDTO.firstName,userDTO.middleName, userDTO.passwordHash, userDTO.login, userDTO.email, userDTO.phone, UserRole.client)

        if (user != null) {
            model.addAttribute("user", user)
            return "register-car" // Имя шаблона Thymeleaf
        } else {
            // Обработка ошибки
            return "redirect:/register"
        }
    }

    // Обработка данных об автомобиле
    @PostMapping
    fun registerCar(
        @RequestParam regNumber: String,
        @RequestParam make: String,
        @RequestParam model: String,
        @RequestParam carYear: Int,
        @RequestParam vin: String,
        @RequestParam userId: Long,
        session: HttpSession
    ): String {
        // Получаем пользователя по ID (через API)
        val userDTO = session.getAttribute("user") as? UserDTO
            ?: return "redirect:/login"
        val user = Users(userDTO.idUser,userDTO.lastName,userDTO.firstName,userDTO.middleName, userDTO.passwordHash, userDTO.login, userDTO.email, userDTO.phone, UserRole.client)
        return if (user != null) {
            // Создаем объект автомобиля
            val car = Car(
                regNumber = regNumber,
                make = make,
                model = model,
                carYear = carYear,
                vin = vin,
                user = user
            )

            // Регистрируем автомобиль через API
            val savedCar = carService.registerCar(car, user)

            if (savedCar != null) {
                "redirect:/dashboard" // Перенаправляем на страницу успешной регистрации
            } else {
                "redirect:/register/car?userId=$userId&error=Ошибка регистрации автомобиля"
            }
        } else {
            "redirect:/register"
        }
    }
}