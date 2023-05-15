package io.violabs.freya.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("user")
data class User(
    @Id @Column("id")
    val username: String? = null,
    @Column
    val firstname: String? = null,

    @Column
    val lastname: String? = null,

    @Column
    val email: String? = null,

    @Column
    val dateOfBirth: LocalDate? = null,

    @Column
    val joinDate: LocalDate? = null
)