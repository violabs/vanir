package io.violabs.freyr.service

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.violabs.freyr.FreyrTestVariables
import io.violabs.freyr.client.FreyaUserClient
import io.violabs.freyr.repository.AccountRepo
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class UserAccountServiceTest {
    private val accountRepo: AccountRepo = mockk()
    private val accountService: AccountService = mockk()
    private val userClient: FreyaUserClient = mockk()

    private val userAccountService = UserAccountService(accountRepo, accountService, userClient)

    @AfterEach
    fun tearDown() {
        confirmVerified(accountRepo, userClient)
    }

    @Test
    fun `createAccount should return null when userClient returns null`() = runBlocking {
        // given
        val userMessage = FreyrTestVariables.USER_MESSAGE
        coEvery { userClient.fetchUser(userMessage) } returns null

        // when
        val result = userAccountService.createAccount(userMessage)

        // then
        assert(result == null)
        coVerify(exactly = 1) { userClient.fetchUser(userMessage) }
    }

    @Test
    fun `createAccount should return account when userClient returns user`() = runBlocking {
        // given
        val userMessage = FreyrTestVariables.USER_MESSAGE
        val user = FreyrTestVariables.USER
        coEvery { userClient.fetchUser(userMessage) } returns user
        coEvery { accountService.saveAccount(user, any()) } returns FreyrTestVariables.NEW_ACCOUNT

        // when
        val result = userAccountService.createAccount(userMessage)

        // then
        assert(result == FreyrTestVariables.NEW_ACCOUNT)
        coVerify(exactly = 1) { userClient.fetchUser(userMessage) }
        coVerify(exactly = 1) { accountService.saveAccount(user, any()) }
    }

    @Test
    fun `updateAccount should return null when userClient returns null`() = runBlocking {
        // given
        val userMessage = FreyrTestVariables.USER_MESSAGE
        coEvery { userClient.fetchUser(userMessage) } returns null

        // when
        val result = userAccountService.updateAccount(userMessage)

        // then
        assert(result == null)
        coVerify(exactly = 1) { userClient.fetchUser(userMessage) }
    }

    // todo swap out for new user
    @Test
    fun `updateAccount should return account when userClient returns user`() = runBlocking {
        // given
        val userMessage = FreyrTestVariables.USER_MESSAGE
        val user = FreyrTestVariables.USER
        coEvery { userClient.fetchUser(userMessage) } returns user
        coEvery { accountService.saveAccount(user, any()) } returns FreyrTestVariables.NEW_ACCOUNT

        // when
        val result = userAccountService.updateAccount(userMessage)

        // then
        assert(result == FreyrTestVariables.NEW_ACCOUNT)
        coVerify(exactly = 1) { userClient.fetchUser(userMessage) }
        coVerify(exactly = 1) { accountService.saveAccount(user, any()) }
    }

    @Test
    fun `deleteAccount should return true when accountService returns true`() = runBlocking {
        // given
        val userMessage = FreyrTestVariables.USER_MESSAGE
        val userId = userMessage.userId
        coEvery { accountService.deleteAccountByUserId(userId) } returns true

        // when
        val result = userAccountService.deleteAccount(userMessage)

        // then
        assert(result)
        coVerify(exactly = 1) { accountService.deleteAccountByUserId(userId) }
    }

    @Test
    fun `deleteAccount should return false when accountService returns false`() = runBlocking {
        // given
        val userMessage = FreyrTestVariables.USER_MESSAGE
        val userId = userMessage.userId
        coEvery { accountService.deleteAccountByUserId(userId) } returns false

        // when
        val result = userAccountService.deleteAccount(userMessage)

        // then
        assert(!result)
        coVerify(exactly = 1) { accountService.deleteAccountByUserId(userId) }
    }
}