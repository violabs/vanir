package io.violabs.freyr.service

import io.violabs.core.domain.UserMessage
import io.violabs.freyr.client.FreyaUserClient
import io.violabs.freyr.domain.Account
import io.violabs.freyr.repository.AccountRepo
import org.springframework.stereotype.Service

@Service
class UserAccountService(
    private val accountRepo: AccountRepo,
    private val accountService: AccountService,
    private val userClient: FreyaUserClient
) {
    suspend fun createAccount(message: UserMessage): Account? {
        val user = userClient.fetchUser(message) ?: return null

        return accountService.saveAccount(user) { accountId, userId ->
            Account(
                id = accountId,
                userId = userId,
                userDetails = user
            )
        }
    }

    suspend fun updateAccount(userMessage: UserMessage): Account? {
        val user = userClient.fetchUser(userMessage) ?: return null

        return accountService.saveAccount(user) { accountId, _ -> accountRepo.findById(accountId) }
    }

    suspend fun deleteAccount(userMessage: UserMessage): Boolean {
        val userId = userMessage.userId

        return accountService.deleteAccountByUserId(userId)
    }

    suspend fun deactivateAccount(userMessage: UserMessage): Boolean {
        // todo

        return false
    }
}