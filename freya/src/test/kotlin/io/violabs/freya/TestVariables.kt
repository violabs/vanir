package io.violabs.freya

import io.violabs.freya.domain.AppUser
import java.time.LocalDate

object TestVariables {
    const val DROP_APP_USER_TABLE_QUERY = "DROP TABLE IF EXISTS app_user"

    const val CREATE_APP_USER_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS app_user (
            id SERIAL PRIMARY KEY,
            username VARCHAR(255) NOT NULL,
            firstname VARCHAR(255) NOT NULL,
            lastname VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            date_of_birth TIMESTAMP,
            join_date TIMESTAMP
        );
    """

    val DATE_OF_BIRTH: LocalDate = LocalDate.now().minusYears(20)
    val JOIN_DATE: LocalDate = LocalDate.now().minusYears(1)

    val MAIN_USER = AppUser(
        id = 1,
        username = "testuser",
        firstname = "Test",
        lastname = "User",
        email = "testuser@test.com",
        dateOfBirth = DATE_OF_BIRTH,
        joinDate = JOIN_DATE
    )

    val PRE_SAVED_USER = MAIN_USER.copy(id = null)
}