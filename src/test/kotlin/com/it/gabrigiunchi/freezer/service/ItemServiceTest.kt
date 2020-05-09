package com.it.gabrigiunchi.freezer.service

import com.it.gabrigiunchi.freezer.BaseTest
import com.it.gabrigiunchi.freezer.enums.ItemType
import com.it.gabrigiunchi.freezer.exceptions.ResourceNotFoundException
import com.it.gabrigiunchi.freezer.model.dto.input.CreateItemDTO
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.OffsetDateTime

class ItemServiceTest : BaseTest() {

    @Autowired
    private lateinit var itemService: ItemService

    @BeforeEach
    fun clear() {
        this.itemDAO.deleteAll()
    }

    @Test
    fun `Should get all the items`() {
        val user = this.createMockUser("gafasa")
        val items = (1..5).map { this.createMockItem("item$4", user) }
        val result = this.itemService.getItems(0, 2)
        Assertions.assertThat(result.content).isEqualTo(items.take(2))
    }

    @Test
    fun `Should get an item`() {
        val user = this.createMockUser("dasda")
        val item = this.createMockItem("dasda", user)
        val result = this.itemService.getItem(item.id)

        Assertions.assertThat(item).isEqualTo(result)
    }

    @Test
    fun `Should throw an exception if the item does not exist`() {
        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException::class.java) {
            this.itemService.getItem(-1)
        }
    }

    @Test
    fun `Should create an item`() {
        val dto = CreateItemDTO("item", ItemType.OTHER, 1, OffsetDateTime.now(), null, "")
        val user = this.createMockUser("dadsa")

        val result = this.itemService.createItem(dto, user)

        Assertions.assertThat(result.user).isEqualTo(user)
        Assertions.assertThat(result.name).isEqualTo(dto.name)
        Assertions.assertThat(result.type).isEqualTo(dto.type)
        Assertions.assertThat(result.quantity).isEqualTo(dto.quantity)
        Assertions.assertThat(result.storeDate).isEqualTo(dto.storeDate)
        Assertions.assertThat(result.expirationDate).isEqualTo(dto.expirationDate)
        Assertions.assertThat(result.note).isEqualTo(dto.note)
    }

    @Test
    fun `Should update an item`() {
        val user = this.createMockUser("dada")
        val item = this.createMockItem("dada", user)
        val dto = CreateItemDTO("item", ItemType.OTHER, 1, OffsetDateTime.now(), null, "")
        val result = this.itemService.updateItem(dto, item.id, user)

        Assertions.assertThat(result.user).isEqualTo(user)
        Assertions.assertThat(result.name).isEqualTo(dto.name)
        Assertions.assertThat(result.type).isEqualTo(dto.type)
        Assertions.assertThat(result.quantity).isEqualTo(dto.quantity)
        Assertions.assertThat(result.storeDate).isEqualTo(dto.storeDate)
        Assertions.assertThat(result.expirationDate).isEqualTo(dto.expirationDate)
        Assertions.assertThat(result.note).isEqualTo(dto.note)
    }

    @Test
    fun `Should not update an item if it does not exist`() {
        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException::class.java) {
            val user = this.createMockUser("dahsdjah")
            val dto = CreateItemDTO("item", ItemType.OTHER, 1, OffsetDateTime.now(), null, "")
            this.itemService.updateItem(dto, -1, user)
        }

    }

    @Test
    fun `Should delete an item`() {
        val user = this.createMockUser("dadasd")
        val item = this.createMockItem("dads", user)

        this.itemService.deleteItem(item.id)

        Assertions.assertThat(this.itemDAO.findById(item.id).isEmpty).isTrue()
    }

    @Test
    fun `Should not delete an item if it does not exist`() {
        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException::class.java) {
            this.itemService.deleteItem(-1)
        }
    }

    @Test
    fun `Should get the items of a user`() {
        val user1 = this.createMockUser("user1")
        val user2 = this.createMockUser("user2")

        (1..4).map { this.createMockItem("i$it", user1) }
        val items2 = (1..3).map { this.createMockItem("i$it", user2) }

        val result = this.itemService.getItemsOfUser(user2)

        Assertions.assertThat(result).isEqualTo(items2)
    }

    @Test
    fun `Should get the item of a user`() {
        val user1 = this.createMockUser("user1")
        val user2 = this.createMockUser("user2")

        (1..4).map { this.createMockItem("i$it", user1) }
        val items2 = (1..3).map { this.createMockItem("i$it", user2) }

        val result = this.itemService.getItemOfUser(items2[2].id, user2)

        Assertions.assertThat(result).isEqualTo(items2[2])
    }

    @Test
    fun `Should get the not expired items of a user`() {
        val user1 = this.createMockUser("user1")
        val user2 = this.createMockUser("user2")

        (1..4).map { this.createMockItem("i$it", user1) }
        val items2 = (1..3).map { this.createMockItem("i$it", user2) }
        (1..4).map { this.createMockItem("i$it", user2, OffsetDateTime.now().minusDays(1)) }

        val result = this.itemService.getNotExpiredItemsOfUser(user2)

        Assertions.assertThat(result).isEqualTo(items2)
    }

    @Test
    fun `Should not get the item of a user if it does not belong to that user`() {
        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException::class.java) {
            val user1 = this.createMockUser("user1")
            val user2 = this.createMockUser("user2")

            (1..4).map { this.createMockItem("i$it", user1) }
            val items2 = (1..3).map { this.createMockItem("i$it", user2) }

            this.itemService.getItemOfUser(items2[2].id, user1)
        }
    }

    @Test
    fun `Should delete the item of a user`() {
        val user = this.createMockUser("dadasd")
        val item = this.createMockItem("dads", user)

        this.itemService.deleteItem(item.id)

        Assertions.assertThat(this.itemDAO.findById(item.id).isEmpty).isTrue()
    }
}