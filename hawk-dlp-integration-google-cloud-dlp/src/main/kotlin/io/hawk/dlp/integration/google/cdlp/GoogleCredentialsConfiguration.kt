package io.hawk.dlp.integration.google.cdlp

import com.google.cloud.dlp.v2.DlpServiceClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GoogleCredentialsConfiguration {

    @Bean
    // TODO: provide credentials
    fun dlpClient(): DlpServiceClient = DlpServiceClient.create()
}