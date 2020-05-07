package com.it.gabrigiunchi.freezer.controller


import com.it.gabrigiunchi.freezer.config.security.JwtTokenProvider
import com.it.gabrigiunchi.freezer.exceptions.AccessDeniedException
import com.it.gabrigiunchi.freezer.model.dto.input.ValidateUserDTO
import com.it.gabrigiunchi.freezer.model.dto.output.Token
import com.it.gabrigiunchi.freezer.model.dto.output.UserDTOOutput
import com.it.gabrigiunchi.freezer.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
@RequestMapping("/api/v1/login")
class LoginController(private val userService: UserService, val jwtTokenProvider: JwtTokenProvider) {

    private val logger = LoggerFactory.getLogger(LoginController::class.java)


    @PostMapping
    fun login(@RequestBody @Valid credentials: ValidateUserDTO): ResponseEntity<Token> {
        this.logger.info("Login request: {username:" + credentials.username + ", password:" + credentials.password + "}")

        val user = this.userService.authenticate(credentials)
        val token = this.jwtTokenProvider.createToken(user.username, listOf(user.type.name))

        return ResponseEntity.ok(Token(UserDTOOutput(user), token))
    }

    @PostMapping("/token")
    fun loginWithToken(@RequestBody token: String): ResponseEntity<Boolean> {
        this.logger.info("Login with token")
        val valid = try {
            this.jwtTokenProvider.validateToken(token)
        } catch (e: AccessDeniedException) {
            false
        }

        return ResponseEntity.ok(valid)
    }
}