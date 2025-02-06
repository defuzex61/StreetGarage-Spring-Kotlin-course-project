package com.example.streetgarage.controllers

import com.example.streetgarage.services.AdminService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminController(private val adminService: AdminService) {

    // Страница админки
    @GetMapping("/dashboard")
    fun dashboard(model: Model): String {
        val userCarsData = adminService.findAllUserCarsData()
        model.addAttribute("userCarsData", userCarsData)
        return "adminDashboard" // Имя шаблона Thymeleaf
    }

    // Экспорт данных в CSV
    @GetMapping("/export-csv")
    fun exportUserCarsToCsv(response: HttpServletResponse) {
        val csvBytes = adminService.exportUserCarsToCsv()

        // Настройка ответа
        response.contentType = "text/csv"
        response.setHeader("Content-Disposition", "attachment; filename=user_cars_export.csv")
        response.outputStream.write(csvBytes)
        response.outputStream.flush()
    }
}