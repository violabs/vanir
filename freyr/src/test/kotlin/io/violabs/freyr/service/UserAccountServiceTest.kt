//package io.violabs.freyr.service
//
//import io.mockk.coEvery
//import io.mockk.coVerify
//import io.mockk.confirmVerified
//import io.mockk.mockk
//import io.violabs.freyr.FreyrTestVariables
//import io.violabs.freyr.client.FreyaUserClient
//import io.violabs.freyr.config.UuidGenerator
//import io.violabs.freyr.domain.Account
//import io.violabs.freyr.domain.AppUser
//import io.violabs.freyr.repository.AccountRepo
//import kotlinx.coroutines.runBlocking
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.Test
//
//class UserAccountServiceTest {
//    private val accountRepo: AccountRepo = mockk()
//    private val accountService: AccountService = mockk()
//    private val userClient: FreyaUserClient = mockk()
//
//    private val userAccountService = UserAccountService(accountRepo, accountService, userClient)
//
//    @AfterEach
//    fun tearDown() {
//        confirmVerified(accountRepo, userClient)
//    }
//
//    //region createAccount
//    @Test
//    fun `createAccount will return null if client returns null`() = runBlocking {
//        //given
//        coEvery { userClient.fetchUser(FreyrTestVariables.USER_MESSAGE) } returns null
//
//        //when
//        val actual: Account? = userAccountService.createAccount(FreyrTestVariables.USER_MESSAGE)
//
//        //then
//        coVerify { userClient.fetchUser(FreyrTestVariables.USER_MESSAGE) }
//        assert(actual == null)
//    }
//
//    @Test
//    fun `createAccount will return null if id is missing`() = runBlocking {
//        //given
//        coEvery { userClient.fetchUser(FreyrTestVariables.USER_MESSAGE) } returns AppUser()
//
//        //when
//        val actual: Account? = userAccountService.createAccount(FreyrTestVariables.USER_MESSAGE)
//
//        //then
//        coVerify { userClient.fetchUser(FreyrTestVariables.USER_MESSAGE) }
//        assert(actual == null)
//    }
//
//    @Test
//    fun `createAccount will return null account was not saved`() = runBlocking {
//        //given
//        coEvery { userClient.fetchUser(FreyrTestVariables.USER_MESSAGE) } returns FreyrTestVariables.USER
//        coEvery { uuidGenerator.generate(FreyrTestVariables.USER.id.toString()) } returns FreyrTestVariables.ACCOUNT_UUID
//        coEvery { accountRepo.saveAccount(FreyrTestVariables.NEW_ACCOUNT) } returns false
//
//        //when
//        val actual: Account? = userAccountService.createAccount(FreyrTestVariables.USER_MESSAGE)
//
//        //then
//        coVerify { userClient.fetchUser(FreyrTestVariables.USER_MESSAGE) }
//        coVerify { uuidGenerator.generate(FreyrTestVariables.USER.id.toString()) }
//        coVerify { accountRepo.saveAccount(FreyrTestVariables.NEW_ACCOUNT) }
//        assert(actual == null)
//    }
//
//    @Test
//    fun `createAccount will return account when saved`() = runBlocking {
//        //given
//        coEvery { userClient.fetchUser(FreyrTestVariables.USER_MESSAGE) } returns FreyrTestVariables.USER
//        coEvery { uuidGenerator.generate(FreyrTestVariables.USER.id.toString()) } returns FreyrTestVariables.ACCOUNT_UUID
//        coEvery { accountRepo.saveAccount(FreyrTestVariables.NEW_ACCOUNT) } returns true
//
//        //when
//        val actual: Account? = userAccountService.createAccount(FreyrTestVariables.USER_MESSAGE)
//
//        //then
//        coVerify { userClient.fetchUser(FreyrTestVariables.USER_MESSAGE) }
//        coVerify { uuidGenerator.generate(FreyrTestVariables.USER.id.toString()) }
//        coVerify { accountRepo.saveAccount(FreyrTestVariables.NEW_ACCOUNT) }
//        assert(actual == FreyrTestVariables.NEW_ACCOUNT)
//    }
//    //endregion newAccount
//}