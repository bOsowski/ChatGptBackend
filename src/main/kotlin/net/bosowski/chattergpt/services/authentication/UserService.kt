package net.bosowski.chattergpt.services.authentication

import net.bosowski.chattergpt.data.models.authentication.*
import net.bosowski.chattergpt.data.repositories.authentication.OauthAttributeRepository
import net.bosowski.chattergpt.data.repositories.authentication.OauthAuthorityRepository
import net.bosowski.chattergpt.data.repositories.authentication.LoginRepository
import net.bosowski.chattergpt.data.repositories.authentication.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList

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


    fun login(authorities: Collection<GrantedAuthority>, attributes: Map<String, Any>, idToken: String): OauthUser {
        val email = attributes["email"] as String
//        var foundUser = userRepository.findByEmail(email)

        val oauthAttributes = ArrayList<OauthAttribute>()
        attributes.forEach {
            oauthAttributes.add(OauthAttribute(it.key, it.value.toString()))
        }

//        if (foundUser != null) {
//            attributeRepository.findAllByUserIdAndActiveTrue(foundUser.id!!).forEach { existingAttribute ->
//                val newCorrespondingAttribute =
//                    oauthAttributes.find { existingAttribute.attributeKey == it.attributeKey }
//                if (newCorrespondingAttribute?.attributeValue != existingAttribute.attributeValue) {
//                    existingAttribute.active = false
//                    attributeRepository.save(existingAttribute)
//                    newCorrespondingAttribute!!.user = foundUser
//                    attributeRepository.save(newCorrespondingAttribute)
//                }
//            }
//            foundUser.oauthAttributes =attributeRepository.findAllByUserIdAndActiveTrue(foundUser.id!!)
//            registerLogin(foundUser)
//            return foundUser
//        }

        val userAuthorities = ArrayList<OauthAuthority>()
        val existingAuthorities = authorityRepository.findAll()
        authorities.forEach { grantedAuthority ->
            val existingAuthority = existingAuthorities.find { it.authority == grantedAuthority.authority }
            if (existingAuthority == null) {
                val newAuthority = OauthAuthority(grantedAuthority.authority)
                authorityRepository.save(newAuthority)
                userAuthorities.add(newAuthority)
            } else {
                userAuthorities.add(existingAuthority)
            }
        }

        val user = OauthUser(
            oauthAttributes = oauthAttributes,
            oauthAuthorities = userAuthorities,
        )

        val newUser = userRepository.save(user)
        registerLogin(newUser)
        return newUser
    }

    private fun registerLogin(oauthUser: OauthUser) {
        loginRepository.save(Login(oauthUser))
    }

    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val loadedUser = oauth2UserService.loadUser(userRequest)
        val oauthUser = OauthUser()
        oauthUser.oauthId = userRequest?.clientRegistration?.clientId
        oauthUser.oauthAttributes = loadedUser.attributes.map { OauthAttribute(it.key, it.value?.toString()) }
        oauthUser.oauthAuthorities = loadedUser.authorities.map { OauthAuthority(it.authority) }
        oauthUser.oauthToken = OauthToken(
            userRequest?.idToken?.tokenValue!!,
            Date.from(userRequest.accessToken.issuedAt),
            Date.from(userRequest.accessToken.expiresAt)
        )
        return userRepository.save(oauthUser)
    }

}