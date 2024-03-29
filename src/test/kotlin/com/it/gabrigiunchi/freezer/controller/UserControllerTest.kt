package com.it.gabrigiunchi.freezer.controller

import com.it.gabrigiunchi.freezer.BaseRestTest
import com.it.gabrigiunchi.freezer.constants.ApiUrls
import com.it.gabrigiunchi.freezer.enums.UserType
import com.it.gabrigiunchi.freezer.model.AppUser
import com.it.gabrigiunchi.freezer.model.dto.input.ChangePasswordDTO
import com.it.gabrigiunchi.freezer.model.dto.input.UserDTOInput
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class UserControllerTest : BaseRestTest() {


    @BeforeEach
    fun clearDB() {
        this.userDAO.deleteAll()
    }

    @Test
    fun `Should get all users`() {
        this.userDAO.saveAll(listOf(
                AppUser("gabrigiunchi1", "dsndja", "Gabriele", "Giunchi", "mail@mail.com"),
                AppUser("fragiunchi", "dsndja", "Francesco", "Giunchi", "mail@mail.com"),
                AppUser("fabiogiunchi", "dsndja", "Fabio", "Giunchi", "mail@mail.com")))

        this.mockMvc.perform(get("${ApiUrls.USERS}/page/0/size/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()", Matchers.`is`(3)))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should get a user given its id`() {
        val user = this.userDAO.save(AppUser("giggi", "ddnsakjn", "Gianni", "Riccio", "mail@mail.com"))
        this.mockMvc.perform(get("${ApiUrls.USERS}/${user.id}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.`is`(user.username)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.`is`(user.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", Matchers.`is`(user.surname)))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not get a user if it does not exist`() {
        this.mockMvc.perform(get("${ApiUrls.USERS}/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("AppUser #-1 not found")))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should create a user`() {
        val user = UserDTOInput("giggi", "ddnsakjn", "", "", "mail@mail.com", UserType.ADMINISTRATOR)
        mockMvc.perform(post(ApiUrls.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(user)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.`is`(user.username)))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not create a user if its username already exists`() {
        val user = this.createMockUser("gab", "aaaa")
        mockMvc.perform(post(ApiUrls.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(UserDTOInput(user))))
                .andExpect(MockMvcResultMatchers.status().isConflict)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should modify a user`() {
        val existing = this.createMockUser("gab", "aaaa")
        val oldPassword = existing.password
        val modified = UserDTOInput("username", "password", "User", "Surname", "newmail", isActive = false, type = UserType.ADMINISTRATOR)

        mockMvc.perform(put("${ApiUrls.USERS}/${existing.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(modified)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.`is`(modified.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", Matchers.`is`(modified.surname)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.`is`(modified.email)))
                .andDo(MockMvcResultHandlers.print())

        val result = this.userDAO.findById(existing.id).get()
        Assertions.assertThat(result.active).isFalse()
        Assertions.assertThat(result.password).isNotEqualTo(oldPassword)
    }

    @Test
    fun `Should not modify a user if it does not exist`() {
        val modified = UserDTOInput("username", "password", "User", "Surname",
                "newmail", isActive = false, type = UserType.ADMINISTRATOR)
        mockMvc.perform(put("${ApiUrls.USERS}/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(modified)))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("AppUser #-1 not found")))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should delete a user`() {
        var user = this.createMockUser("fadsd", "dasda")
        mockMvc.perform(delete("${ApiUrls.USERS}/${user.id}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should not delete a user if it does not exist`() {
        mockMvc.perform(delete("${ApiUrls.USERS}/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("AppUser #-1 not found")))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should deactivate a user`() {
        this.userDAO.deleteAll()
        val user = this.createMockUser("gabrigiunchi")
        Assertions.assertThat(user.active).isTrue()

        mockMvc.perform(get("${ApiUrls.USERS}/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcResultHandlers.print())

        mockMvc.perform(patch("${ApiUrls.USERS}/${user.id}/active/false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcResultHandlers.print())

        Assertions.assertThat(this.userDAO.findByUsername(user.username).get().active).isFalse()
    }

    @Test
    fun `Should not deactivate a user if it does not exist`() {
        mockMvc.perform(patch("${ApiUrls.USERS}/-1/active/false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("AppUser #-1 not found")))
                .andDo(MockMvcResultHandlers.print())
    }

    /************************************** ME ************************************************************************/

    @Test
    fun `Should get the logged user`() {
        this.userDAO.deleteAll()
        val user = this.createMockUser("gabrigiunchi")
        mockMvc.perform(get("${ApiUrls.USERS}/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.`is`(user.id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.`is`(user.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname", Matchers.`is`(user.surname)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.`is`(user.username)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.`is`(user.email)))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `Should change my password`() {
        this.userDAO.deleteAll()
        val user = this.createMockUser("gabrigiunchi", "aaaa")
        val oldPassword = user.password
        val dto = ChangePasswordDTO("aaaa", "bbbb")
        mockMvc.perform(patch("${ApiUrls.USERS}/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcResultHandlers.print())

        val result = this.userDAO.findById(user.id).get()
        Assertions.assertThat(oldPassword).isNotEqualTo(result.password)
    }

    @Test
    fun `Should not change my password if the old password is incorrect`() {
        this.userDAO.deleteAll()
        val user = this.createMockUser("gabrigiunchi")
        val oldPassword = user.password
        val dto = ChangePasswordDTO("acvd", "bbbb")
        mockMvc.perform(patch("${ApiUrls.USERS}/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("Old password is incorrect")))
                .andDo(MockMvcResultHandlers.print())

        val result = this.userDAO.findById(user.id).get()
        Assertions.assertThat(oldPassword).isEqualTo(result.password)
    }
}