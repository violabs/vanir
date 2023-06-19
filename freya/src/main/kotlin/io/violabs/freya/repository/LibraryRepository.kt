package io.violabs.freya.repository

import io.violabs.freya.domain.PostgresLibrary
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LibraryRepository : CoroutineCrudRepository<PostgresLibrary, Long> {

    @Query("SELECT * FROM library WHERE user_id = :userId")
    suspend fun findByUserId(userId: Long): PostgresLibrary? // can't suppress this warning
}