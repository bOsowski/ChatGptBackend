package net.bosowski.chattergpt.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.cors.*


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class WebConfiguration {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }

    @Bean
    @Order(HIGHEST_PRECEDENCE)
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.antMatcher("/api/**").authorizeRequests { authorize ->
            authorize.antMatchers("/api/**").authenticated()
        }.exceptionHandling {
            it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        }
            .cors().configurationSource { CorsConfiguration().applyPermitDefaultValues() }.and()
            .csrf().disable()
            .oauth2ResourceServer().jwt()
        return http.build()
    }

    @Bean
    fun uiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests { authorize ->
            authorize.antMatchers("/", "/login", "/error", "/webjars/**").permitAll().anyRequest()
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