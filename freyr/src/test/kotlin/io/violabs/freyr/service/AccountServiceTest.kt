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

    //region addOrderToAccount
    @Test
    fun `addOrderToAccount will return false if account was not found`() = runBlocking {
        //given
        val userId = 1L
        val orderId = "abc"
        every { uuidGenerator.generate(userId.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.findById(ACCOUNT_UUID.toString()) } returns null

        //when
        val actual: Boolean = accountService.addOrderToAccount(userId, orderId)

        //then
        verify { uuidGenerator.generate(userId.toString()) }
        coVerify { accountRepo.findById(ACCOUNT_UUID.toString()) }
        assert(!actual)
    }

    @Test
    fun `addOrderToAccount will return false if account was not saved`() = runBlocking {
        //given
        val userId = 1L
        val orderId = "abc"
        every { uuidGenerator.generate(userId.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.findById(ACCOUNT_UUID.toString()) } returns NEW_ACCOUNT
        coEvery { accountRepo.save(NEW_ACCOUNT) } returns false

        //when
        val actual: Boolean = accountService.addOrderToAccount(userId, orderId)

        //then
        verify { uuidGenerator.generate(userId.toString()) }
        coVerify { accountRepo.findById(ACCOUNT_UUID.toString()) }
        coVerify { accountRepo.save(NEW_ACCOUNT) }
        assert(!actual)
    }

    @Test
    fun `addOrderToAccount will return true if account was saved`() = runBlocking {
        //given
        val userId = 1L
        val orderId = "abc"
        every { uuidGenerator.generate(userId.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.findById(ACCOUNT_UUID.toString()) } returns NEW_ACCOUNT
        coEvery { accountRepo.save(NEW_ACCOUNT) } returns true

        //when
        val actual: Boolean = accountService.addOrderToAccount(userId, orderId)

        //then
        verify { uuidGenerator.generate(userId.toString()) }
        coVerify { accountRepo.findById(ACCOUNT_UUID.toString()) }
        coVerify { accountRepo.save(NEW_ACCOUNT) }
        assert(actual)
    }
    //endregion addOrderToAccount

    //region getAccountByUserId
    @Test
    fun `getAccountByUserId will correctly find the user`() = runBlocking {
        //given
        val userId = 1L
        every { uuidGenerator.generate(userId.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.findById(ACCOUNT_UUID.toString()) } returns NEW_ACCOUNT

        //when
        val actual: Account? = accountService.getAccountByUserId(userId)

        //then
        verify { uuidGenerator.generate(userId.toString()) }
        coVerify { accountRepo.findById(ACCOUNT_UUID.toString()) }
        assert(actual == NEW_ACCOUNT)
    }

    @Test
    fun `getAccountByUserId will return null if not found`() = runBlocking {
        //given
        val userId = 1L
        every { uuidGenerator.generate(userId.toString()) } returns ACCOUNT_UUID
        coEvery { accountRepo.findById(ACCOUNT_UUID.toString()) } returns null

        //when
        val actual: Account? = accountService.getAccountByUserId(userId)

        //then
        verify { uuidGenerator.generate(userId.toString()) }
        coVerify { accountRepo.findById(ACCOUNT_UUID.toString()) }
        assert(actual == null)
    }
    //endregion getAccountByUserId

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