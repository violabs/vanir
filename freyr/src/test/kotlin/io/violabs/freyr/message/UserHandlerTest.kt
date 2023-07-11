package io.violabs.freyr.message

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.violabs.core.domain.UserMessage
import io.violabs.freyr.FreyrTestVariables
import io.violabs.freyr.domain.UserAccountAction
import io.violabs.freyr.service.UserAccountService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class UserHandlerTest {
    private val userAccountService: UserAccountService = mockk()

    private val userHandler = UserHandler(userAccountService)

    @AfterEach
    fun teardown() {
        confirmVerified(userAccountService)
    }

    @Test
    fun `handleUserMessage will do nothing if user message is deactivate`() = runBlocking {
        //given
        val message = UserMessage(1, "test", UserMessage.Type.USER_DEACTIVATED)

        //expect
        userHandler.handleUserMessage(message)
    }

    @Test
    fun `handleUserMessage will create account if message is create`() = sharedHandleMessageTest(
        UserMessage.Type.USER_CREATED,
        FreyrTestVariables.NEW_ACCOUNT,
        UserAccountService::createAccount
    )

    @Test
    fun `handleUserMessage will update account if message is update`() = sharedHandleMessageTest(
        UserMessage.Type.USER_UPDATED,
        FreyrTestVariables.NEW_ACCOUNT,
        UserAccountService::updateAccount
    )

    @Test
    fun `handleUserMessage will delete account if message is delete`() = sharedHandleMessageTest(
        UserMessage.Type.USER_DELETED,
        true,
        UserAccountService::deleteAccount
    )

    private fun <T> sharedHandleMessageTest(
        type: UserMessage.Type,
        returned: T,
        userAccountServiceFn: suspend (UserAccountService, UserAccountAction) -> T
    ) = runBlocking {
        val message = UserMessage(1, "test", type)

        val action = UserAccountAction(message)

        coEvery { userAccountServiceFn(userAccountService, action) } returns returned

        userHandler.handleUserMessage(message)

        coVerify { userAccountServiceFn(userAccountService, action) }
    }
}