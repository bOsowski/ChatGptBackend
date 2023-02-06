package net.bosowski.chattergpt.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import java.util.stream.Stream


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class WebConfiguration {

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    @Order(HIGHEST_PRECEDENCE)
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.antMatcher("/api/**").authorizeRequests { authorize ->
            authorize.antMatchers("/api/**").authenticated()
        }.exceptionHandling {
            it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        }
            .csrf().disable()
            .oauth2ResourceServer().jwt()
        return http.build()
    }

    @Bean
    fun uiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests { authorize ->
            authorize.antMatchers("/", "/login", "/error", "/webjars/**", "/testing").permitAll().anyRequest()
                .authenticated()
        }.logout {
            it.logoutSuccessUrl("/").permitAll()
        }.exceptionHandling {
            it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        }.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .oauth2Login { oauth2Login ->
                oauth2Login.loginPage("/login")
                oauth2Login.defaultSuccessUrl("/", true)
            }.oauth2Client()
        return http.build()
    }

}