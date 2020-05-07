package com.it.gabrigiunchi.freezer.controller

import com.it.gabrigiunchi.freezer.dao.UserDAO
import com.it.gabrigiunchi.freezer.model.Item
import com.it.gabrigiunchi.freezer.model.dto.input.CreateItemDTO
import com.it.gabrigiunchi.freezer.service.ItemService
import com.it.gabrigiunchi.freezer.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/items")
class ItemController(
        userDAO: UserDAO,
        private val userService: UserService,
        private val itemService: ItemService) : BaseController(userDAO) {

    private val logger = LoggerFactory.getLogger(ItemController::class.java)

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/page/{page}/size/{size}")
    fun getItems(@PathVariable page: Int, @PathVariable size: Int): ResponseEntity<Page<Item>> {
        this.logger.info("Get items, page=$page, size=$size")
        return ResponseEntity.ok(this.itemService.getItems(page, size))
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/{id}")
    fun getItem(@PathVariable id: Int): ResponseEntity<Item> {
        this.logger.info("Get item #$id")
        return ResponseEntity.ok(this.itemService.getItem(id))
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/user/{id}")
    fun createItem(@RequestBody @Valid dto: CreateItemDTO, @PathVariable id: Int): ResponseEntity<Item> {
        this.logger.info("Create item for user #$id")
        return ResponseEntity(this.itemService.createItem(dto, this.userService.getUser(id)), HttpStatus.CREATED)
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PutMapping("/{itemId}/user/{userId}")
    fun modifyItem(@RequestBody @Valid dto: CreateItemDTO, @PathVariable itemId: Int, @PathVariable userId: Int): ResponseEntity<Item> {
        this.logger.info("Modify item #$itemId of user #$userId")
        return ResponseEntity.ok(this.itemService.updateItem(dto, itemId, this.userService.getUser(userId)))
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    fun deleteItem(@PathVariable id: Int): ResponseEntity<Void> {
        this.logger.info("Delete item #$id")
        this.itemService.deleteItem(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/me")
    fun getMyItems(): ResponseEntity<Collection<Item>> {
        val user = this.getLoggedUser()
        this.logger.info("Get items of logged user #${user.id}")
        return ResponseEntity.ok(this.itemService.getItemsOfUser(user))
    }

    @GetMapping("/me/{id}")
    fun getMyItem(@PathVariable id: Int): ResponseEntity<Item> {
        val user = this.getLoggedUser()
        this.logger.info("Get item #$id of logged user #${user.id}")
        return ResponseEntity.ok(this.itemService.getItemOfUser(id, user))
    }

    @PostMapping("/me")
    fun createMyItem(@RequestBody @Valid dto: CreateItemDTO): ResponseEntity<Item> {
        val user = this.getLoggedUser()
        this.logger.info("Create item for logged user #${user.id}")
        return ResponseEntity(this.itemService.createItem(dto, user), HttpStatus.CREATED)
    }

    @PutMapping("/me/{id}")
    fun updateMyItem(@RequestBody @Valid dto: CreateItemDTO, @PathVariable id: Int): ResponseEntity<Item> {
        val user = this.getLoggedUser()
        this.logger.info("Update item #$id of logged user #${user.id}")
        return ResponseEntity.ok(this.itemService.updateItem(dto, id, user))
    }

    @DeleteMapping("/me/{id}")
    fun deleteMyItem(@PathVariable id: Int): ResponseEntity<Void> {
        val user = this.getLoggedUser()
        this.logger.info("Delete item #$id of logged user #${user.id}")
        this.itemService.deleteItemOfUser(id, user)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}