package io.violabs.freyr

import io.violabs.freyr.repository.AccountRepo
import kotlinx.coroutines.runBlocking
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
open class RedisTestConfig {

    @Bean
    open fun testDatabaseSeeder(accountRepo: AccountRepo): TestDatabaseSeeder = TestDatabaseSeeder(accountRepo)

    class TestDatabaseSeeder(
        private val accountRepo: AccountRepo
    ) {
        fun seedAccount(): Unit = runBlocking {
            accountRepo.save(FreyrTestVariables.NEW_ACCOUNT)
        }
    }
}