package com.example.streetgarage.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.*
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
open class SecurityConfig {
    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf // Настройка CSRF, если необходимо
                // Например, можно настроить исключения для определенных URL
                csrf.disable()
            }
            .authorizeHttpRequests { authz ->
                authz
                    .anyRequest().permitAll()
            }
            .formLogin { form ->
                form
                    .disable()
            }
            .logout { logout ->
                logout.permitAll()
                    .logoutUrl("/logout") // Указываем URL для выхода
                    .logoutSuccessUrl("/login") // Перенаправляем на страницу входа после выхода
                    .invalidateHttpSession(true) // Завершаем сессию
                    .deleteCookies("JSESSIONID") // Удаляем cookie сессии
            }

        return http.build()
    }
}