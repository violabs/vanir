package io.violabs.freyr.repository

import io.violabs.freyr.domain.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
open class AccountRepo(private val accountRedisOps: ReactiveRedisOperations<String, Account>) {

        suspend fun saveAccount(account: Account): Boolean {
            val id: String = account.id ?: throw Exception("Missing Id!!")

            return accountRedisOps.opsForValue().set(id, account).awaitSingleOrNull() ?: false
        }

        suspend fun findAccountById(id: String): Account? {
            return accountRedisOps.opsForValue().get(id).awaitSingleOrNull()
        }

        suspend fun deleteAccountById(id: String): Boolean {
            return accountRedisOps.opsForValue().delete(id).awaitSingle() ?: false
        }

        fun findAllAccounts(): Flow<Account> {
            return accountRedisOps.keys("*").flatMap { accountRedisOps.opsForValue().get(it) }.asFlow()
        }
}