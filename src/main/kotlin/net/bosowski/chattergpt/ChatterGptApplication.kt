package net.bosowski.chattergpt

import net.bosowski.chattergpt.data.models.OauthAttribute
import net.bosowski.chattergpt.data.models.OauthAuthority
import net.bosowski.chattergpt.data.models.User
import net.bosowski.chattergpt.data.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@SpringBootApplication
@RestController
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class ChatterGptApplication: OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    lateinit var userRepo: UserRepository

    @GetMapping("/user")
    fun user(@AuthenticationPrincipal principal: OAuth2User): Map<String?, Any?> {
        return Collections.singletonMap("name", principal.getAttribute("name"))
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests().antMatchers("/", "/error", "/webjars/**").permitAll().anyRequest()
            .authenticated().and()
            .logout().logoutSuccessUrl("/").permitAll().and()
            .exceptionHandling().authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and()
            .oauth2Login().and()
            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

        return http.build()
    }

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        var defaultUser = DefaultOAuth2UserService().loadUser(userRequest);
        var user = User()
        defaultUser.authorities.forEach { (user.oauthAuthorities as ArrayList).add(OauthAuthority(authorityString = it.authority)) }
        defaultUser.attributes?.forEach { (key, value) ->
            (user.oauthAttributes as ArrayList<OauthAttribute>).add(OauthAttribute(attributeKey = key, attributeValue = value?.toString()))
        }

        userRepo.save(user)

        return user
    }
}

fun main(args: Array<String>) {
    runApplication<ChatterGptApplication>(*args)
}
