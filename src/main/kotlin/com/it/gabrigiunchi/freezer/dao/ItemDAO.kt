package com.it.gabrigiunchi.freezer.dao

import com.it.gabrigiunchi.freezer.model.AppUser
import com.it.gabrigiunchi.freezer.model.Item
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.OffsetDateTime
import java.util.*

interface ItemDAO : PagingAndSortingRepository<Item, Int> {
    fun findByIdAndUser(id: Int, user: AppUser): Optional<Item>
    fun findByUser(user: AppUser): Collection<Item>
    fun findByUserAndExpirationDateAfter(user: AppUser, date: OffsetDateTime): Collection<Item>
}