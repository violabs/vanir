package io.violabs.freya.repository

import io.violabs.freya.domain.PostgresLibrary
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LibraryRepository : CoroutineCrudRepository<PostgresLibrary, Long>