package com.it.gabrigiunchi.freezer.config.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

class SecurityFilter : GenericFilterBean() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest?, res: ServletResponse?,
                          chain: FilterChain) {
        val auth = SecurityContextHolder.getContext().authentication
        SecurityContextHolder.getContext().authentication = if (isActive(auth)) auth else null
        chain.doFilter(req, res)
    }

    private fun isActive(authentication: Authentication?): Boolean {
        return authentication != null && (authentication.principal as UserDetails).isEnabled
    }
}