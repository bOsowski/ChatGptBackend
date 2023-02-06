package net.bosowski.chattergpt.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import java.util.stream.Stream


@Configuration
@EnableWebSecurity
class WebConfiguration {

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    interface Jwt2AuthenticationConverter: Converter<Jwt?, AbstractAuthenticationToken?>
    interface Jwt2AuthoritiesConverter: Converter<Jwt?, Collection<GrantedAuthority?>?>

    @Bean
    fun authoritiesConverter(): Jwt2AuthoritiesConverter? = object: Jwt2AuthoritiesConverter {
        override fun convert(source: Jwt): Collection<GrantedAuthority?>? {
            val realmAccess = source.claims.getOrDefault(
                "realm_access", java.util.Map.of<Any, Any>()
            ) as Map<*, *>
            val realmRoles = (realmAccess["roles"] ?: listOf<Any>()) as Collection<*>
            val resourceAccess = source.claims.getOrDefault(
                "resource_access", java.util.Map.of<Any, Any>()
            ) as Map<*, *> // We assume here you have "spring-addons-confidential" and "spring-addons-public" clients configured with "client roles" mapper in Keycloak
            val confidentialClientAccess =
                (resourceAccess["spring-addons-confidential"] ?: java.util.Map.of<Any, Any>()) as Map<*, *>
            val confidentialClientRoles = (confidentialClientAccess["roles"] ?: listOf<Any>()) as Collection<*>
            val publicClientAccess =
                (resourceAccess["spring-addons-public"] ?: java.util.Map.of<Any, Any>()) as Map<*, *>
            val publicClientRoles = (publicClientAccess["roles"] ?: listOf<Any>()) as Collection<*>
            return Stream.concat(
                realmRoles.stream(), Stream.concat(confidentialClientRoles.stream(), publicClientRoles.stream())
            ).map { role: Any? -> SimpleGrantedAuthority(role.toString()) }.toList()
        }
    }

    @Bean
    fun authenticationConverter(authoritiesConverter: Jwt2AuthoritiesConverter): Jwt2AuthenticationConverter? {
        return object: Jwt2AuthenticationConverter {
            override fun convert(source: Jwt): AbstractAuthenticationToken {
                return JwtAuthenticationToken(source, authoritiesConverter.convert(source))
            }
        }
    }

    @Bean
    fun apiFilterChain(
        http: HttpSecurity, authenticationConverter: Converter<Jwt, AbstractAuthenticationToken>
    ): SecurityFilterChain {
        http.authorizeRequests { authorize ->
            authorize.antMatchers("/api/*").authenticated()
        }.exceptionHandling {
            it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        }.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and().oauth2Client().and()
            .oauth2ResourceServer().jwt()

        return http.build()
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun uiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeRequests { authorize ->
            authorize.antMatchers("/api/*").authenticated()
                .antMatchers("/", "/login", "/error", "/webjars/**", "/testing").permitAll().anyRequest()
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
            }.oauth2Client().and().oauth2ResourceServer().jwt()

        return http.build()
    }
}