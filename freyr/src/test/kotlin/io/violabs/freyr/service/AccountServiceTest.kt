package io.violabs.freyr.service

import io.mockk.*
import io.violabs.freyr.FreyrTestVariables.ACCOUNT_UUID
import io.violabs.freyr.FreyrTestVariables.NEW_ACCOUNT
import io.violabs.freyr.FreyrTestVariables.USER
import io.violabs.freyr.FreyrTestVariables.USER_MESSAGE_ACTION
import io.violabs.freyr.config.UuidGenerator
import io.violabs.freyr.domain.Account
import io.violabs.freyr.domain.UserAccountAction
import io.violabs.freyr.repository.AccountRepo
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class AccountServiceTest {
    private val accountRepo: AccountRepo = mockk()
    private val uuidGenerator: UuidGenerator = mockk()

    private val accountService = AccountService(accountRepo, uuidGenerator)

    @AfterEach
    fun tearDown() {
        confirmVerified(accountRepo, uuidGenerator)
    }

    //region saveAccount
    @Test
    fun `saveAccount will return null if account is missing`() = runBlocking {
        //given
        every { uuidGenerator.generate(USER.id.toString()) } returns ACCOUNT_UUID

        //when
        val actual: UserAccountAction? = accountService.saveAccount(USER_MESSAGE_ACTION) { _ -> null }

        //then
        assert(actual == null)
        verify { uuidGenerator.generate(USER.id.toString()) }
    }

    @Test
    fun `saveAccount will return null if account was not saved`() = runBlocking {
        //given
        val accountProvider: suspend (String) -> Account? = { _ -> NEW_ACCOUNT }
        every { uuidGenerator.generate(USER.id.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.save(NEW_ACCOUNT) } returns false

        //when
        val actual: UserAccountAction? = accountService.saveAccount(USER_MESSAGE_ACTION, accountProvider)

        //then
        verify { uuidGenerator.generate(USER.id.toString()) }
        coVerify { accountRepo.save(NEW_ACCOUNT) }
        assert(actual == USER_MESSAGE_ACTION)
    }

    @Test
    fun `saveAccount will return account if account was saved`() = runBlocking {
        //given
        val accountProvider: suspend (String) -> Account? = { _ -> NEW_ACCOUNT }
        every { uuidGenerator.generate(USER.id.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.save(NEW_ACCOUNT) } returns true

        //when
        val actual: UserAccountAction? = accountService.saveAccount(USER_MESSAGE_ACTION, accountProvider)

        //then
        verify { uuidGenerator.generate(USER.id.toString()) }
        coVerify { accountRepo.save(NEW_ACCOUNT) }
        assert(actual == USER_MESSAGE_ACTION.copy(account = NEW_ACCOUNT, saved = true))
    }
    //endregion saveAccount

    //region deleteAccountByUserId
    @Test
    fun `deleteAccountByUserId will return false if account was not deleted`() = runBlocking {
        //given
        every { uuidGenerator.generate(USER.id.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.deleteById(ACCOUNT_UUID.toString()) } returns false

        //when
        val actual: UserAccountAction = accountService.deleteAccountByUserId(USER_MESSAGE_ACTION)

        //then
        verify { uuidGenerator.generate(USER.id.toString()) }
        coVerify { accountRepo.deleteById(ACCOUNT_UUID.toString()) }
        assert(actual.deleted == false)
    }

    @Test
    fun `deleteAccountByUserId will return true if account was deleted`() = runBlocking {
        //given
        every { uuidGenerator.generate(USER.id.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.deleteById(ACCOUNT_UUID.toString()) } returns true

        //when
        val actual: UserAccountAction = accountService.deleteAccountByUserId(USER_MESSAGE_ACTION)

        //then
        verify { uuidGenerator.generate(USER.id.toString()) }
        coVerify { accountRepo.deleteById(ACCOUNT_UUID.toString()) }
        assert(actual.deleted == true)
    }
    //endregion deleteAccountByUserId
}