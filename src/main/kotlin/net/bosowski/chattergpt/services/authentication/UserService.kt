package net.bosowski.chattergpt.services.authentication

import net.bosowski.chattergpt.data.models.authentication.*
import net.bosowski.chattergpt.data.repositories.authentication.OauthAttributeRepository
import net.bosowski.chattergpt.data.repositories.authentication.OauthAuthorityRepository
import net.bosowski.chattergpt.data.repositories.authentication.LoginRepository
import net.bosowski.chattergpt.data.repositories.authentication.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService : OidcUserService() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var authorityRepository: OauthAuthorityRepository

    @Autowired
    lateinit var attributeRepository: OauthAttributeRepository

    @Autowired
    lateinit var loginRepository: LoginRepository

    private val oauth2UserService = DefaultOAuth2UserService()

    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val loadedUser = oauth2UserService.loadUser(userRequest)
        val clientId = loadedUser.attributes["sub"] as String
        var user = userRepository.findByOauthId(clientId)
        if (user == null) {
            user = OauthUser()
            user.oauthId = clientId
            user.oauthToken = OauthToken(
                userRequest?.idToken?.tokenValue!!,
                Date.from(userRequest.accessToken.issuedAt),
                Date.from(userRequest.accessToken.expiresAt)
            )
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

        userRepository.save(user)
        val login = Login(user)
        loginRepository.save(login)
        return user
    }

}