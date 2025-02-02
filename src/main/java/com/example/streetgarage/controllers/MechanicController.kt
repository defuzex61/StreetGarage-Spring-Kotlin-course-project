package com.example.streetgarage.controllers

import com.example.streetgarage.dto.UserDTO
import com.example.streetgarage.models.RequestWork
import com.example.streetgarage.models.UserRole
import com.example.streetgarage.models.Users
import com.example.streetgarage.services.RequestService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@Controller
@RequestMapping("/mechanic")
class MechanicController<UserDTO>(private val requestService: RequestService) {

    // Личный кабинет механика
    @GetMapping("/dashboard")
    fun mechanicDashboard(model: Model, session: HttpSession): String {
        val user = session.getAttribute("user") as? com.example.streetgarage.dto.UserDTO
            ?: return "redirect:/login" // Если данные отсутствуют, перенаправляем на страницу входа
        println(user.role)
        if (user.role != "mechanic") {
            return "redirect:/dashboard" // Если пользователь не механик, перенаправляем на обычный кабинет
        }

        val userId = user.idUser

        // Получаем заказы со статусом New
        val newRequests = requestService.getNewRequests()
        // Получаем заказы, назначенные на этого механика
        val myRequests = requestService.getRequestsByMechanicId(userId ?: 0)

        model.addAttribute("user", user)
        model.addAttribute("newRequests", newRequests)
        model.addAttribute("myRequests", myRequests)
        return "mechanicDashboard"
    }

    // Взять заказ
    @PostMapping("/take-request")
    fun takeRequest(@RequestParam requestId: Long, session: HttpSession): String {
        val userDTO = session.getAttribute("user") as? com.example.streetgarage.dto.UserDTO
            ?: return "redirect:/login"

        val user = Users(userDTO.idUser,userDTO.lastName,userDTO.firstName,userDTO.middleName, userDTO.passwordHash, userDTO.login, userDTO.email, userDTO.phone, UserRole.mechanic)
        requestService.takeRequest(requestId, user)
        return "redirect:/mechanic/dashboard"
    }

    // Завершить заказ
    @PostMapping("/complete-request")
    fun completeRequest(
        @RequestParam requestId: Long,
        @ModelAttribute requestWork: RequestWork,
        session: HttpSession
    ): String {
        val user = session.getAttribute("user") as? com.example.streetgarage.dto.UserDTO
            ?: return "redirect:/login"

        // Устанавливаем актуальное время завершения
        requestWork.completionDate = LocalDateTime.now().toString() // Установите актуальное время завершения

        // Завершение заказа
        requestService.completeRequest(requestId, requestWork)
        return "redirect:/mechanic/dashboard"
    }
}