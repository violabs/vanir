package io.violabs.freya.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("book")
data class Book(
    @Id val id: Long? = null,
    val title: String? = null,
    val author: String? = null
)