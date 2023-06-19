package io.violabs.freya

import io.violabs.freya.domain.AppUser
import io.violabs.freya.domain.Book
import java.time.LocalDate
import java.time.ZonedDateTime

object TestVariables {
    object Library {
        const val DROP_LIBRARY_TABLE_QUERY = "DROP TABLE IF EXISTS library"

        const val CREATE_LIBRARY_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS library (
                id SERIAL PRIMARY KEY,
                user_id INT NOT NULL,
                book_id INT NOT NULL,
                added_on TIMESTAMP NOT NULL
            );
        """

        val MAIN_LIBRARY = io.violabs.freya.domain.Library(
            id = 1,
            userId = 1,
            bookId = 1,
            addedOn = ZonedDateTime.now().toInstant(),
        )

        val PRE_SAVED_LIBRARY = MAIN_LIBRARY.copy(id = null)
    }

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

    const val DROP_BOOK_TABLE_QUERY = "DROP TABLE IF EXISTS book"

    const val CREATE_BOOK_TABLE_QUERY = """
        CREATE TABLE IF NOT EXISTS book (
            id SERIAL PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            author VARCHAR(255) NOT NULL,
            number_left_in_stock INT NOT NULL
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

    val MAIN_BOOK = Book(
        id = 1,
        title = "Test Book",
        author = "Test Author"
    )

    val PRE_SAVED_BOOK = MAIN_BOOK.copy(id = null)
}