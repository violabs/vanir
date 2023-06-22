package io.violabs.freya.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
open class AppConfig {
    @Bean
    open fun clock(): Clock = Clock.systemDefaultZone()
}