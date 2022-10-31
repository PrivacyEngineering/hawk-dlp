package io.hawk.dlp.integration.google.cdlp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScans

@SpringBootApplication
@ComponentScan("io.hawk.dlp.integration")
class GoogleApplication

fun main(args: Array<String>) {
    runApplication<GoogleApplication>(*args)
}
