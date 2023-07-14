package io.violabs.freya.controller

import io.violabs.freya.domain.AppUser
import io.violabs.freya.service.UserEventService
import io.violabs.freya.service.db.UserDbService
import kotlinx.coroutines.flow.Flow
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/users")
class UserController(
    private val userDbService: UserDbService,
    private val userEventService: UserEventService
) {

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    suspend fun createUser(@RequestBody user: AppUser): AppUser = log("createUser", user) {
        userEventService.createUser(user)
    }

    @PutMapping
    suspend fun updateUser(@RequestBody user: AppUser): AppUser = log("updateUser", user) {
        userEventService.updateUser(user)
    }

    @GetMapping("/{id}")
    suspend fun getUserById(@PathVariable id: Long): AppUser? = log("getUserById", id) {
        userDbService.getUserById(id)
    }

    @GetMapping
    suspend fun getAllUsers(): Flow<AppUser> = log("getAllUsers") {
        userDbService.getAllUsers()
    }

    @DeleteMapping("/{id}")
    suspend fun deleteUserById(@PathVariable id: Long): AppUser = log("deleteUserById", id) {
        userEventService.deleteUserById(id)
    }

    private suspend fun <T> log(method: String, parameter: Any? = null, contentFn: suspend () -> T): T {
        val parameterString = parameter?.toString() ?: ""

        logger.info("$method($parameterString) START")
        return contentFn.invoke().also {
            logger.info("$method($parameterString) ENDED")
        }
    }

    companion object : KLogging()
}