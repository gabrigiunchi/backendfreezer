package com.it.gabrigiunchi.freezer

import com.it.gabrigiunchi.freezer.config.AppInitializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FreezerApplication : CommandLineRunner {
    @Autowired
    lateinit var appInitializer: AppInitializer

    override fun run(vararg args: String?) {
        this.appInitializer.initDB()
    }
}

fun main(args: Array<String>) {
    runApplication<FreezerApplication>(*args)
}
