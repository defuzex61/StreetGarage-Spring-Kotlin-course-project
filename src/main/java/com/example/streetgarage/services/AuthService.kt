import com.example.streetgarage.models.UserLoginRequest
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

@Service
class AuthService(private val restTemplate: RestTemplate) {

    private val apiBaseUrl = "https://localhost:8081/api/users/" // Замените на ваш URL API

    fun loginUser(request: UserLoginRequest): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.set("Content-Type", "application/json") // Устанавливаем заголовок для JSON

        val requestEntity = HttpEntity(request, headers) // Создаем HTTP-запрос с телом и заголовками
        val response = restTemplate.exchange("$apiBaseUrl/login", HttpMethod.POST, requestEntity, String::class.java)

        return when {
            response.statusCode.is2xxSuccessful -> ResponseEntity.ok("Login successful: ${response.body}")
            else -> ResponseEntity.status(response.statusCode).body("Login failed: ${response.body}")
        }
    }
}