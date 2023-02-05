package net.bosowski.chattergpt.services.ai

import com.fasterxml.jackson.databind.ObjectMapper
import net.bosowski.chattergpt.data.repositories.authentication.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

@RestController
class AIService {

    @Value("\${openai.api.url}")
    private lateinit var openaiApiUrl: String

    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping("/api/ai")
    fun getResponse(@AuthenticationPrincipal jwt: Jwt): String {
        val email = jwt.claims["email"] as String
        val oauthUser = userRepository.findByUsername(email)
        return "Hello"
    }

}