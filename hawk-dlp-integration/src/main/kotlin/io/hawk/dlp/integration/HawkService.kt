package io.hawk.dlp.integration

import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
@Profile("hawk")
class HawkService {

    @Order(-1)
    @EventListener
    fun jobCreated(event: JobCreatedEvent) {
        event.job.addListener {

        }
    }

    private fun notifyHawkService() {

    }
}