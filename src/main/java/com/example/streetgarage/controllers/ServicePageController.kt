package com.example.streetgarage.controllers

import com.example.streetgarage.services.ServicesService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ServicePageController(private val servicesService: ServicesService) {

    @GetMapping("/services")
    fun servicesPage(model: Model): String {
        val services = servicesService.getAllWorkTypes()
        val cars = servicesService.getAllCars()
        model.addAttribute("workTypes", services)
        model.addAttribute("cars", cars)
        return "servicesPage"
    }
}