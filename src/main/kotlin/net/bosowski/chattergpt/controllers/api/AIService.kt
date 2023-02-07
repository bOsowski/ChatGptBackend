package net.bosowski.chattergpt.controllers.api

import net.bosowski.chattergpt.data.models.ai.ChatRequest
import net.bosowski.chattergpt.data.dtos.ModelRequestDto
import net.bosowski.chattergpt.data.dtos.ApiResponseDto
import net.bosowski.chattergpt.data.models.ai.ApiResponse
import net.bosowski.chattergpt.data.models.authentication.OauthUser
import net.bosowski.chattergpt.data.repositories.ai.*
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

    @Autowired
    private lateinit var modelPricingRepository: ModelPricingRepository

    @Autowired
    private lateinit var chatRequestRepository: ChatRequestRepository

    @PostMapping("/chat")
    fun getResponse(@AuthenticationPrincipal jwt: Jwt, @RequestBody chatRequest: ChatRequest): ResponseEntity<String> {
        val email = jwt.claims["email"] as String
        val user = userRepository.findByUsername(email)
        if(user == null) {
            return ResponseEntity("User not registered.", org.springframework.http.HttpStatus.UNAUTHORIZED)
        }

        val participants = chatRequest.messages.map { it.sender }.distinct().toMutableList()
        val prompt = """The below chat is between me and ${participants.filter { it != "Me" }.joinToString(",") }. Create a response as if you were me. Don't use much punctuation and keep in mind that today's date is ${Date()}.
            
${chatRequest.messages.joinToString(separator = "\n") { "${it.sender}:${it.message}" }}
Me:"""

        if(user.availableCredits < getMaxCost(chatRequest)){
            chatRequestRepository.save(chatRequest)
            return ResponseEntity("Not enough credits.", HttpStatus.PAYMENT_REQUIRED)
        }

        val modelRequest = ModelRequestDto()
        modelRequest.model = chatRequest.model
        modelRequest.prompt = prompt
        modelRequest.stop = participants.map { "$it:" }.toMutableList()

        val response = performRequest(modelRequest)
        val apiResponse = ApiResponse(response.body, user)
        chatRequest.apiResponse = apiResponse

        if(response.body == null ){
            chatRequestRepository.save(chatRequest)
            return ResponseEntity("Invalid response from OpenAI.", HttpStatus.NO_CONTENT)
        }
        val modelResponse: ApiResponseDto = response.body!!

        val charge = chargeUser(user, modelResponse)
        chatRequest.cost = charge
        chatRequestRepository.save(chatRequest)
        return ResponseEntity(modelResponse.choices?.first()?.text, HttpStatus.OK)
    }

    private fun performRequest(modelRequest: ModelRequestDto): ResponseEntity<ApiResponseDto> {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $openaiApiKey")
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(modelRequest, headers)
        return restTemplate.postForEntity(openaiApiUrl, request, ApiResponseDto::class.java)
    }

    private fun chargeUser(user: OauthUser, response: ApiResponseDto): Double{
        val requestCost = getPricePerToken(response.model!!) * response.usage?.total_tokens!!
        user.availableCredits = user.availableCredits - requestCost
        userRepository.save(user)
        return requestCost
    }

    private fun getMaxCost(chatRequest: ChatRequest): Double {
        return getPricePerToken(chatRequest.model) * chatRequest.maxTokens
    }

    private fun getPricePerToken(model: String): Double {
        val modelPricing = modelPricingRepository.findById(model).get()
        return modelPricing.price / 1000.0
    }

    @PostMapping("/testing")
    fun test(request: HttpServletRequest, @RequestBody(required = false) requestBody: String?): ResponseEntity<String> {
        return ResponseEntity("$requestBody", org.springframework.http.HttpStatus.OK)
    }

}