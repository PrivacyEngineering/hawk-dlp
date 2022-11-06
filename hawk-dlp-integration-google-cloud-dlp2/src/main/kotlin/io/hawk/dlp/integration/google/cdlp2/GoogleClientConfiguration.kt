package io.hawk.dlp.integration.google.cdlp2

import com.google.cloud.dlp.v2.DlpServiceClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GoogleClientConfiguration {

    @Bean
    fun dlpClient(): DlpServiceClient = DlpServiceClient.create()
}