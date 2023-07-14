package io.violabs.freyr.service

import io.violabs.freyr.config.UuidGenerator
import io.violabs.freyr.domain.Account
import io.violabs.freyr.domain.UserAccountAction
import io.violabs.freyr.repository.AccountRepo
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepo: AccountRepo,
    private val uuidGenerator: UuidGenerator
) {

    suspend fun saveAccount(
        action: UserAccountAction,
        accountProvider: suspend (accountId: String) -> Account?
    ): UserAccountAction? {
        val userId = action.userMessage.userId

        action.accountId = generateAccountIdByUserId(userId)

        action.account = accountProvider(action.accountIdNotNull()) ?: return null

        val saved: Boolean = accountRepo.save(action.account!!)

        return action.also { it.saved = saved }
    }

    fun listAccounts(): Flow<Account> = accountRepo.findAll()

    suspend fun deleteAccountByUserId(action: UserAccountAction): UserAccountAction {
        action.accountId = generateAccountIdByUserId(action.userMessage.userId)

        return accountRepo.deleteById(action.accountId!!).let {
            action.deleted = it
            action
        }
    }

    private fun generateAccountIdByUserId(userId: Long): String = uuidGenerator.generate(userId.toString()).toString()
}