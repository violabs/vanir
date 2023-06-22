package io.violabs.freya

import io.violabs.freya.repository.BookRepository
import io.violabs.freya.repository.UserBookRepository
import io.violabs.freya.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOneOrNull

@TestConfiguration
open class DatabaseTestConfig {

    @Bean
    open fun testDatabaseSeeder(
        bookRepository: BookRepository,
        userBookRepository: UserBookRepository,
        userRepository: UserRepository,
        dbClient: DatabaseClient
    ): TestDatabaseSeeder {
        return TestDatabaseSeeder(bookRepository, userBookRepository, userRepository, dbClient)
    }

    class TestDatabaseSeeder(
        private val bookRepository: BookRepository,
        private val userBookRepository: UserBookRepository,
        private val userRepository: UserRepository,
        private val dbClient: DatabaseClient
    ) {
        fun seedBook(): Unit = runBlocking {
            truncateBook()

            listOf(TestVariables.Book.PRE_SAVED_BOOK_1, TestVariables.Book.PRE_SAVED_BOOK_2)
                .map { bookRepository.save(it) }
                .forEach(::println)
        }

        fun seedUserBook(): Unit = runBlocking {
            truncateUserBook()

            listOf(TestVariables.UserBook.PRE_SAVED_USER_BOOK_1, TestVariables.UserBook.PRE_SAVED_USER_BOOK_2)
                .map { userBookRepository.save(it) }
                .forEach(::println)
        }

        fun seedUser(): Unit = runBlocking {
            truncateUser()

            listOf(TestVariables.User.PRE_SAVED_USER_1, TestVariables.User.PRE_SAVED_USER_2)
                .map { userRepository.save(it) }
                .forEach(::println)
        }

        fun truncateUserBook(): Unit = runBlocking {
            truncate("user_book")
        }

        fun truncateUser(): Unit = runBlocking {
            truncate("app_user")
        }

        fun truncateBook(): Unit = runBlocking {
            truncate("book")
        }

        private suspend fun truncate(tableName: String) {
            dbClient.sql("TRUNCATE TABLE $tableName").fetch().awaitOneOrNull()

            val sequenceName = "${tableName}_id_seq"

            dbClient.sql("ALTER SEQUENCE $sequenceName RESTART").fetch().awaitOneOrNull()

            dbClient.sql("UPDATE $tableName SET id = DEFAULT;").fetch().awaitOneOrNull()
        }

        fun seedAll() {
            seedBook()
            seedUserBook()
            seedUser()
        }
    }
}