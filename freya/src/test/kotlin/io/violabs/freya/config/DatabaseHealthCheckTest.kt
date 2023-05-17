package io.violabs.freya.config

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DatabaseHealthCheckTest(@Autowired val restTemplate: TestRestTemplate) {

    @LocalServerPort
    private val port: Int = 0

    @Test
    fun `database health check should return UP status when the database is reachable`() {
        val entity = restTemplate.getForEntity("http://localhost:$port/actuator/health", String::class.java)
        assertTrue(entity.statusCode.is2xxSuccessful)
        assertTrue(entity.body!!.contains("\"database\":{\"status\":\"UP\"}"))
    }
}