package io.violabs.freya.service

import io.violabs.freya.domain.Library
import io.violabs.freya.domain.PostgresLibrary
import io.violabs.freya.repository.LibraryRepository
import org.springframework.stereotype.Service

@Service
class LibraryService(private val libraryRepository: LibraryRepository) {
    suspend fun createLibrary(library: Library): Library {
        return library
            .toDbEntity()
            .let { libraryRepository.save(it) }
            .let(PostgresLibrary::toDto)
    }

    suspend fun getLibraryById(id: Long): Library? {
        return libraryRepository
            .findById(id)
            ?.toDto()
    }
}