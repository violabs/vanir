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
    private val sql = "SELECT 1"

    override fun health(): Mono<Health> = try {
        checkHealth()
    } catch (ex: Exception) {
        buildHealthDown(ex)
    }

    private fun checkHealth(): Mono<Health> =
        databaseClient
            .sql(sql)
            .fetch()
            .rowsUpdated()
            .map { upState }
            .onErrorResume { throwable -> buildHealthDown(throwable as Exception) }

    private fun buildHealthDown(ex: Exception): Mono<Health> =
        Health
            .down(ex)
            .build()
            .toMono()
}