package io.violabs.freyr.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
open class JacksonConfig {

    @Bean
    open fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
        return builder
            .createXmlMapper(false)
            .modulesToInstall(JavaTimeModule())
            .build<ObjectMapper>()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerKotlinModule()
    }
}