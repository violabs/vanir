package io.violabs.freya.config

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOneOrNull

@Configuration
open class DatabaseConfig(@Autowired private val databaseClient: DatabaseClient) {

    @PostConstruct
    fun buildDb() = runBlocking {
        println("Building database...")
        Sql
            .queries
            .stream()
            .map(CreateQuery::query)
            .map(databaseClient::sql)
            .map(DatabaseClient.GenericExecuteSpec::fetch)
            .forEach {
                runBlocking {
                    it.awaitOneOrNull()
                }
            }
    }

    private object Sql {
        private val userBook = CreateQuery(
            """
                CREATE TABLE IF NOT EXISTS user_book (
                    id SERIAL PRIMARY KEY,
                    user_id INT NOT NULL,
                    book_id INT NOT NULL,
                    added_on TIMESTAMP NOT NULL
                );
            """
        )

        private val user = CreateQuery(
            """
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
        )

        private val book = CreateQuery(
            """
                CREATE TABLE IF NOT EXISTS book (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    author VARCHAR(255) NOT NULL
                );
            """
        )

        val queries = listOf(
            userBook,
            user,
            book
        )
    }

    @JvmInline
    private value class CreateQuery(val query: String)
}