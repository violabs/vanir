package io.violabs.freya.repository

import io.violabs.freya.domain.AppUser
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<AppUser, Long>