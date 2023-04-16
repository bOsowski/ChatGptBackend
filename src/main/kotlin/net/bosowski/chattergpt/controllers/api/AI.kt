package net.bosowski.chattergpt.controllers.api

import net.bosowski.chattergpt.data.dtos.*
import net.bosowski.chattergpt.data.models.ai.*
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
import java.lang.Exception
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/ai")
class AI {

    @Value("\${openai.api.url}")
    private lateinit var openaiApiUrl: String

    @Value("\${openai.api.key}")
    private lateinit var openaiApiKey: String

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var modelPricingRepository: ModelPricingRepository

    @Autowired
    private lateinit var modelRequestRepository: ModelRequestRepository

    @PostMapping("/chatRequest")
    fun chatRequest(@AuthenticationPrincipal jwt: Jwt, @RequestBody chatRequestDto: ChatRequestDto): ResponseEntity<Any> {
        val email = jwt.claims["email"] as String
        val user = userRepository.findByUsername(email)
        if(user == null) {
            return ResponseEntity("User not registered.", HttpStatus.UNAUTHORIZED)
        }

        val participants = chatRequestDto.messages.map { it.sender }.distinct().toMutableList()
        val prompt = """The below chat is between me and ${
            participants.filter { it != "Me" }.joinToString(",")
        }. Create a response as if you were me. Don't use much punctuation and keep in mind that today's date is ${Date()}.
            
${chatRequestDto.messages.joinToString(separator = "\n") { "${it.sender}:${it.message}" }}
Me:"""

        val modelRequest = ModelRequest()
        modelRequest.prompt = prompt
        modelRequest.stop = participants.map { "$it:" }.toMutableList()
        modelRequest.oauthUser = user
        modelRequest.model = chatRequestDto.model
        modelRequest.maxTokens = chatRequestDto.maxTokens
        modelRequest.temperature = chatRequestDto.temperature
        modelRequestRepository.save(modelRequest)
        val apiResponse = performRequest(modelRequest)
        return ResponseEntity(apiResponse.body, HttpStatus.OK)
    }


    @PostMapping("/autocompleteRequest")
    fun getResponse(@AuthenticationPrincipal jwt: Jwt, @RequestBody text: String): ResponseEntity<Any> {
        val email = jwt.claims["email"] as String
        val user = userRepository.findByUsername(email) ?: return ResponseEntity("User not registered.", HttpStatus.UNAUTHORIZED)
        val modelRequest = ModelRequest()
        modelRequest.prompt =
            "The user is typing text into a text field on his phone. Your replies should only consist of the prediction. Be creative and try to predict the full contents of the text field, given the following message that is wrapped in quotes:\"${text}\""
        modelRequest.oauthUser = user
        modelRequest.temperature = 0.6f
        modelRequest.n = 3
        modelRequestRepository.save(modelRequest)
        val apiResponse = performRequest(modelRequest)
        return ResponseEntity(apiResponse.body, HttpStatus.OK)
    }

    private fun performRequest(modelRequest: ModelRequest): ResponseEntity<ApiResponseDto> {
        if(modelRequest.oauthUser !!.availableCredits < getMaxCost(modelRequest)) {
            throw Exception("Not enough credits!") //todo: create an exception class for this particular exception
        }
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $openaiApiKey")
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(modelRequest.toModelRequestDto(), headers)
        val response = restTemplate.postForEntity(openaiApiUrl, request, ApiResponseDto::class.java)
        val apiResponse = ApiResponse(response.body)
        modelRequest.apiResponse = apiResponse
        val charge = chargeUser(modelRequest.oauthUser !!, apiResponse)
        modelRequest.cost = charge
        modelRequestRepository.save(modelRequest)
        return response
    }

    private fun chargeUser(user: OauthUser, response: ApiResponse): Float {
        val requestCost = getPricePerToken(response.model !!) * response.usage?.totalTokens !!
        user.availableCredits = user.availableCredits - requestCost
        userRepository.save(user)
        return requestCost
    }

    private fun getMaxCost(modelRequest: ModelRequest): Float {
        return getPricePerToken(modelRequest.model) * modelRequest.maxTokens
    }

    private fun getPricePerToken(model: String): Float {
        val modelPricing = modelPricingRepository.findById(model).get()
        return modelPricing.price / 1000f
    }

    @PostMapping("/test")
    fun test(request: HttpServletRequest, @RequestBody(required = false) requestBody: String?): ResponseEntity<String> {
        return ResponseEntity("Test response.", HttpStatus.OK)
    }

}