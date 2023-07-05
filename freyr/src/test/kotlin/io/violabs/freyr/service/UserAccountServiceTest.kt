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

    private val action = FreyrTestVariables.USER_MESSAGE_ACTION

    @AfterEach
    fun tearDown() {
        confirmVerified(accountRepo, userClient)
    }

    @Test
    fun `createAccount should return null when userClient returns null`() = runBlocking {
        // given
        coEvery { userClient.fetchUser(action) } returns null

        // when
        val result = userAccountService.createAccount(action)

        // then
        assert(result == action)
        coVerify(exactly = 1) { userClient.fetchUser(action) }
    }

    @Test
    fun `createAccount should return account when userClient returns user`() = runBlocking {
        // given
        val user = FreyrTestVariables.USER
        coEvery { userClient.fetchUser(action) } returns user
        coEvery { accountService.saveAccount(action, any()) } returns action

        // when
        val result = userAccountService.createAccount(action)

        // then
        assert(result == action)
        coVerify(exactly = 1) { userClient.fetchUser(action) }
        coVerify(exactly = 1) { accountService.saveAccount(action, any()) }
    }

    @Test
    fun `updateAccount should return null when userClient returns null`() = runBlocking {
        // given
        coEvery { userClient.fetchUser(action) } returns null

        // when
        userAccountService.updateAccount(action)

        // then
        coVerify(exactly = 1) { userClient.fetchUser(action) }
    }

    // todo swap out for new user
    @Test
    fun `updateAccount should return account when userClient returns user`() = runBlocking {
        // given
        val user = FreyrTestVariables.USER
        coEvery { userClient.fetchUser(action) } returns user
        coEvery { accountService.saveAccount(action, any()) } returns action

        // when
        val result = userAccountService.updateAccount(action)

        // then
        coVerify(exactly = 1) { userClient.fetchUser(action) }
        coVerify(exactly = 1) { accountService.saveAccount(action, any()) }
    }

    @Test
    fun `deleteAccount should return true when accountService returns true`() = runBlocking {
        // given
        coEvery { accountService.deleteAccountByUserId(action) } returns action

        // when
        userAccountService.deleteAccount(action)

        // then
        coVerify(exactly = 1) { accountService.deleteAccountByUserId(action) }
    }

    @Test
    fun `deleteAccount should return false when accountService returns false`() = runBlocking {
        // given
        coEvery { accountService.deleteAccountByUserId(action) } returns action

        // when
        userAccountService.deleteAccount(action)

        // then
        coVerify(exactly = 1) { accountService.deleteAccountByUserId(action) }
    }
}