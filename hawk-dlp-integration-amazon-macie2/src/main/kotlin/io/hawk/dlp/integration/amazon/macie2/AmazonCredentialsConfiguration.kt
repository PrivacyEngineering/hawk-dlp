package io.hawk.dlp.integration.amazon.macie2

import com.amazonaws.services.macie2.AmazonMacie2
import com.amazonaws.services.macie2.AmazonMacie2Client
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class AmazonCredentialsConfiguration {

    @Bean
    fun macie2Client(): AmazonMacie2 = AmazonMacie2Client.builder()
        .build()
}