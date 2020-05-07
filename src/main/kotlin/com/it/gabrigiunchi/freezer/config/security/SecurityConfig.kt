package com.it.gabrigiunchi.freezer.config.security

import com.it.gabrigiunchi.freezer.constants.ApiUrls
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class SecurityConfig(
        private val jwtTokenProvider: JwtTokenProvider,
        private val userDetailsService: UserDetailsService
) : WebSecurityConfigurerAdapter() {

    private val SECURITY_WHITELIST = arrayOf(
            "/index",
            "/swagger-ui.html",
            "/swagger-ui.html/**",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/webjars/**",
            "_ah/**",
            ApiUrls.LOGIN,
            ApiUrls.LOGIN + "/token",
            ApiUrls.ALIVE)

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .httpBasic().disable()
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(*SECURITY_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(JwtConfigurer(jwtTokenProvider))
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authProvider())
    }

    @Bean
    fun authProvider(): DaoAuthenticationProvider? {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(BCryptPasswordEncoder())
        return authProvider
    }
}