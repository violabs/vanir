package io.violabs.freya

import io.violabs.freya.domain.AppUser
import io.violabs.freya.domain.Book
import io.violabs.freya.domain.UserBook
import java.time.LocalDate
import java.time.ZonedDateTime

object TestVariables {
    object Book {
        const val DROP_BOOK_TABLE_QUERY = "DROP TABLE IF EXISTS book"

        const val CREATE_BOOK_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS book (
                id SERIAL PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                author VARCHAR(255) NOT NULL
            );
        """

        val BOOK_1 = Book(
            id = 1,
            title = "Test Book",
            author = "Test Author"
        )

        val BOOK_2 = Book(
            id = 2,
            title = "Test Book 2",
            author = "Test Author 2"
        )

        val PRE_SAVED_BOOK_1 = BOOK_1.copy(id = null)
        val PRE_SAVED_BOOK_2 = BOOK_2.copy(id = null)
    }

    object UserBook {
        val USER_BOOK_1 = UserBook(
            id = 1,
            userId = 1,
            bookId = 1,
            addedOn = ZonedDateTime.now().toInstant(),
        )

        val USER_BOOK_2 = UserBook(
            id = 2,
            userId = 1,
            bookId = 2,
            addedOn = ZonedDateTime.now().toInstant(),
        )

        val PRE_SAVED_USER_BOOK_1 = USER_BOOK_1.copy(id = null)
        val PRE_SAVED_USER_BOOK_2 = USER_BOOK_2.copy(id = null)
    }

    object User {
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

        val USER_1 = AppUser(
            id = 1,
            username = "testuser",
            firstname = "Test",
            lastname = "User",
            email = "testuser@test.com",
            dateOfBirth = DATE_OF_BIRTH,
            joinDate = JOIN_DATE
        )

        val USER_2 = AppUser(
            id = 2,
            username = "testuser2",
            firstname = "Test2",
            lastname = "User2",
            email = "test2@test.com",
            dateOfBirth = DATE_OF_BIRTH,
            joinDate = JOIN_DATE
        )

        val PRE_SAVED_USER_1 = USER_1.copy(id = null)
        val PRE_SAVED_USER_2 = USER_2.copy(id = null)
    }
}