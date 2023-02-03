package net.bosowski.chattergpt.data.controllers

import net.bosowski.chattergpt.data.models.OauthUser
import net.bosowski.chattergpt.data.repositories.UserRepository
import org.apache.catalina.security.SecurityConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WebAppConfiguration
@SpringBootTest
class CreditControllerTest {

    @Autowired
    private lateinit var creditController: CreditController

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    @WithMockUser
    fun purchase() {
        val user = OauthUser()
        user.id = "test_123"
        userRepository.save(user)
        var redirectView = creditController.purchase(user)
    }

    @Test
    fun success() {
    }
}