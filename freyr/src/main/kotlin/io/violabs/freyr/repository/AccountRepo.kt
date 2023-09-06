package io.violabs.freyr.repository

import io.violabs.freyr.domain.Account
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.stereotype.Service

@Service
class AccountRepo(@Autowired factory: ReactiveRedisConnectionFactory) :
    RedisRepo<Account>(factory, Account::class.java, "account") {

    suspend fun save(value: Account): Boolean = super.save(value, value.id)
}