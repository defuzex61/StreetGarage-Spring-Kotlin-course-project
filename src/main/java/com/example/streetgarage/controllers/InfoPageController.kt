package com.example.streetgarage.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class InfoPageController {

    @GetMapping("/about")
    fun aboutPage(model: Model): String {
        model.addAttribute("title", "О нас")
        model.addAttribute("navbar", "nav") // Указываем фрагмент
        model.addAttribute("footer", "footer")
        return "about" // Возвращаем шаблон "О нас"
    }

    @GetMapping("/contacts")
    fun contactsPage(model: Model): String {
        model.addAttribute("title", "Контакты")
        model.addAttribute("navbar", "nav") // Указываем фрагмент
        model.addAttribute("footer", "footer")
        return "contacts" // Возвращаем шаблон "Контакты"
    }
}
