package com.example.streetgarage.controllers

import com.example.streetgarage.dto.UserDTO
import com.example.streetgarage.models.*
import com.example.streetgarage.services.ServicesService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDateTime

@Controller
class ServicePageController(
    private val servicesService: ServicesService,
    private val session: HttpSession
) {
    @GetMapping("/services")
    fun servicesPage(model: Model): String {
        val user = session.getAttribute("user") as? UserDTO

        val userId = user?.idUser
        val cars = servicesService.getUserCars(userId)
        val workTypes = servicesService.getAllWorkTypes()

        model.addAttribute("workTypes", workTypes)
        model.addAttribute("cars", cars)

        return "servicesPage"
    }

    @PostMapping("/create-request")
    fun createRequest(
        @RequestParam("plannedDate") plannedDate: String,
        @RequestParam("carId") carId: Long,
        @RequestParam("workTypeId") workTypeId: Long,
        @RequestParam("comment") comment: String?,
        model: Model
    ): String {
        try {
            // Получаем пользователя из сессии
            val userDTO = session.getAttribute("user") as? UserDTO
                ?: return "redirect:/services?error=notAuthorized"

            val workType = servicesService.getWorkTypeById(workTypeId)
                ?: return "redirect:/services?error=workTypeNotFound"
            val user = Users(userDTO.idUser,userDTO.lastName,userDTO.firstName,userDTO.middleName,userDTO.passwordHash,userDTO.login,userDTO.email,userDTO.phone,UserRole.client )
            // Создаем заявку
            val request = servicesService.getCarById(carId)?.let {
                Request(
                    plannedDate = plannedDate,
                    car = it,
                    client = user,
                    status = RequestStatus.New,
                    creationDate = LocalDateTime.now().toString(),
                    completionDate = null,
                    mechanic = null
                )
            }

            // Вызываем сервис для создания заявки
            val result = request?.let { servicesService.createRequest(it, workType, comment) }

            if (result!!.isSuccessful) {
                model.addAttribute("successMessage", "Заявка успешно создана!")
            } else {
                model.addAttribute("errorMessage", result.errorMessage)
            }
        } catch (e: Exception) {
            model.addAttribute("errorMessage", "Ошибка при создании заявки: ${e.message}")
        }

        return "redirect:/services"
    }
}