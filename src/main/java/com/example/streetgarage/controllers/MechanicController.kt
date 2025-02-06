package com.example.streetgarage.controllers

import com.example.streetgarage.dto.UserDTO
import com.example.streetgarage.models.*

import com.example.streetgarage.services.RequestService
import com.example.streetgarage.services.Result
import jakarta.servlet.http.HttpSession
import org.apache.commons.logging.Log
import org.springframework.http.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.logging.Logger

@Controller
@RequestMapping("/mechanic")
class MechanicController<UserDTO>(private val requestService: RequestService, private val restTemplate: RestTemplate) {

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

    @PostMapping("/update-planned-date")
    fun updatePlannedDate(
        @RequestParam("requestId") requestId: Long,
        @RequestParam("plannedDate") plannedDate: String,
        model: Model
    ): String {
        try {
            // Обновляем планируемую дату завершения
            requestService.updatePlannedDate(requestId, plannedDate)
            model.addAttribute("successMessage", "Планируемая дата успешно обновлена!")
        } catch (e: Exception) {
            model.addAttribute("errorMessage", "Ошибка при обновлении даты: ${e.message}")
        }

        return "redirect:/mechanic/dashboard"
    }

    // Взять заказ
    @PostMapping("/take-request")
    fun takeRequest(@RequestParam requestId: Long, session: HttpSession, model: Model): String {
        val userDTO = session.getAttribute("user") as? com.example.streetgarage.dto.UserDTO
            ?: return "redirect:/login"

        val user = Users(userDTO.idUser,userDTO.lastName,userDTO.firstName,userDTO.middleName, userDTO.passwordHash, userDTO.login, userDTO.email, userDTO.phone, UserRole.mechanic)
        requestService.takeRequest(requestId, user)

        // Назначаем механика и изменяем статус на In_progress
        model.addAttribute("successMessage", "Заказ успешно взят!")

    return "redirect:/mechanic/dashboard"
    }

    @PostMapping("/complete-request")
    fun completeRequest(
        @RequestParam requestId: Long,
        @RequestParam workComment: String?,
        @RequestParam(required = false) newPartName: String,
        @RequestParam(required = false) newPartArticle: String,
        @RequestParam(required = false) newPartPrice: Double,
        @RequestParam(required = false) newPartQuantity: Int,
        @RequestParam(required = false) newWorkTypeName: String,
        @RequestParam(required = false) newWorkTypeDescription: String?,
        @RequestParam(required = false) newWorkTypePrice: Double?,
        model: Model,
        session: HttpSession
    ): Any? {
        val user = session.getAttribute("user") as? UserDTO
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized")

        // Передаем данные в сервис для обработки
        val result = requestService.completeRequest(
            requestId,
            workComment,
            newPartName,
            newPartArticle,
            newPartPrice,
            newPartQuantity,
            newWorkTypeName,
            newWorkTypeDescription,
            newWorkTypePrice
        )
        val newWorkType = WorkType(name = newWorkTypeName, description = newWorkTypeDescription, price = newWorkTypePrice ?: 0.0)
        val newPart = Part(partName = newPartName, article = newPartArticle, price = newPartPrice, quantity = newPartQuantity)
        val request = restTemplate.getForObject("/api/requests/$requestId", Request::class.java) ?: return Result(false, "Заказ не найден")
        val requestWork = restTemplate.getForObject("/api/request-works/$requestId", Request::class.java) ?: return Result(false, "Заказ не найден")
        println(requestId.toString())
        model.addAttribute("user", user)
        model.addAttribute("newRequests", requestWork)
        model.addAttribute("myRequest", request)
        model.addAttribute("requestId", requestId) // Также можно добавить ID отдельноя
        model.addAttribute("workTypes", newWorkType) // Добавляем типы работ
        model.addAttribute("parts", newPart)
        return if (result.isSuccessful) {
            ResponseEntity.ok("Заказ успешно завершен")
            "mechanicDashboard"
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при завершении заказа: ${result.errorMessage}")
        }
    }


}