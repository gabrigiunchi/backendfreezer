package com.it.gabrigiunchi.freezer.model.dto.input

import com.it.gabrigiunchi.freezer.enums.UserType
import com.it.gabrigiunchi.freezer.model.AppUser

class UserDTOInput(
        val username: String,
        val password: String,
        val name: String,
        val surname: String,
        val email: String,
        val type: UserType,
        val isActive: Boolean
) {

    constructor(username: String, password: String, name: String, surname: String, email: String, type: UserType) :
            this(username, password, name, surname, email, type, true)

    constructor(user: AppUser) :
            this(user.username, user.password, user.name, user.surname, user.email,
                    user.type, user.active)
}