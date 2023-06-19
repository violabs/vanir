package io.violabs.freya.service

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.violabs.freya.TestVariables
import io.violabs.freya.message.UserProducer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.kafka.sender.SenderResult

class UserEventServiceTest {
    private val userProducer = mockk<UserProducer>()
    private val userService = mockk<UserService>()

    private val userEventService = UserEventService(userProducer, userService)

    @Test
    fun `createUser will create the user and send a message`(): Unit = runBlocking {
        //given
        val senderResult = mockk<SenderResult<Void>>()

        coEvery { userService.createUser(TestVariables.MAIN_USER) } returns TestVariables.MAIN_USER
        coEvery { userProducer.sendUserData(TestVariables.MAIN_USER) } returns senderResult

        //when
        userEventService.createUser(TestVariables.MAIN_USER)

        //then
        coVerify {
            userService.createUser(TestVariables.MAIN_USER)
            userProducer.sendUserData(TestVariables.MAIN_USER)
        }

        confirmVerified(userService, userProducer)
    }

    @Test
    fun `createUser will throw an exception if the user is not sent`(): Unit = runBlocking {
        //given
        coEvery { userService.createUser(TestVariables.MAIN_USER) } returns TestVariables.MAIN_USER
        coEvery { userProducer.sendUserData(TestVariables.MAIN_USER) } returns null

        //when
        val exception = kotlin.runCatching {
            userEventService.createUser(TestVariables.MAIN_USER)
        }.exceptionOrNull()

        //then
        coVerify {
            userService.createUser(TestVariables.MAIN_USER)
            userProducer.sendUserData(TestVariables.MAIN_USER)
        }

        confirmVerified(userService, userProducer)

        assert(exception is IllegalStateException)
    }

    @Test
    fun `updateUser will update the user and send a message`(): Unit = runBlocking {
        //given
        val senderResult = mockk<SenderResult<Void>>()

        coEvery { userService.updateUser(TestVariables.MAIN_USER) } returns TestVariables.MAIN_USER
        coEvery { userProducer.sendUserData(TestVariables.MAIN_USER) } returns senderResult

        //when
        userEventService.updateUser(TestVariables.MAIN_USER)

        //then
        coVerify {
            userService.updateUser(TestVariables.MAIN_USER)
            userProducer.sendUserData(TestVariables.MAIN_USER)
        }

        confirmVerified(userService, userProducer)
    }

    @Test
    fun `updateUser will throw an exception if the user is not sent`(): Unit = runBlocking {
        //given
        coEvery { userService.updateUser(TestVariables.MAIN_USER) } returns TestVariables.MAIN_USER
        coEvery { userProducer.sendUserData(TestVariables.MAIN_USER) } returns null

        //when
        val exception = kotlin.runCatching {
            userEventService.updateUser(TestVariables.MAIN_USER)
        }.exceptionOrNull()

        //then
        coVerify {
            userService.updateUser(TestVariables.MAIN_USER)
            userProducer.sendUserData(TestVariables.MAIN_USER)
        }

        confirmVerified(userService, userProducer)

        assert(exception is IllegalStateException)
    }

    //region deleteUserById
    @Test
    fun `deleteUserById will throw exception if user is not found`(): Unit = runBlocking {
        //given
        val id = TestVariables.MAIN_USER.id!!

        coEvery { userService.getUserById(id) } returns null

        //when
        assertThrows<IllegalArgumentException> { userEventService.deleteUserById(id) }
    }

    @Test
    fun `deleteUserById will throw exception if user is not deleted`(): Unit = runBlocking {
        //given
        val id = TestVariables.MAIN_USER.id!!

        coEvery { userService.getUserById(id) } returns TestVariables.MAIN_USER
        coEvery { userService.deleteUserById(id) } returns false

        //when
        assertThrows<IllegalStateException> { userEventService.deleteUserById(id) }
    }

    @Test
    fun `deleteUserById will delete the user and send a message`(): Unit = runBlocking {
        //given
        val senderResult = mockk<SenderResult<Void>>()

        val id = TestVariables.MAIN_USER.id!!

        coEvery { userService.getUserById(id) } returns TestVariables.MAIN_USER
        coEvery { userService.deleteUserById(id) } returns true
        coEvery { userProducer.sendUserData(TestVariables.MAIN_USER) } returns senderResult

        //when
        userEventService.deleteUserById(id)

        //then
        coVerify {
            userService.getUserById(id)
            userService.deleteUserById(id)
            userProducer.sendUserData(TestVariables.MAIN_USER)
        }

        confirmVerified(userService, userProducer)
    }

    @Test
    fun `deleteUserById will throw an exception if the user is not sent`(): Unit = runBlocking {
        //given
        val id = TestVariables.MAIN_USER.id!!

        coEvery { userService.getUserById(id) } returns TestVariables.MAIN_USER
        coEvery { userService.deleteUserById(id) } returns true
        coEvery { userProducer.sendUserData(TestVariables.MAIN_USER) } returns null

        //when
        val exception = kotlin.runCatching {
            userEventService.deleteUserById(id)
        }.exceptionOrNull()

        //then
        coVerify {
            userService.getUserById(id)
            userService.deleteUserById(id)
            userProducer.sendUserData(TestVariables.MAIN_USER)
        }

        confirmVerified(userService, userProducer)

        assert(exception is IllegalStateException)
    }
    //endregion deleteUserById
}