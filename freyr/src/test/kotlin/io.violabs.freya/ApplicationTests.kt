package io.violabs.freya

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests(@Autowired val client: WebTestClient) {

	@Test
	fun `health endpoint returns UP`() {
		client
			.get()
			.uri("/actuator/health")
			.exchange()
			.expectStatus().isOk
			.expectBody()
			.jsonPath("$.status")
			.isEqualTo("UP")
	}

}
