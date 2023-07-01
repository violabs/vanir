package io.violabs.freyr.repository

import io.violabs.freyr.domain.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.stereotype.Service

@Service
class AccountRepo(@Autowired factory: ReactiveRedisConnectionFactory) :
    RedisRepo<Account>(factory, Account::class.java) {

    suspend fun saveAccount(account: Account): Boolean {
        val id: String = account.id ?: throw Exception("Missing Id!!")

        return operations.opsForValue().set(id, account).awaitSingleOrNull() ?: false
    }

    suspend fun findAccountById(id: String): Account? {
        return operations.opsForValue().get(id).awaitSingleOrNull()
    }

    suspend fun deleteAccountById(id: String): Boolean {
        return operations.opsForValue().delete(id).awaitSingle() ?: false
    }

    fun findAllAccounts(): Flow<Account> {
        return operations.keys("*").flatMap { operations.opsForValue().get(it) }.asFlow()
    }
}