package io.violabs.freya.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

interface ILibrary {
    val id: Long?
    val userId: Long?
    val bookId: Long?
    val addedOn: Instant?
}

@Table("library")
data class PostgresLibrary(
    @Id override val id: Long? = null,
    override val userId: Long? = null,
    override val bookId: Long? = null,
    override val addedOn: Instant? = null
) : ILibrary {
    fun toDto(): Library {
        return Library(
            id = id,
            userId = userId,
            bookId = bookId,
            addedOn = addedOn
        )
    }
}

data class Library(
    override val id: Long? = null,
    override val userId: Long? = null,
    override val bookId: Long? = null,
    override val addedOn: Instant? = null
) : ILibrary {
    var book: Book? = null
    var user: AppUser? = null

    fun toDbEntity(): PostgresLibrary {
        return PostgresLibrary(
            id = id,
            userId = userId,
            bookId = bookId,
            addedOn = addedOn
        )
    }
}