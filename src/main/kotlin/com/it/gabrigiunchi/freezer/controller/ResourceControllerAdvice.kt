package com.it.gabrigiunchi.freezer.controller

import com.it.gabrigiunchi.freezer.exceptions.AccessDeniedException
import com.it.gabrigiunchi.freezer.exceptions.BadRequestException
import com.it.gabrigiunchi.freezer.exceptions.ResourceAlreadyExistsException
import com.it.gabrigiunchi.freezer.exceptions.ResourceNotFoundException
import org.springframework.hateoas.mediatype.vnderrors.VndErrors
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*

@ControllerAdvice
@RequestMapping(produces = ["application/vnd.error"])
class ResourceControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun resourceNotFoundExceptionHandler(ex: ResourceNotFoundException): VndErrors? {
        return VndErrors("Not Found Error", ex.message!!)
    }

    @ResponseBody
    @ExceptionHandler(ResourceAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun resourceAlreadyExistsExceptionHandler(ex: ResourceAlreadyExistsException): VndErrors? {
        return VndErrors("Already Exist Error", ex.message!!)
    }

    @ResponseBody
    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun badRequestExceptionHandler(ex: BadRequestException): VndErrors? {
        return VndErrors("Bad Request", ex.message!!)
    }

    @ResponseBody
    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun accessDeniedExceptionHandler(ex: AccessDeniedException): VndErrors? {
        return VndErrors("Forbidden", ex.message!!)
    }

    @ResponseBody
    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun badCredentialsExceptionHandler(ex: BadCredentialsException): VndErrors? {
        return VndErrors("Bad credentials", ex.message!!)
    }
}