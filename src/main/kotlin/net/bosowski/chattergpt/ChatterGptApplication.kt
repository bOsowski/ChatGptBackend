package net.bosowski.chattergpt

import net.bosowski.chattergpt.services.authentication.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.interfaces.RSAPublicKey
import java.util.*


@SpringBootApplication
@RestController
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class ChatterGptApplication {

    @Autowired
    lateinit var userService: UserService


//    @Value("\${spring.security.oauth2.resourceserver.jwt.key-value}")
//    lateinit var key: RSAPublicKey

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
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests{ authorize ->
//            authorize.antMatchers("/", "/login", "/error", "/webjars/**").permitAll().anyRequest().authenticated()
            authorize.antMatchers("/api/ai").authenticated()//.hasAuthority("ROLE_USER")
                .antMatchers("/", "/login", "/error", "/webjars/**").permitAll()
                .anyRequest().authenticated()
        }
            .logout {
                it.logoutSuccessUrl("/").permitAll()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .oauth2Login { oauth2Login ->
                oauth2Login.loginPage("/login")
                oauth2Login.defaultSuccessUrl("/user", true)
            }
            .oauth2Client().and()
            .oauth2ResourceServer().jwt()

//            .oauth2ResourceServer{ oauth2ResourceServer ->
//                oauth2ResourceServer.jwt{ jwt ->
//                    jwt.decoder(jwtDecoder())
//                }
//            }
        return http.build()
    }

//    @Bean
//    fun jwtDecoder(): JwtDecoder {
//        return NimbusJwtDecoder.withPublicKey(key).build()
//    }



//    @Bean
//    fun filterChain(http: HttpSecurity): SecurityFilterChain {
//        http.authorizeHttpRequests {
//            it.antMatchers("/", "/login", "/error", "/webjars/**").permitAll().anyRequest().authenticated()
//        }
//            //            .oauth2ResourceServer().jwt().and().and()
//            .logout {
//                it.logoutSuccessUrl("/").permitAll()
//            }
//            .exceptionHandling {
//                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//            }
//            .oauth2Login { oauth2Login ->
//                oauth2Login.loginPage("/login")
//                oauth2Login.defaultSuccessUrl("/user", true)
//            }
//            .oauth2Client { oauth2Client -> }
//            .csrf {
//                it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//            }
//        return http.build()
//    }

}

fun main(args: Array<String>) {
    runApplication<ChatterGptApplication>(*args)
}
