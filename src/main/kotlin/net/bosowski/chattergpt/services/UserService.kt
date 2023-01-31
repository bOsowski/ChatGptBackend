package net.bosowski.chattergpt.services

import net.bosowski.chattergpt.data.models.Login
import net.bosowski.chattergpt.data.models.OauthAttribute
import net.bosowski.chattergpt.data.models.OauthAuthority
import net.bosowski.chattergpt.data.models.OauthUser
import net.bosowski.chattergpt.data.repositories.OauthAttributeRepository
import net.bosowski.chattergpt.data.repositories.OauthAuthorityRepository
import net.bosowski.chattergpt.data.repositories.LoginRepository
import net.bosowski.chattergpt.data.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service

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

    fun login(authorities: Collection<GrantedAuthority>, attributes: Map<String, Any>, idToken: String): OauthUser {
        val login = Login()

        val email = attributes["email"] as String
        var foundUser = userRepository.findByEmail(email)

        val oauthAttributes = ArrayList<OauthAttribute>()
        attributes.forEach {
            oauthAttributes.add(OauthAttribute(it.key, it.value.toString()))
        }

        if (foundUser != null) {
            attributeRepository.findAllByUserIdAndActiveTrue(foundUser.id!!).forEach { existingAttribute ->
                val newCorrespondingAttribute =
                    oauthAttributes.find { existingAttribute.attributeKey == it.attributeKey }
                if (newCorrespondingAttribute?.attributeValue != existingAttribute.attributeValue) {
                    existingAttribute.active = false
                    attributeRepository.save(existingAttribute)
                    newCorrespondingAttribute!!.user = foundUser
                    attributeRepository.save(newCorrespondingAttribute)
                }
            }
            foundUser.oauthAttributes =attributeRepository.findAllByUserIdAndActiveTrue(foundUser.id!!)
            registerLogin(foundUser)
            return foundUser
        }

        val userAuthorities = ArrayList<OauthAuthority>()
        val existingAuthorities = authorityRepository.findAll()
        authorities.forEach{ grantedAuthority ->
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
            email = email,
            firstName = attributes["given_name"] as String,
            lastName = attributes["family_name"] as String
        )

        val newUser = userRepository.save(user)
        oauthAttributes.forEach { it.user = newUser; attributeRepository.save(it) }
        registerLogin(newUser)
        return newUser
    }

    private fun registerLogin(oauthUser: OauthUser){
        loginRepository.save(Login(oauthUser))
    }


    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val oidcUser = super.loadUser(userRequest)
        login(oidcUser.authorities, oidcUser.attributes, oidcUser.idToken.tokenValue)
        return oidcUser
    }

}