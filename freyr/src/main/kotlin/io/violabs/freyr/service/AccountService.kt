package io.violabs.freyr.service

import io.violabs.freyr.config.UuidGenerator
import io.violabs.freyr.domain.Account
import io.violabs.freyr.domain.AppUser
import io.violabs.freyr.repository.AccountRepo
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepo: AccountRepo,
    private val uuidGenerator: UuidGenerator
) {
    suspend fun saveAccount(user: AppUser, accountProvider: suspend (String, Long) -> Account?): Account? {
        val userId = user.id ?: return null

        val accountId: String =
            uuidGenerator
                .generate(userId.toString())
                .toString()

        val account: Account = accountProvider(accountId, userId) ?: return null

        val saved: Boolean = accountRepo.saveAccount(account)

        if (saved) return account

        return null
    }
}