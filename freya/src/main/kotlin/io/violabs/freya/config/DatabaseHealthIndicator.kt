package io.violabs.freya.config

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class DatabaseHealthIndicator(private val databaseClient: DatabaseClient) : ReactiveHealthIndicator {
    private val upState = Health.up().build()

    override fun health(): Mono<Health> {
        return try {
            checkHealth()
        } catch (ex: Exception) {
            Health
                .down(ex)
                .build()
                .toMono()
        }
    }

    private fun checkHealth(): Mono<Health> =
        databaseClient
            .sql("SELECT 1")
            .fetch()
            .rowsUpdated()
            .map { upState }
            .onErrorResume { throwable ->
                val ex = throwable as Exception

                Health.down(ex).build().toMono()
            }
}