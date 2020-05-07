package com.it.gabrigiunchi.freezer.controller

import com.it.gabrigiunchi.freezer.dao.UserDAO
import com.it.gabrigiunchi.freezer.enums.UserType
import com.it.gabrigiunchi.freezer.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

open class BaseController(private val userDAO: UserDAO) {

    protected fun getLoggedUser(): User {
        return this.userDAO.findByUsername((SecurityContextHolder.getContext().authentication.principal as UserDetails).username).get()
    }


    protected fun isAdmin(): Boolean {
        return this.getLoggedUser().type == UserType.ADMINISTRATOR
    }
}