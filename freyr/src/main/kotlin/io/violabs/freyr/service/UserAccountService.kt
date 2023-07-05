package io.violabs.freyr.service

import io.violabs.freyr.client.FreyaUserClient
import io.violabs.freyr.domain.Account
import io.violabs.freyr.domain.UserAccountAction
import io.violabs.freyr.repository.AccountRepo
import org.springframework.stereotype.Service

@Service
class UserAccountService(
    private val accountRepo: AccountRepo,
    private val accountService: AccountService,
    private val userClient: FreyaUserClient
) {
    suspend fun createAccount(action: UserAccountAction): UserAccountAction {
        action.user = userClient.fetchUser(action) ?: return action

        return accountService.saveAccount(action) { accountId ->
            Account(
                id = accountId,
                userId = action.userId,
                userDetails = action.user
            )
        } ?: throw Exception("Account not saved $action")
    }

    suspend fun updateAccount(action: UserAccountAction): UserAccountAction {
        action.user = userClient.fetchUser(action) ?: return action

        return accountService
            .saveAccount(action) { accountId -> accountRepo.findById(accountId) }
            ?: throw Exception("Account not saved $action")
    }

    suspend fun deleteAccount(action: UserAccountAction): UserAccountAction =
        accountService.deleteAccountByUserId(action)
}