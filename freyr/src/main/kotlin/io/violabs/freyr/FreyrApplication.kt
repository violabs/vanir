package io.violabs.freyr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class FreyrApplication

fun main(args: Array<String>) {
    runApplication<FreyrApplication>(*args)
}