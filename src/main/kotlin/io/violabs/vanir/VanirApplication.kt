package io.violabs.vanir

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VanirApplication

fun main(args: Array<String>) {
	runApplication<VanirApplication>(*args)
}
