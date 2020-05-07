package com.it.gabrigiunchi.freezer.controller

import com.it.gabrigiunchi.freezer.BaseRestTest
import com.it.gabrigiunchi.freezer.constants.ApiUrls
import com.it.gabrigiunchi.freezer.enums.ItemType
import com.it.gabrigiunchi.freezer.model.dto.input.CreateItemDTO
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.OffsetDateTime

class ItemControllerTest : BaseRestTest() {

    @BeforeEach
    fun clear() {
        this.itemDAO.deleteAll()
        this.userDAO.deleteAll()
        this.userDAO.count()
    }

    @Test
    fun `Should get all items`() {
        val user = this.createMockUser("dadsad")
        val items = (1..4).map { this.createMockItem("i$it", user) }

        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ITEMS}/page/0/size/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id", Matchers.`is`(items[0].id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id", Matchers.`is`(items[1].id)))
    }

    @Test
    fun `Should get an item`() {
        val user = this.createMockUser("dadsa")
        val item = this.createMockItem("dadsa", user)

        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ITEMS}/${item.id}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.`is`(item.id)))
    }

    @Test
    fun `Should not get an item if it does not exist`() {
        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ITEMS}/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("Item #-1 not found")))
    }

    @Test
    fun `Should create an item`() {
        val user = this.createMockUser("dadsa")
        val dto = CreateItemDTO(
                "dadas",
                ItemType.OTHER,
                2,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1),
                "dasda"
        )

        this.mockMvc.perform(MockMvcRequestBuilders.post("${ApiUrls.ITEMS}/user/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.`is`(dto.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.`is`(dto.type.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.note", Matchers.`is`(dto.note)))
    }

    @Test
    fun `Should update an item`() {
        val user = this.createMockUser("dadsa")
        val item = this.createMockItem("dadasd", user)
        val dto = CreateItemDTO(
                "dadas",
                ItemType.OTHER,
                2,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1),
                "dasda"
        )

        this.mockMvc.perform(MockMvcRequestBuilders.put("${ApiUrls.ITEMS}/${item.id}/user/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.`is`(dto.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.`is`(dto.type.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.note", Matchers.`is`(dto.note)))
    }

    @Test
    fun `Should not update an item if it does not exist`() {
        val user = this.createMockUser("dasa")
        val dto = CreateItemDTO(
                "dadas",
                ItemType.OTHER,
                2,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1),
                "dasda"
        )
        this.mockMvc.perform(MockMvcRequestBuilders.put("${ApiUrls.ITEMS}/-1/user/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(dto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("Item #-1 not found")))
    }

    @Test
    fun `Should not update an item if the user does not exist`() {
        val user = this.createMockUser("dasa")
        val item = this.createMockItem("dadsa", user)
        val dto = CreateItemDTO(
                "dadas",
                ItemType.OTHER,
                2,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1),
                "dasda"
        )
        this.mockMvc.perform(MockMvcRequestBuilders.put("${ApiUrls.ITEMS}/${item.id}/user/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(dto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("User #-1 not found")))
    }

    @Test
    fun `Should delete an item`() {
        val user = this.createMockUser("dadsa")
        val item = this.createMockItem("dadas", user)

        this.mockMvc.perform(MockMvcRequestBuilders.delete("${ApiUrls.ITEMS}/${item.id}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun `Should not delete an item if it does not exist`() {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("${ApiUrls.ITEMS}/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("Item #-1 not found")))
    }

    @Test
    fun `Should get the items of the logged user`() {
        val user1 = this.createMockUser("user1")
        val user2 = this.createMockUser("gabrigiunchi")

        (1..4).map { this.createMockItem("i$it", user1) }
        val items2 = (1..3).map { this.createMockItem("i$it", user2) }

        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ITEMS}/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.`is`(items2[0].id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.`is`(items2[1].id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id", Matchers.`is`(items2[2].id)))
    }

    @Test
    fun `Should get an item of the logged user`() {
        val user = this.createMockUser("gabrigiunchi")
        val item = this.createMockItem("dasdas", user)

        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ITEMS}/me/${item.id}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.`is`(item.id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.`is`(item.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.`is`(item.type.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.note", Matchers.`is`(item.note)))
    }

    @Test
    fun `Should return 404 when requesting an item of the logged user which does not exist`() {
        this.createMockUser("gabrigiunchi")
        this.mockMvc.perform(MockMvcRequestBuilders.get("${ApiUrls.ITEMS}/me/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("Item #-1 not found")))
    }

    @Test
    fun `Should create an item for the logged user`() {
        this.createMockUser("gabrigiunchi")
        val dto = CreateItemDTO(
                "dasdas",
                ItemType.OTHER,
                1,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1),
                ""
        )

        this.mockMvc.perform(MockMvcRequestBuilders.post("${ApiUrls.ITEMS}/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.`is`(dto.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.`is`(dto.type.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.note", Matchers.`is`(dto.note)))
    }

    @Test
    fun `Should update an item of the logged user`() {
        val user = this.createMockUser("gabrigiunchi")
        val item = this.createMockItem("dasdas", user)

        val dto = CreateItemDTO(
                "dasdas",
                ItemType.OTHER,
                1,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1),
                ""
        )

        this.mockMvc.perform(MockMvcRequestBuilders.put("${ApiUrls.ITEMS}/me/${item.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.`is`(item.id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.`is`(item.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.`is`(item.type.name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.note", Matchers.`is`(item.note)))
    }

    @Test
    fun `Should return 404 when updating an item of the logged user which does not exist`() {
        this.createMockUser("gabrigiunchi")
        val dto = CreateItemDTO(
                "dasdas",
                ItemType.OTHER,
                1,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1),
                ""
        )

        this.mockMvc.perform(MockMvcRequestBuilders.put("${ApiUrls.ITEMS}/me/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(dto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("Item #-1 not found")))
    }

    @Test
    fun `Should delete an item of the logged user`() {
        val user = this.createMockUser("gabrigiunchi")
        val item = this.createMockItem("dasdas", user)

        this.mockMvc.perform(MockMvcRequestBuilders.delete("${ApiUrls.ITEMS}/me/${item.id}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun `Should return 404 when deleting an item of the logged user which does not exist`() {
        this.createMockUser("gabrigiunchi")
        this.mockMvc.perform(MockMvcRequestBuilders.delete("${ApiUrls.ITEMS}/me/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.`is`("Item #-1 not found")))
    }
}