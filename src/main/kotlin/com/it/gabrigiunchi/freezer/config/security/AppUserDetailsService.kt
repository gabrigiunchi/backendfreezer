package com.it.gabrigiunchi.freezer.config.security

import com.it.gabrigiunchi.freezer.dao.UserDAO
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class AppUserDetailsService(private val users: UserDAO) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        return this.users.findByUsername(username)
                .map { AppUserDetails(it) }
                .orElseThrow { UsernameNotFoundException("Username: $username not found") }
    }
}