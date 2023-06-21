package io.violabs.freya.repository

import io.violabs.freya.domain.UserBook
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserBookRepository : CoroutineCrudRepository<UserBook, Long> {

    @Query("SELECT book_id FROM user_book WHERE user_id = :userId")
    fun getBookIdsByUserId(userId: Long): Flow<Long>
}