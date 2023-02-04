package net.bosowski.chattergpt.data.controllers

import net.bosowski.chattergpt.data.models.authentication.OauthUser
import net.bosowski.chattergpt.data.repositories.authentication.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.web.WebAppConfiguration

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
        user.oauthId = "test_123"
        userRepository.save(user)
        var redirectView = creditController.purchase(user)
        assert(redirectView.url?.contains("stripe.com") == true)
    }

    @Test
    fun success() {
    }
}