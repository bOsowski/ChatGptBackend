package net.bosowski.chattergpt.controllers.api

import net.bosowski.chattergpt.data.models.authentication.OauthUser
import net.bosowski.chattergpt.data.repositories.authentication.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class User {

    @Autowired
    lateinit var userRepository: UserRepository

    @GetMapping("/info")
    fun test(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<OauthUser> {
        val email = jwt.claims["email"] as String
        val user = userRepository.findByUsername(email)
        return ResponseEntity(user, HttpStatus.OK)
    }
}