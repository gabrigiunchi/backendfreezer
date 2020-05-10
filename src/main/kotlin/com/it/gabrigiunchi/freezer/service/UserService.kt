package com.it.gabrigiunchi.freezer.service

import com.it.gabrigiunchi.freezer.dao.UserDAO
import com.it.gabrigiunchi.freezer.exceptions.BadRequestException
import com.it.gabrigiunchi.freezer.exceptions.ResourceNotFoundException
import com.it.gabrigiunchi.freezer.model.AppUser
import com.it.gabrigiunchi.freezer.model.dto.input.ChangePasswordDTO
import com.it.gabrigiunchi.freezer.model.dto.input.UserDTOInput
import com.it.gabrigiunchi.freezer.model.dto.input.ValidateUserDTO
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userDAO: UserDAO) {

    fun getUser(username: String): AppUser =
            this.userDAO.findByUsername(username).orElseThrow { ResourceNotFoundException("User $username not found") }

    fun getUser(id: Int): AppUser =
            this.userDAO.findById(id).orElseThrow { ResourceNotFoundException(AppUser::class.java, id) }

    fun createUser(dto: UserDTOInput): AppUser {
        val user = AppUser(dto.username, BCryptPasswordEncoder().encode(dto.password), dto.name, dto.surname, dto.email, dto.type)
        user.active = dto.isActive
        return user
    }

    fun modifyUser(dto: UserDTOInput, id: Int): AppUser {
        val savedUser = this.userDAO.findById(id).orElseThrow { ResourceNotFoundException(AppUser::class.java, id) }
        savedUser.active = dto.isActive
        savedUser.email = dto.email
        savedUser.name = dto.name
        savedUser.surname = dto.surname
        savedUser.password = BCryptPasswordEncoder().encode(dto.password)
        savedUser.type = dto.type
        return this.userDAO.save(savedUser)
    }

    fun modifyPasswordOfUser(user: AppUser, dto: ChangePasswordDTO): AppUser {
        if (!this.checkPassword(user, dto.oldPassword)) {
            throw BadRequestException("Old password is incorrect")
        }

        user.password = BCryptPasswordEncoder().encode(dto.newPassword)
        return this.userDAO.save(user)
    }

    fun authenticate(credentials: ValidateUserDTO): AppUser {
        val user = this.userDAO.findByUsername(credentials.username)
        if (
                user.isEmpty ||
                !this.checkPassword(user.get(), credentials.password) ||
                !user.get().active) {

            throw BadCredentialsException("Invalid username/password supplied")
        }

        return user.get()
    }

    fun checkPassword(user: AppUser, password: String): Boolean = BCryptPasswordEncoder().matches(password, user.password)
}