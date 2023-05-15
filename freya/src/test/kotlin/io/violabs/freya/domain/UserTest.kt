package io.violabs.freya.domain

import com.fasterxml.jackson.module.kotlin.readValue
import com.violabs.wesly.Wesley
import io.violabs.core.TestVariables
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UserTest : Wesley() {

    @Test
    fun `no args constructor instantiates`() = test {
        expect {
            val json = """
                {
                    "username": null,
                    "firstname": null,
                    "lastname": null,
                    "email": null,
                    "dateOfBirth": null,
                    "joinDate": null
                }
            """.trimIndent()

            TestVariables.objectMapper.readValue(json)
        }

        whenever { User() }
    }

    @Test
    fun `all args constructor instantiates`() = test {
        expect {
            val json = """
                {
                    "username": "username",
                    "firstname": "firstname",
                    "lastname": "lastname",
                    "email": "email",
                    "dateOfBirth": "2021-01-01",
                    "joinDate": "2021-01-01"
                }
            """.trimIndent()

            TestVariables.objectMapper.readValue(json)
        }

        whenever {
            User(
                username = "username",
                firstname = "firstname",
                lastname = "lastname",
                email = "email",
                dateOfBirth = LocalDate.of(2021, 1, 1),
                joinDate = LocalDate.of(2021, 1, 1)
            )
        }
    }
}