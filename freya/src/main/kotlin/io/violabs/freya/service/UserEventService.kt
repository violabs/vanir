package io.violabs.freya.service

import io.violabs.freya.domain.AppUser
import io.violabs.freya.message.UserProducer
import org.springframework.stereotype.Component

@Component
class UserEventService(private val userProducer: UserProducer, private val userService: UserService) {

    suspend fun createUser(user: AppUser): AppUser = sendUserAfter {
        userService.createUser(user)
    }

    suspend fun updateUser(user: AppUser): AppUser = sendUserAfter {
        userService.updateUser(user)
    }

    suspend fun deleteUserById(id: Long): AppUser = sendUserAfter {
        val foundUser: AppUser =
            userService
                .getUserById(id)
                ?: throw IllegalArgumentException("User with id $id not found")

        val deleted: Boolean = userService.deleteUserById(id)

        if (!deleted) throw IllegalStateException("User with id $id not deleted")

        foundUser
    }

    private suspend fun sendUserAfter(userProvider: suspend () -> AppUser): AppUser {
        val user: AppUser = userProvider()

        return userProducer
            .sendUserData(user)
            ?.let { user }
            ?: throw IllegalStateException("User not sent")
    }
}