package net.bosowski.chattergpt.services.authentication

import net.bosowski.chattergpt.data.models.authentication.*
import net.bosowski.chattergpt.data.repositories.authentication.LoginRepository
import net.bosowski.chattergpt.data.repositories.authentication.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

//@Controller
@RestController
class UserService : OidcUserService() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var loginRepository: LoginRepository

    private val oauth2UserService = DefaultOAuth2UserService()

    @GetMapping("/login")
    fun authenticate(): RedirectView {
        return RedirectView("/oauth2/authorization/google")
    }

    @GetMapping("/oauth/token")
    fun getToken(@AuthenticationPrincipal user: OauthUser): RedirectView {
        return RedirectView("https://accounts.google.com/o/oauth2/auth")
    }

    @GetMapping("/oauth/callback")
    fun oauthCallback(request: HttpServletRequest, response: HttpServletResponse) {
        val jwtToken = "generated_jwt_token"
        val redirectUrl = "chrome-extension://oimihdlbjgcjllfogekhoglpdgakjgoj/popup.html?jwt_token=$jwtToken"

        response.sendRedirect(redirectUrl)
    }

    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val loadedUser = oauth2UserService.loadUser(userRequest)
        val username = loadedUser.attributes["email"] as String
        var user = userRepository.findByUsername(username)
        if (user == null) {
            user = OauthUser()
            user.username = username
        }

        loadedUser.attributes.forEach { loadedAttribute ->
            val userAttribute = user.oauthAttributes.find { loadedAttribute.key == it.attributeKey && it.active }
            val newAttribute = OauthAttribute(loadedAttribute.key, loadedAttribute.value?.toString())
            if(userAttribute == null){
                user.oauthAttributes.add(newAttribute)
            }
            else if(userAttribute.attributeValue != loadedAttribute.value?.toString()){
                userAttribute.active = false
                user.oauthAttributes.add(newAttribute)
            }
        }
        user.oauthAuthorities = loadedUser.authorities.map { OauthAuthority(it.authority) }.toMutableList()
        user.oauthToken = OauthToken(
            userRequest?.idToken?.tokenValue!!,
            Date.from(userRequest.idToken.issuedAt),
            Date.from(userRequest.idToken.expiresAt)
        )
        userRepository.save(user)
        val login = Login(user)
        loginRepository.save(login)
        return user
    }

}