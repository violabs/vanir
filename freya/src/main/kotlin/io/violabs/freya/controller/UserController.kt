package io.violabs.freya.controller

import io.violabs.freya.domain.AppUser
import io.violabs.freya.service.db.UserDbService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/users")
class UserController(private val userDbService: UserDbService) {

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    suspend fun createUser(@RequestBody user: AppUser): AppUser = userDbService.createUser(user)

    @PutMapping
    suspend fun updateUser(@RequestBody user: AppUser): AppUser = userDbService.updateUser(user)

    @GetMapping("/{id}")
    suspend fun getUserById(@PathVariable id: Long): AppUser? = userDbService.getUserById(id)

    @GetMapping
    fun getAllUsers(): Flow<AppUser> = userDbService.getAllUsers()

    @DeleteMapping("/{id}")
    suspend fun deleteUserById(@PathVariable id: Long): Boolean = userDbService.deleteUserById(id)
}