package com.it.gabrigiunchi.freezer.service

import com.it.gabrigiunchi.freezer.dao.ItemDAO
import com.it.gabrigiunchi.freezer.exceptions.ResourceNotFoundException
import com.it.gabrigiunchi.freezer.model.AppUser
import com.it.gabrigiunchi.freezer.model.Item
import com.it.gabrigiunchi.freezer.model.dto.input.CreateItemDTO
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class ItemService(private val itemDAO: ItemDAO, private val userService: UserService) {

    private val logger = LoggerFactory.getLogger(ItemService::class.java)

    fun getItems(page: Int, size: Int): Page<Item> {
        this.logger.info("Get items,  page=$page, size=$size")
        return this.itemDAO.findAll(PageRequest.of(page, size))
    }

    fun getItemsOfUser(user: AppUser): Collection<Item> {
        this.logger.info("Get items of user ${user.id}")
        return this.itemDAO.findByUser(user)
    }

    fun getItem(id: Int): Item {
        this.logger.info("Get item #$id")
        return this.itemDAO.findById(id).orElseThrow { ResourceNotFoundException(Item::class.java, id) }
    }

    fun getNotExpiredItemsOfUser(user: AppUser): Collection<Item> {
        this.logger.info("Get not expired items of user #${user.id}")
        return this.itemDAO.findByUserAndExpirationDateAfter(user, OffsetDateTime.now())
    }

    fun getItemOfUser(id: Int, user: AppUser): Item {
        this.logger.info("Get item #$id of user #${user.id}")
        return this.itemDAO.findByIdAndUser(id, user).orElseThrow { ResourceNotFoundException(Item::class.java, id) }
    }

    fun createItem(dto: CreateItemDTO, user: AppUser): Item {
        this.logger.info("Create new item for user #${user.id}")
        return this.itemDAO.save(Item(
                dto.name,
                dto.type,
                dto.quantity,
                dto.storeDate,
                dto.expirationDate,
                dto.note,
                user
        ))
    }

    fun updateItem(dto: CreateItemDTO, id: Int, user: AppUser): Item {
        this.logger.info("Update item #$id of user ${user.id}")
        this.getItemOfUser(id, user)
        return this.itemDAO.save(Item(id, dto, user))
    }

    fun deleteItem(id: Int) {
        this.logger.info("Delete item #$id")
        this.itemDAO.delete(this.getItem(id))
    }

    fun deleteItemOfUser(id: Int, user: AppUser) {
        this.logger.info("Delete item #$id of user ${user.id}")
        this.itemDAO.delete(this.getItemOfUser(id, user))
    }
}