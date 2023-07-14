package io.violabs.freya.service

import io.violabs.core.domain.UserMessage
import io.violabs.freya.domain.AppUser
import io.violabs.freya.message.UserProducer
import io.violabs.freya.service.db.UserDbService
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class UserEventService(private val userProducer: UserProducer, private val userDbService: UserDbService) {

    suspend fun createUser(user: AppUser): AppUser = sendUserAfter(UserMessage.Type.USER_CREATED) {
        userDbService.createUser(user)
    }

    suspend fun updateUser(user: AppUser): AppUser = sendUserAfter(UserMessage.Type.USER_UPDATED) {
        userDbService.updateUser(user)
    }

    suspend fun deleteUserById(id: Long): AppUser = sendUserAfter(UserMessage.Type.USER_DELETED) {
        val foundUser: AppUser =
            userDbService
                .getUserById(id)
                ?: throw IllegalArgumentException("User with id $id not found")

        val deleted: Boolean = userDbService.deleteUserById(id)

        if (!deleted) throw IllegalStateException("User with id $id not deleted")

        foundUser
    }

    private suspend fun sendUserAfter(type: UserMessage.Type, userProvider: suspend () -> AppUser): AppUser {
        val user: AppUser = userProvider()

        logger.info("type: $type, user: $user")

        return userProducer
            .sendUserData(user, type)
            ?.let { user }
            ?: throw IllegalStateException("User not sent")
    }

    companion object : KLogging()
}