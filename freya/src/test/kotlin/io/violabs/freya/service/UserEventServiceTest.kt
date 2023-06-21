package io.violabs.freya.service

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.violabs.freya.TestVariables.User.USER_1
import io.violabs.freya.message.UserProducer
import io.violabs.freya.service.db.UserDbService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.kafka.sender.SenderResult

class UserEventServiceTest {
    private val userProducer = mockk<UserProducer>()
    private val userDbService = mockk<UserDbService>()

    private val userEventService = UserEventService(userProducer, userDbService)

    @Test
    fun `createUser will create the user and send a message`(): Unit = runBlocking {
        //given
        val senderResult = mockk<SenderResult<Void>>()

        coEvery { userDbService.createUser(USER_1) } returns USER_1
        coEvery { userProducer.sendUserData(USER_1) } returns senderResult

        //when
        userEventService.createUser(USER_1)

        //then
        coVerify {
            userDbService.createUser(USER_1)
            userProducer.sendUserData(USER_1)
        }

        confirmVerified(userDbService, userProducer)
    }

    @Test
    fun `createUser will throw an exception if the user is not sent`(): Unit = runBlocking {
        //given
        coEvery { userDbService.createUser(USER_1) } returns USER_1
        coEvery { userProducer.sendUserData(USER_1) } returns null

        //when
        val exception = kotlin.runCatching {
            userEventService.createUser(USER_1)
        }.exceptionOrNull()

        //then
        coVerify {
            userDbService.createUser(USER_1)
            userProducer.sendUserData(USER_1)
        }

        confirmVerified(userDbService, userProducer)

        assert(exception is IllegalStateException)
    }

    @Test
    fun `updateUser will update the user and send a message`(): Unit = runBlocking {
        //given
        val senderResult = mockk<SenderResult<Void>>()

        coEvery { userDbService.updateUser(USER_1) } returns USER_1
        coEvery { userProducer.sendUserData(USER_1) } returns senderResult

        //when
        userEventService.updateUser(USER_1)

        //then
        coVerify {
            userDbService.updateUser(USER_1)
            userProducer.sendUserData(USER_1)
        }

        confirmVerified(userDbService, userProducer)
    }

    @Test
    fun `updateUser will throw an exception if the user is not sent`(): Unit = runBlocking {
        //given
        coEvery { userDbService.updateUser(USER_1) } returns USER_1
        coEvery { userProducer.sendUserData(USER_1) } returns null

        //when
        val exception = kotlin.runCatching {
            userEventService.updateUser(USER_1)
        }.exceptionOrNull()

        //then
        coVerify {
            userDbService.updateUser(USER_1)
            userProducer.sendUserData(USER_1)
        }

        confirmVerified(userDbService, userProducer)

        assert(exception is IllegalStateException)
    }

    //region deleteUserById
    @Test
    fun `deleteUserById will throw exception if user is not found`(): Unit = runBlocking {
        //given
        val id = USER_1.id!!

        coEvery { userDbService.getUserById(id) } returns null

        //when
        assertThrows<IllegalArgumentException> { userEventService.deleteUserById(id) }
    }

    @Test
    fun `deleteUserById will throw exception if user is not deleted`(): Unit = runBlocking {
        //given
        val id = USER_1.id!!

        coEvery { userDbService.getUserById(id) } returns USER_1
        coEvery { userDbService.deleteUserById(id) } returns false

        //when
        assertThrows<IllegalStateException> { userEventService.deleteUserById(id) }
    }

    @Test
    fun `deleteUserById will delete the user and send a message`(): Unit = runBlocking {
        //given
        val senderResult = mockk<SenderResult<Void>>()

        val id = USER_1.id!!

        coEvery { userDbService.getUserById(id) } returns USER_1
        coEvery { userDbService.deleteUserById(id) } returns true
        coEvery { userProducer.sendUserData(USER_1) } returns senderResult

        //when
        userEventService.deleteUserById(id)

        //then
        coVerify {
            userDbService.getUserById(id)
            userDbService.deleteUserById(id)
            userProducer.sendUserData(USER_1)
        }

        confirmVerified(userDbService, userProducer)
    }

    @Test
    fun `deleteUserById will throw an exception if the user is not sent`(): Unit = runBlocking {
        //given
        val id = USER_1.id!!

        coEvery { userDbService.getUserById(id) } returns USER_1
        coEvery { userDbService.deleteUserById(id) } returns true
        coEvery { userProducer.sendUserData(USER_1) } returns null

        //when
        val exception = kotlin.runCatching {
            userEventService.deleteUserById(id)
        }.exceptionOrNull()

        //then
        coVerify {
            userDbService.getUserById(id)
            userDbService.deleteUserById(id)
            userProducer.sendUserData(USER_1)
        }

        confirmVerified(userDbService, userProducer)

        assert(exception is IllegalStateException)
    }
    //endregion deleteUserById
}