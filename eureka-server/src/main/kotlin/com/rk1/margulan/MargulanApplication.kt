package com.rk1.margulan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@EnableEurekaServer
@SpringBootApplication
class MargulanApplication

fun main(args: Array<String>) {
	runApplication<MargulanApplication>(*args)
}
