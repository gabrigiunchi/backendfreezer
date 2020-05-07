package com.it.gabrigiunchi.freezer.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/_ah/health")
@RestController
class HealthController {

    @GetMapping
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("Healthy")
    }
}