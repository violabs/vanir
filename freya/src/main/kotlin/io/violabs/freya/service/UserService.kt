package io.violabs.freya.service

import io.violabs.freya.domain.AppUser
import io.violabs.freya.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    suspend fun createUser(user: AppUser): AppUser = userRepository.save(user)

    suspend fun updateUser(user: AppUser): AppUser = userRepository.save(user)

    suspend fun getUserById(id: Long): AppUser? = userRepository.findById(id)

    fun getAllUsers(): Flow<AppUser> = userRepository.findAll()

    suspend fun deleteUserById(id: Long): Boolean {
        return userRepository.deleteById(id).let { !userRepository.existsById(id) }
    }
}