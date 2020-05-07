package com.it.gabrigiunchi.freezer.controller


import com.it.gabrigiunchi.freezer.BaseRestTest
import com.it.gabrigiunchi.freezer.config.security.JwtTokenProvider
import com.it.gabrigiunchi.freezer.constants.ApiUrls
import com.it.gabrigiunchi.freezer.enums.UserType
import com.it.gabrigiunchi.freezer.model.dto.input.ValidateUserDTO
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class LoginControllerTest : BaseRestTest() {

    @Autowired
    private lateinit var tokenProvider: JwtTokenProvider

    @BeforeEach
    fun clearDB() {
        this.userDAO.deleteAll()
    }

    @Test
    fun `Should log in a user with valid username and password`() {
        val password = "aaaa"
        val user = this.createMockUser("dahdkahskd", password)

        val credentials = ValidateUserDTO(user.username, password)

        Assertions.assertThat(this.userDAO.findByUsername(user.username).isPresent).isTrue()
        Assertions.assertThat(this.userDAO.findByUsername(user.username).get().password).isEqualTo(user.password)
        Assertions.assertThat(this.userDAO.findByUsername(user.username).get().active).isTrue()

        this.mockMvc.perform(post(ApiUrls.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(credentials)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.id", Matchers.`is`(user.id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.name", Matchers.`is`(user.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.surname", Matchers.`is`(user.surname)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username", Matchers.`is`(user.username)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email", Matchers.`is`(user.email)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.notNullValue()))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should NOT log in a user with invalid username`() {
        val credentials = ValidateUserDTO("mario", "djksnkan")

        Assertions.assertThat(this.userDAO.findByUsername(credentials.username).isEmpty).isTrue()

        this.mockMvc.perform(post(ApiUrls.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(credentials)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("Invalid username/password supplied")))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should NOT log in a user with invalid password`() {
        val password = "aaaa"
        val user = this.createMockUser("djkasnjdna", password)
        val credentials = ValidateUserDTO(user.username, "mmkldamaldmak")

        this.mockMvc.perform(post(ApiUrls.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(credentials)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("Invalid username/password supplied")))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should say if a token is valid`() {
        val user = this.createMockUser("gabrigiunchi", "aaaa")
        this.userDAO.save(user)
        val token = this.tokenProvider.createToken(user.username, listOf(UserType.ADMINISTRATOR.name))

        this.mockMvc.perform(post("${ApiUrls.LOGIN}/token")
                .contentType(MediaType.TEXT_PLAIN)
                .content(token))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.`is`(true)))
    }

    @Test
    fun `Should say if a token is NOT valid`() {
        val token = "dajdjadjasdnaj"
        this.mockMvc.perform(post("${ApiUrls.LOGIN}/token")
                .contentType(MediaType.TEXT_PLAIN)
                .content(token))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.`is`(false)))
    }

    @Test
    fun `Should not log in a disabled user`() {
        val password = "aaaa"
        val user = this.createMockUser("fasfassd", password)
        user.active = false
        this.userDAO.save(user)

        val credentials = ValidateUserDTO(user.username, password)

        this.mockMvc.perform(post(ApiUrls.LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(credentials)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("Invalid username/password supplied")))
    }

}