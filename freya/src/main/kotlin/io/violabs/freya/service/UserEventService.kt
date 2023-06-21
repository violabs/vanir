package io.violabs.freya.service

import io.violabs.freya.domain.AppUser
import io.violabs.freya.message.UserProducer
import io.violabs.freya.service.db.UserDbService
import org.springframework.stereotype.Component

@Component
class UserEventService(private val userProducer: UserProducer, private val userDbService: UserDbService) {

    suspend fun createUser(user: AppUser): AppUser = sendUserAfter {
        userDbService.createUser(user)
    }

    suspend fun updateUser(user: AppUser): AppUser = sendUserAfter {
        userDbService.updateUser(user)
    }

    suspend fun deleteUserById(id: Long): AppUser = sendUserAfter {
        val foundUser: AppUser =
            userDbService
                .getUserById(id)
                ?: throw IllegalArgumentException("User with id $id not found")

        val deleted: Boolean = userDbService.deleteUserById(id)

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