package endterm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class EndtermApplication

fun main(args: Array<String>) {
	runApplication<EndtermApplication>(*args)
}
