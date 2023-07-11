package io.violabs.freyr.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
open class AppConfig {

    @Bean
    open fun uuidGenerator(): UuidGenerator = UuidGenerator()
}

class UuidGenerator {
    fun generate(seed: Any? = null): UUID =
        seed
            ?.toString()
            ?.toByteArray()
            ?.let(UUID::nameUUIDFromBytes)
            ?: UUID.randomUUID()

    fun generateString(seed: Any? = null): String = generate(seed).toString()
}