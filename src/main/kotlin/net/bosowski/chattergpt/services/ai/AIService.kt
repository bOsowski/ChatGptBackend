package net.bosowski.chattergpt.services.ai

import com.fasterxml.jackson.databind.ObjectMapper
import net.bosowski.chattergpt.data.models.authentication.OauthUser
import net.bosowski.chattergpt.data.repositories.authentication.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException.Unauthorized
import org.springframework.web.servlet.view.RedirectView
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
    fun getResponse(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<String> {
        val email = jwt.claims["email"] as String
        val oauthUser = userRepository.findByUsername(email)
        if(oauthUser == null){
            return ResponseEntity("User not registered.", org.springframework.http.HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity("Hello world!", org.springframework.http.HttpStatus.OK)
    }

}