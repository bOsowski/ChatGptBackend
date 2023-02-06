package net.bosowski.chattergpt.controllers.api

import net.bosowski.chattergpt.data.models.ai.ChatRequest
import net.bosowski.chattergpt.data.dtos.ModelRequestDto
import net.bosowski.chattergpt.data.dtos.ModelResponseDto
import net.bosowski.chattergpt.data.repositories.authentication.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
class AIService {

    @Value("\${openai.api.url}")
    private lateinit var openaiApiUrl: String

    @Value("\${openai.api.key}")
    private lateinit var openaiApiKey: String

    @Autowired
    private lateinit var userRepository: UserRepository

    @PostMapping("/chat")
    fun getResponse(@AuthenticationPrincipal jwt: Jwt, @RequestBody chatRequest: ChatRequest): ResponseEntity<String> {
        val email = jwt.claims["email"] as String
        val oauthUser = userRepository.findByUsername(email)
        if(oauthUser == null) {
            return ResponseEntity("User not registered.", org.springframework.http.HttpStatus.UNAUTHORIZED)
        }

        val participants = chatRequest.messages.map { it.sender }.distinct().toMutableList()
        val prompt = """The below chat is between me and ${participants.filter { it != "Me" }.joinToString(",") }. Create a response as if you were me. Don't use much punctuation and keep in mind that today's date is ${Date()}.
            
${chatRequest.messages.joinToString(separator = "\n") { "${it.sender}:${it.message}" }}
Me:"""

        val modelRequest = ModelRequestDto()
        modelRequest.model = chatRequest.model
        modelRequest.prompt = prompt
        modelRequest.stop = participants.map { "$it:" }.toMutableList()

        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $openaiApiKey")
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(modelRequest, headers)
        val modelResponse = restTemplate.postForEntity(openaiApiUrl, request, ModelResponseDto::class.java)
        return ResponseEntity(modelResponse.body?.choices?.first()?.text, org.springframework.http.HttpStatus.OK)
    }

    @PostMapping("/testing")
    fun test(request: HttpServletRequest, @RequestBody(required = false) requestBody: String?): ResponseEntity<String> {
        return ResponseEntity("$requestBody", org.springframework.http.HttpStatus.OK)
    }

}