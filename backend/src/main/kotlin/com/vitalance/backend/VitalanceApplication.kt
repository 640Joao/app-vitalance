package com.vitalance.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VitalanceApplication

fun main(args: Array<String>) {
	runApplication<VitalanceApplication>(*args)
}
