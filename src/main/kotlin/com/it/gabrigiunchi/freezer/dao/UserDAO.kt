package com.it.gabrigiunchi.freezer.dao

import com.it.gabrigiunchi.freezer.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface UserDAO : PagingAndSortingRepository<User, Int> {
    override fun findAll(pageable: Pageable): Page<User>
    fun findByUsername(username: String): Optional<User>
    fun findByUsernameAndActive(username: String, active: Boolean): Optional<User>
}