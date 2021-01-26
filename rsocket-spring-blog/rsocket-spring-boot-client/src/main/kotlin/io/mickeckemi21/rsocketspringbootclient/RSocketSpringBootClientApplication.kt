package io.mickeckemi21.rsocketspringbootclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RSocketSpringBootClientApplication

fun main(args: Array<String>) {
	runApplication<RSocketSpringBootClientApplication>(*args)
}
