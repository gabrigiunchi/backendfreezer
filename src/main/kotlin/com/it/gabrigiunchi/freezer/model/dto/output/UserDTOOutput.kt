package com.it.gabrigiunchi.freezer.model.dto.output

import com.it.gabrigiunchi.freezer.model.User


data class UserDTOOutput(
        val id: Int,
        val username: String,
        val name: String,
        val surname: String,
        val email: String) {

    constructor(user: User) : this(user.id, user.username, user.name, user.surname, user.email)
}