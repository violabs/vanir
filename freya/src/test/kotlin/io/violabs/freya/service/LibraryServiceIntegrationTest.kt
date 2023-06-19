package io.violabs.freya.service

import io.violabs.freya.TestVariables
import io.violabs.freya.TestVariables.Library.MAIN_LIBRARY
import io.violabs.freya.TestVariables.Library.PRE_SAVED_LIBRARY
import io.violabs.freya.TestVariables.Library.DROP_LIBRARY_TABLE_QUERY
import io.violabs.freya.TestVariables.Library.CREATE_LIBRARY_TABLE_QUERY
import io.violabs.freya.domain.Library
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataR2dbcTest
@Import(LibraryService::class)
@ExtendWith(SpringExtension::class)
class LibraryServiceIntegrationTest(
    @Autowired val libraryService: LibraryService,
    @Autowired val dbClient: DatabaseClient
) {

    @BeforeEach
    fun setup(): Unit = runBlocking {
        dbClient.sql(DROP_LIBRARY_TABLE_QUERY).fetch().awaitOneOrNull()
        dbClient.sql(CREATE_LIBRARY_TABLE_QUERY).fetch().awaitOneOrNull()
    }

    @Test
    fun `createLibrary will add a library successfully`() = runBlocking {
        // given
        val preSavedLibrary = PRE_SAVED_LIBRARY.copy().also {
            it.book = TestVariables.MAIN_BOOK
            it.user = TestVariables.MAIN_USER
        }

        //when
        val actual: Library = libraryService.createLibrary(preSavedLibrary)

        println(actual)

        //then
        assertEquals(MAIN_LIBRARY, actual)
    }

    @Test
    fun `getLibraryById returns null when the user does not exist`() = runBlocking {
        //when
        val found: Library? = libraryService.getLibraryById(1)

        //then
        assertEquals(null, found)
    }

    @Test
    fun `getLibraryById gets the library when it exists`() = runBlocking {
        //setup
        val createdId = libraryService.createLibrary(PRE_SAVED_LIBRARY).id!!

        println("CREATED: $createdId")

        //when
        val found: Library? = libraryService.getLibraryById(createdId)

        //then
        assertEquals(MAIN_LIBRARY.copy(id = createdId), found!!)
    }

    @Test
    fun `getLibraryByUserId gets the user library`() = runBlocking {
        //setup
        val library: Library = libraryService.createLibrary(PRE_SAVED_LIBRARY)

        println("CREATED: $library")

        //when
        val found: Library? = libraryService.getLibraryByUserId(1)

        //then
        assertEquals(library, found)
    }

    private fun <T> assertEquals(expected: T, actual: T) {
        assert(expected == actual) {
            """
               | EXPECT: $expected
               | ACTUAL: $actual
            """.trimMargin()
        }
    }
}