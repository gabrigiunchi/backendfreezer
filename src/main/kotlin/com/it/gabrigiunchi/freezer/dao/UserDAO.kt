package com.it.gabrigiunchi.freezer.dao

import com.it.gabrigiunchi.freezer.model.AppUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface UserDAO : PagingAndSortingRepository<AppUser, Int> {
    override fun findAll(pageable: Pageable): Page<AppUser>
    fun findByUsername(username: String): Optional<AppUser>
    fun findByUsernameAndActive(username: String, active: Boolean): Optional<AppUser>
}