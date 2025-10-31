package com.vitalance.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class VitalanceApplication

fun main(args: Array<String>) {
	runApplication<VitalanceApplication>(*args)
}