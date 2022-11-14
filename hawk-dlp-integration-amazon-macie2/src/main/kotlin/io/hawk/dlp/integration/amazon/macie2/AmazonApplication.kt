package io.hawk.dlp.integration.amazon.macie2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
@ComponentScan("io.hawk.dlp.integration")
class AmazonApplication

fun main(args: Array<String>) {
    runApplication<AmazonApplication>(*args)
}
