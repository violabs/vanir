package io.violabs.freyr.service

import io.violabs.core.TestUtils
import io.violabs.freyr.config.AccountRedisOps
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

@SpringBootTest
@Import(RedisReactiveAutoConfiguration::class)
class AccountServiceIntegrationTest(
    @Autowired private val accountService: AccountService,
    @Autowired private val accountRedisOps: AccountRedisOps
) {
    private val sharedAccount = Account("1", 1, listOf("abc", "def"))

    @BeforeEach
    fun setup() = runBlocking {
        val keys: Flow<String> = accountRedisOps.keys("*").asFlow()
        keys
            .map { accountRedisOps.delete(it).awaitSingleOrNull() }
            .toList()
            .forEach { println("Deleting with id=$it") }
    }

    @Test
    fun `saveAccount throws exception if id is null`(): Unit = runBlocking {
        assertThrows<Exception> { accountService.saveAccount(Account()) }
    }

    @Test
    fun  `saveAccount saves account to redis`() = runBlocking {
        //when
        val actual = accountService.saveAccount(sharedAccount)

        //then
        assert(actual) {
            "Was not able to save $sharedAccount"
        }
    }

    @Test
    fun `findAccountById will return null if not found`() = runBlocking {
        //when
        val actual: Account? = accountService.findAccountById("1")

        //then
        assert(actual == null) {
            "Found $actual when it should have been null"
        }
    }

    @Test
    fun `findAccountById will return account if found`() = runBlocking {
        //given
        accountService.saveAccount(sharedAccount)

        //when
        val actual: Account? = accountService.findAccountById("1")

        //then
        assert(actual == sharedAccount) {
            "Found $actual when it should have been $sharedAccount"
        }
    }

    @Test
    fun `deleteAccountById will return false when account does not exist`() = runBlocking {
        //when
        val actual: Boolean = accountService.deleteAccountById("1")

        //then
        assert(!actual) {
            "Was not able to delete account with id 1"
        }
    }

    @Test
    fun `deleteAccountById will return true when account exists`() = runBlocking {
        //given
        createAccount(sharedAccount)

        //when
        val actual: Boolean = accountService.deleteAccountById("1")

        //then
        assert(actual) {
            "Was not able to delete account with id 1"
        }
    }

    @Test
    fun `findAllAccounts will return empty list when no accounts exist`() = runBlocking {
        //when
        val actual: List<Account> = accountService.findAllAccounts().toList()

        //then
        assert(actual.isEmpty()) {
            "Found $actual when it should have been empty"
        }
    }

    @Test
    fun `findAllAccounts will return list of accounts when accounts exist`() = runBlocking {
        //given
        createAccount(sharedAccount)
        val account2 = Account("2", 2, listOf("ghi", "jkl"))
        createAccount(account2)
        val expected = listOf(sharedAccount, account2)

        //when
        val actual: List<Account> = accountService.findAllAccounts().toList()

        //then
        TestUtils.assertContains(actual, expected)
    }

    private suspend fun createAccount(account: Account): Account {
        accountRedisOps
            .opsForValue()
            .set(account.id!!, account)
            .awaitSingleOrNull() ?: throw Exception("Was not able to save $account")

        return account
    }
}