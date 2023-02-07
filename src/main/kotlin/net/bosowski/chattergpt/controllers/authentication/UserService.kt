package net.bosowski.chattergpt.controllers.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import net.bosowski.chattergpt.data.models.authentication.*
import net.bosowski.chattergpt.data.repositories.ai.ChatRequestRepository
import net.bosowski.chattergpt.data.repositories.authentication.LoginRepository
import net.bosowski.chattergpt.data.repositories.authentication.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.util.*

//@Controller
@RestController
class UserService: OidcUserService() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var loginRepository: LoginRepository

    @Autowired
    lateinit var chatRequestRepository: ChatRequestRepository

    private val oauth2UserService = DefaultOAuth2UserService()

    @GetMapping("/login")
    fun authenticate(): RedirectView {
        return RedirectView("/oauth2/authorization/google")
    }

    @GetMapping("/user")
    fun user(@AuthenticationPrincipal principal: OAuth2User): Any {
        val user = userRepository.findByUsername(principal.attributes["email"] as String) !!
        val requests = chatRequestRepository.findAllByOauthUser(user)
        val mapper = ObjectMapper()
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper.writeValueAsString(
         linkedMapOf(
            "credits" to user.availableCredits,
            "requests" to requests.map {
                linkedMapOf(
                    "model" to it.model,
                    "tokens" to it.apiResponse?.usage?.totalTokens,
                    "cost" to it.cost,
                    "response" to it.apiResponse?.choices?.first()?.text
                )
            }).toString())
    }

    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val loadedUser = oauth2UserService.loadUser(userRequest)
        val username = loadedUser.attributes["email"] as String
        var user = userRepository.findByUsername(username)
        if(user == null) {
            user = OauthUser()
            user.username = username
        }

        loadedUser.attributes.forEach { loadedAttribute ->
            val userAttribute = user.oauthAttributes.find { loadedAttribute.key == it.attributeKey && it.active }
            val newAttribute = OauthAttribute(loadedAttribute.key, loadedAttribute.value?.toString())
            if(userAttribute == null) {
                user.oauthAttributes.add(newAttribute)
            }
            else if(userAttribute.attributeValue != loadedAttribute.value?.toString()) {
                userAttribute.active = false
                user.oauthAttributes.add(newAttribute)
            }
        }
        user.oauthAuthorities = loadedUser.authorities.map { OauthAuthority(it.authority) }.toMutableList()
        user.oauthToken = OauthToken(
            userRequest?.idToken?.tokenValue !!,
            Date.from(userRequest.idToken.issuedAt),
            Date.from(userRequest.idToken.expiresAt)
        )
        userRepository.save(user)
        val login = Login(user)
        loginRepository.save(login)
        return user
    }
}