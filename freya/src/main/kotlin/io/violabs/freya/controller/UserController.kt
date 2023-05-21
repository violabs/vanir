package io.violabs.freya.controller

import io.violabs.freya.domain.AppUser
import io.violabs.freya.service.UserService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    suspend fun createUser(@RequestBody user: AppUser): AppUser = userService.createUser(user)

    @PutMapping
    suspend fun updateUser(@RequestBody user: AppUser): AppUser = userService.updateUser(user)

    @GetMapping("/{id}")
    suspend fun getUserById(@PathVariable id: Long): AppUser? = userService.getUserById(id)

    @GetMapping
    fun getAllUsers(): Flow<AppUser> = userService.getAllUsers()

    @DeleteMapping("/{id}")
    suspend fun deleteUserById(@PathVariable id: Long): Boolean = userService.deleteUserById(id)
}