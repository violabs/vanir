package io.violabs.freyr.service

import io.mockk.*
import io.violabs.freyr.FreyrTestVariables.ACCOUNT_UUID
import io.violabs.freyr.FreyrTestVariables.NEW_ACCOUNT
import io.violabs.freyr.FreyrTestVariables.USER
import io.violabs.freyr.config.UuidGenerator
import io.violabs.freyr.domain.Account
import io.violabs.freyr.domain.AppUser
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
    fun `saveAccount will return null if id is missing`() = runBlocking {
        //given
        val user = AppUser()

        //when
        val actual: Account? = accountService.saveAccount(user) { _, _ -> null }

        //then
        assert(actual == null)
    }

    @Test
    fun `saveAccount will return null if account was not saved`() = runBlocking {
        //given
        val accountProvider: suspend (String, Long) -> Account? = { _, _ -> NEW_ACCOUNT }
        every { uuidGenerator.generate(USER.id.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.save(NEW_ACCOUNT) } returns false

        //when
        val actual: Account? = accountService.saveAccount(USER, accountProvider)

        //then
        verify { uuidGenerator.generate(USER.id.toString()) }
        coVerify { accountRepo.save(NEW_ACCOUNT) }
        assert(actual == null)
    }

    @Test
    fun `saveAccount will return account if account was saved`() = runBlocking {
        //given
        val accountProvider: suspend (String, Long) -> Account? = { _, _ -> NEW_ACCOUNT }
        every { uuidGenerator.generate(USER.id.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.save(NEW_ACCOUNT) } returns true

        //when
        val actual: Account? = accountService.saveAccount(USER, accountProvider)

        //then
        verify { uuidGenerator.generate(USER.id.toString()) }
        coVerify { accountRepo.save(NEW_ACCOUNT) }
        assert(actual == NEW_ACCOUNT)
    }
    //endregion saveAccount

    //region deleteAccountByUserId
    @Test
    fun `deleteAccountByUserId will return false if account was not deleted`() = runBlocking {
        //given
        every { uuidGenerator.generate(USER.id.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.deleteById(ACCOUNT_UUID.toString()) } returns false

        //when
        val actual: Boolean = accountService.deleteAccountByUserId(USER.id!!)

        //then
        verify { uuidGenerator.generate(USER.id.toString()) }
        coVerify { accountRepo.deleteById(ACCOUNT_UUID.toString()) }
        assert(!actual)
    }

    @Test
    fun `deleteAccountByUserId will return true if account was deleted`() = runBlocking {
        //given
        every { uuidGenerator.generate(USER.id.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.deleteById(ACCOUNT_UUID.toString()) } returns true

        //when
        val actual: Boolean = accountService.deleteAccountByUserId(USER.id!!)

        //then
        verify { uuidGenerator.generate(USER.id.toString()) }
        coVerify { accountRepo.deleteById(ACCOUNT_UUID.toString()) }
        assert(actual)
    }
    //endregion deleteAccountByUserId
}