package com.it.gabrigiunchi.freezer.controller

import com.it.gabrigiunchi.freezer.BaseRestTest
import com.it.gabrigiunchi.freezer.constants.ApiUrls
import com.it.gabrigiunchi.freezer.enums.UserType
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class AliveControllerTest : BaseRestTest() {


    @Value("\${application.version}")
    private var version: String = ""

    @BeforeEach
    fun clear() {
        this.userDAO.deleteAll()
        this.userDAO.count()
        this.createMockUser("gabrigiunchi")
    }

    @Test
    fun `Should return OK and be accessible to anyone`() {
        this.mockMvc.perform(MockMvcRequestBuilders.get(ApiUrls.ALIVE))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.`is`("Everything's fine")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.version", Matchers.`is`(this.version)))
    }

    @Test
    @WithMockUser(username = "gabrigiunchi", password = "aaaa", authorities = ["ADMINISTRATOR"])
    fun `Should get the logged user`() {
        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ALIVE}/me"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.`is`("gabrigiunchi")))
    }

    @Test
    @WithMockUser(username = "gabrigiunchi", password = "aaaa", authorities = ["ADMINISTRATOR"])
    fun `Should say if I am an admin`() {
        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ALIVE}/me/admin"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.isAdmin", Matchers.`is`("true")))
    }

    @Test
    @WithMockUser(username = "baseuser", password = "bbbb", authorities = ["USER"])
    fun `Should say if I am NOT an admin`() {
        this.createMockUser("baseuser", "bbbb", UserType.USER)
        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ALIVE}/me/admin"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.isAdmin", Matchers.`is`("false")))
    }

    @Test
    @WithMockUser(username = "gabrigiunchi", password = "aaaa", authorities = ["ADMINISTRATOR"])
    fun `Should allow administrators to access secured endpoints`() {
        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ALIVE}/secret"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @WithMockUser(username = "baseuser", password = "bbbb", authorities = ["USER"])
    fun `Should forbid regular users to access secured endpoints`() {
        this.createMockUser("baseuser", "bbbb", UserType.USER)
        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ALIVE}/secret"))
                .andExpect(MockMvcResultMatchers.status().isForbidden)
    }
}