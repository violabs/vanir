package io.violabs.freyr.client

import io.violabs.core.TestUtils
import io.violabs.core.domain.UserMessage
import io.violabs.freyr.domain.AppUser
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClientResponseException

@SpringBootTest
class FreyaUserClientIntegrationTest(@Autowired private val freyaUserClient: FreyaUserClient) {

    private val sharedMessage = UserMessage(
        userId = 1L,
        uri = "http://localhost:8083/user/1",
        type = UserMessage.Type.USER_CREATED
    )

    @BeforeEach
    fun setup() {
//        server.start(8083)
        clearRequests()
    }

//    @AfterEach
//    fun tearDown() {
//        server.shutdown()
//    }

    @Test
    fun `fetchUser will return a 404`(): Unit = runBlocking {
        //given
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("User not found")
        )

        //when
        assertThrows<WebClientResponseException.NotFound> { freyaUserClient.fetchUser(sharedMessage) }
    }

    @Test
    fun `fetchUser will return a 200 and no user`() {
        //given
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")
        )

        //when
        val actual: AppUser? = runBlocking { freyaUserClient.fetchUser(sharedMessage) }

        //then
        Assertions.assertNull(actual)
    }

    @Test
    fun `fetchUser will return a 200 and a user`() {
        //given
        val jsonBody =
            """
                { 
                    "id": 1,
                    "username": "test",
                    "firstname": "test",
                    "lastname": "test",
                    "email": "test@test.com",
                    "dateOfBirth": "2020-01-01",
                    "joinDate": "2020-01-01"
                }
            """.trimIndent()

        val mockResponse =
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(jsonBody)

        server.enqueue(mockResponse)

        val expected = AppUser(
            id = 1,
            username = "test",
            firstname = "test",
            lastname = "test",
            email = "test@test.com"
        )

        //when
        val actual: AppUser? = runBlocking { freyaUserClient.fetchUser(sharedMessage) }

        //then
        TestUtils.assertEquals(expected, actual)
    }

    companion object {
        private val server = MockWebServer()

        @JvmStatic
        @BeforeAll
        fun setupAll() {
            server.start(8083)
        }

        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            server.shutdown()
        }


        fun clearRequests() {
            0.until(server.requestCount).forEach { _ -> server.takeRequest() }
        }
    }
}