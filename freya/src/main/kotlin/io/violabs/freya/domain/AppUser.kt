package io.violabs.freya.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate


@Table("app_user")
data class AppUser(
    @Id val id: Long? = null,
    val username: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val email: String? = null,
    val dateOfBirth: LocalDate? = null,
    val joinDate: LocalDate? = null
)