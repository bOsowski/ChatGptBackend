package net.bosowski.chattergpt.data.controllers

import org.springframework.beans.factory.annotation.Value

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j
import net.bosowski.chattergpt.data.models.CreditPurchase
import net.bosowski.chattergpt.data.repositories.CreditPurchaseRepository
import net.bosowski.chattergpt.data.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.servlet.view.RedirectView
import java.lang.Exception
import javax.servlet.http.HttpServletRequest

@Controller
@Slf4j
class CreditController {

    @Autowired
    lateinit var creditPurchaseRepository: CreditPurchaseRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Value("\${stripe.api.key}")
    lateinit var stripeApiKey: String

    @Value("\${stripe.api.product}")
    lateinit var stripeProduct: String

    val baseUrl = "http://localhost:8080"  //ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
    val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/purchase")
    fun purchase(): RedirectView {
        Stripe.apiKey = stripeApiKey
        val sessionCreateParams = SessionCreateParams.builder()
            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(String.format("%s/success?session_id={CHECKOUT_SESSION_ID}", baseUrl))
            .setCancelUrl(String.format("%s/cancel", baseUrl))
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPrice(stripeProduct)
                    .build()
            )
            .build();

        val session = Session.create(sessionCreateParams)
        val redirectView = RedirectView(session.url)
        redirectView.setStatusCode(HttpStatus.SEE_OTHER)

        val creditPurchase = CreditPurchase()
        val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val authenticatedUser = authentication.principal as DefaultOidcUser
        val foundUser = userRepository.findByEmail(authenticatedUser.idToken.claims["email"].toString())
        if(foundUser != null){
            creditPurchase.oauthUser = foundUser
            creditPurchase.credits = 10f    // todo: change this.
            creditPurchase.sessionId = session.id
            creditPurchaseRepository.save(creditPurchase)
            return redirectView
        }
        else{
            throw Exception("User not found")
        }
    }

    @GetMapping("/success")
    fun success(request: HttpServletRequest?, @RequestBody(required = false) body: String?): RedirectView {
        var response = Session.retrieve(request?.getParameter("session_id"))
        var creditPurchase = creditPurchaseRepository.findBySessionId(response.id)
        if(creditPurchase != null){
            if(response.status == "complete"){
                creditPurchase.completed = true
            }
            if(response.paymentStatus == "paid"){
                creditPurchase.successful = true
            }
            creditPurchase.oauthUser!!.availableCredits += creditPurchase.credits!!
            creditPurchaseRepository.save(creditPurchase)
//            userRepository.save(creditPurchase.oauthUser!!)
        }
        log.info(response.toString())
        return RedirectView("/")
    }

}