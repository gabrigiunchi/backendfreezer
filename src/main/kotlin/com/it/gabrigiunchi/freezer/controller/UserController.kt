package com.it.gabrigiunchi.freezer.controller

import com.it.gabrigiunchi.freezer.dao.UserDAO
import com.it.gabrigiunchi.freezer.exceptions.ResourceAlreadyExistsException
import com.it.gabrigiunchi.freezer.exceptions.ResourceNotFoundException
import com.it.gabrigiunchi.freezer.model.AppUser
import com.it.gabrigiunchi.freezer.model.dto.input.ChangePasswordDTO
import com.it.gabrigiunchi.freezer.model.dto.input.UserDTOInput
import com.it.gabrigiunchi.freezer.model.dto.output.UserDTOOutput
import com.it.gabrigiunchi.freezer.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userDAO: UserDAO, private val userService: UserService) : BaseController(userDAO) {

    val logger = LoggerFactory.getLogger(UserController::class.java)!!

    @GetMapping("/page/{page}/size/{size}")
    fun getAllUsers(@PathVariable page: Int, @PathVariable size: Int): ResponseEntity<Page<UserDTOOutput>> {
        this.logger.info("GET all users, page=$page size=$size")
        return ResponseEntity.ok(this.userDAO.findAll(PageRequest.of(page, size)).map { e -> UserDTOOutput(e) })
    }

    @GetMapping("/{id}")
    fun getUserByid(@PathVariable id: Int): ResponseEntity<AppUser> {
        this.logger.info("GET user #$id")
        return this.userDAO.findById(id)
                .map { ResponseEntity.ok(it) }
                .orElseThrow { ResourceNotFoundException(AppUser::class.java, id) }
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping
    fun createUser(@Valid @RequestBody user: UserDTOInput): ResponseEntity<UserDTOOutput> {
        this.logger.info("POST a new user")
        this.logger.info(user.toString())

        if (this.userDAO.findByUsername(user.username).isPresent) {
            throw ResourceAlreadyExistsException("user with username ${user.username} already exists")
        }

        return ResponseEntity(UserDTOOutput(this.userDAO.save(this.userService.createUser(user))), HttpStatus.CREATED)
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PutMapping("/{id}")
    fun modifyUser(@Valid @RequestBody user: UserDTOInput, @PathVariable id: Int): ResponseEntity<UserDTOOutput> {
        this.logger.info("PUT user $id")
        return ResponseEntity.ok(UserDTOOutput(this.userService.modifyUser(user, id)))
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Int): ResponseEntity<Void> {
        this.logger.info("DELETE user #$id")
        val user = this.userDAO.findById(id).orElseThrow { ResourceNotFoundException(AppUser::class.java, id) }
        this.userDAO.delete(user)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PatchMapping("/{id}/active/{active}")
    fun enableUser(@PathVariable id: Int, @PathVariable active: Boolean): ResponseEntity<Void> {
        this.logger.info("PATCH to set user #$id active=$active")
        val user = this.userDAO.findById(id).orElseThrow { ResourceNotFoundException(AppUser::class.java, id) }
        user.active = active
        this.userDAO.save(user)
        return ResponseEntity(HttpStatus.OK)
    }

    /*************************************** ME ********************************************************************/

    @GetMapping("/me")
    fun getMyDetails(): ResponseEntity<UserDTOOutput> {
        val loggedUser = this.getLoggedUser()
        this.logger.info("GET logged user (#${loggedUser.id})")
        return ResponseEntity.ok(UserDTOOutput(loggedUser))
    }


    @PatchMapping("/me/password")
    fun changeMyPassword(@Valid @RequestBody dto: ChangePasswordDTO): ResponseEntity<UserDTOOutput> {
        val user = this.getLoggedUser()
        this.logger.info("POST to change password of #${user.id}")
        return ResponseEntity.ok(UserDTOOutput(this.userService.modifyPasswordOfUser(user, dto)))
    }
}