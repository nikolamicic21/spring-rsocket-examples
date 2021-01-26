package io.mickeckemi21.rsocketspringbootserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RSocketSpringBootServerApplication

fun main(args: Array<String>) {
	runApplication<RSocketSpringBootServerApplication>(*args)
}
