package io.violabs.freya.domain

data class Library(
    var user: AppUser? = null,
    var books: List<Book>? = null
)