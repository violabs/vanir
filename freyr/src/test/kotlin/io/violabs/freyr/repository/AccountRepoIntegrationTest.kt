package io.violabs.freyr.repository

import io.violabs.core.TestUtils
import io.violabs.freyr.domain.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory

@SpringBootTest
@Import(RedisReactiveAutoConfiguration::class)
class AccountRepoIntegrationTest(
    @Autowired private val accountRepo: AccountRepo,
    @Autowired private val factory: ReactiveRedisConnectionFactory
) {
    private val accountRedisOps: RedisOps<Account> = RedisRepo.createRedisOps(factory, Account::class.java)

    private val sharedAccount = Account("1", 1, mutableListOf("abc", "def"))

    @BeforeEach
    fun setup() = runBlocking {
        val keys: Flow<String> = accountRedisOps.keys("*").asFlow()
        keys
            .map { accountRedisOps.delete(it).awaitSingleOrNull() }
            .toList()
            .forEach { println("Deleting with id=$it") }
    }

    @Test
    fun `save throws exception if id is null`(): Unit = runBlocking {
        assertThrows<Exception> { accountRepo.save(Account()) }
    }

    @Test
    fun  `save saves account to redis`() = runBlocking {
        //when
        val actual = accountRepo.save(sharedAccount)

        //then
        assert(actual) {
            "Was not able to save $sharedAccount"
        }
    }

    @Test
    fun `findById will return null if not found`() = runBlocking {
        //when
        val actual: Account? = accountRepo.findById("1")

        //then
        assert(actual == null) {
            "Found $actual when it should have been null"
        }
    }

    @Test
    fun `findById will return account if found`() = runBlocking {
        //given
        accountRepo.save(sharedAccount)

        //when
        val actual: Account? = accountRepo.findById("1")

        //then
        assert(actual == sharedAccount) {
            "Found $actual when it should have been $sharedAccount"
        }
    }

    @Test
    fun `deleteById will return false when account does not exist`() = runBlocking {
        //when
        val actual: Boolean = accountRepo.deleteById("1")

        //then
        assert(!actual) {
            "Was not able to delete account with id 1"
        }
    }

    @Test
    fun `deleteById will return true when account exists`() = runBlocking {
        //given
        createAccount(sharedAccount)

        //when
        val actual: Boolean = accountRepo.deleteById("1")

        //then
        assert(actual) {
            "Was not able to delete account with id 1"
        }
    }

    @Test
    fun `findAll will return empty list when no accounts exist`() = runBlocking {
        //when
        val actual: List<Account> = accountRepo.findAll().toList()

        //then
        assert(actual.isEmpty()) {
            "Found $actual when it should have been empty"
        }
    }

    @Test
    fun `findAll will return list of accounts when accounts exist`() = runBlocking {
        //given
        createAccount(sharedAccount)
        val account2 = Account("2", 2, mutableListOf("ghi", "jkl"))
        createAccount(account2)
        val expected = listOf(sharedAccount, account2)

        //when
        val actual: List<Account> = accountRepo.findAll().toList()

        //then
        TestUtils.assertContains(actual, expected)
    }

    private suspend fun createAccount(account: Account): Account {
        accountRedisOps
            .opsForValue()
            .set(makeId(account.id!!), account)
            .awaitSingleOrNull() ?: throw Exception("Was not able to save $account")

        return account
    }

    private fun makeId(uuid: String): String = "account:$uuid"
}