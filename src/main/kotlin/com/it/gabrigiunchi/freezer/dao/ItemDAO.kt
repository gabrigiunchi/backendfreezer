package com.it.gabrigiunchi.freezer.dao

import com.it.gabrigiunchi.freezer.model.Item
import com.it.gabrigiunchi.freezer.model.User
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.OffsetDateTime
import java.util.*

interface ItemDAO : PagingAndSortingRepository<Item, Int> {
    fun findByIdAndUser(id: Int, user: User): Optional<Item>
    fun findByUser(user: User): Collection<Item>
    fun findByUserAndExpirationDateAfter(user: User, date: OffsetDateTime): Collection<Item>
}