package io.violabs.freya.controller

import io.violabs.freya.domain.AppUser
import io.violabs.freya.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    suspend fun createUser(@RequestBody user: AppUser): AppUser = userService.createUser(user)
}