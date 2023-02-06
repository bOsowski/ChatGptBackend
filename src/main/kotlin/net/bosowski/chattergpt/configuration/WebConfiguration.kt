package net.bosowski.chattergpt.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class WebConfiguration {

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests { authorize ->
            authorize
                .antMatchers("/api/ai").authenticated()
                .antMatchers("/", "/login", "/error", "/webjars/**").permitAll().anyRequest().authenticated()
        }.logout {
            it.logoutSuccessUrl("/").permitAll()
        }.exceptionHandling {
            it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        }.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
            .oauth2Login { oauth2Login ->
                oauth2Login.loginPage("/login")
                oauth2Login.defaultSuccessUrl("/", true)
            }.oauth2Client().and().oauth2ResourceServer().jwt()

        return http.build()
    }
}