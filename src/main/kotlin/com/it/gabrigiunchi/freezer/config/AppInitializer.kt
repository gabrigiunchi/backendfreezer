package com.it.gabrigiunchi.freezer.config

import com.it.gabrigiunchi.freezer.dao.UserDAO
import com.it.gabrigiunchi.freezer.enums.UserType
import com.it.gabrigiunchi.freezer.model.AppUser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AppInitializer(private val userDAO: UserDAO) {


    @Value("\${application.initDB}")
    private var initDB = false

    private val logger = LoggerFactory.getLogger(AppInitializer::class.java)

    fun initDB() {
        if (this.initDB) {
            this.logger.info("Creating entities")
            this.initUsers()
            this.logger.info("DB initialized")
        }
    }

    private fun initUsers() {
        this.logger.info("Creating users")
        this.userDAO.saveAll(listOf(
                AppUser("gabrigiunchi", BCryptPasswordEncoder().encode("aaaa"), "Gabriele", "Giunchi", "", UserType.ADMINISTRATOR),
                AppUser("antonellatondi", BCryptPasswordEncoder().encode("pinguino1317"), "Antonella", "Tondi", "", UserType.USER)
        ))
        this.logger.info("Users created")
    }
}