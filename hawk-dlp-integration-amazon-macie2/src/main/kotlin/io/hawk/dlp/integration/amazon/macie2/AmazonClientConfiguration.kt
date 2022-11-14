package io.hawk.dlp.integration.amazon.macie2

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.services.macie2.AmazonMacie2
import com.amazonaws.services.macie2.AmazonMacie2Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class AmazonClientConfiguration {
    @Value("\${hawk.amazon.macie2.region}")
    lateinit var region: String

    @Bean
    fun macie2Client(): AmazonMacie2 = AmazonMacie2Client.builder()
        .withRegion(region)
        .build()
}