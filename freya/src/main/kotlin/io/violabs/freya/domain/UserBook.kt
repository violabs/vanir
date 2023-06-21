package io.violabs.freya.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_book")
data class UserBook(
    @Id val id: Long? = null,
    val userId: Long? = null,
    val bookId: Long? = null,
    val addedOn: Instant? = null
)