package io.hawk.dlp.integration

import io.hawk.dlp.common.Job
import io.hawk.dlp.common.InspectResult
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.scheduler.Schedulers

@Service
@Profile("hawk")
@ConditionalOnProperty("hawk.service.url")
class HawkService {
    @Value("\${hawk.service.url}")
    lateinit var hawkServiceUrl: String

    private val client by lazy {
        // TODO: add authentication
        WebClient.create(hawkServiceUrl)
    }
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun onStart() {
        logger.info("Hawk service integration enabled using URL $hawkServiceUrl")
    }

    @Order(-1)
    @EventListener
    fun jobCreated(event: JobCreatedEvent) {
        event.job.addListener {
            notifyHawkService(it)
        }
    }

    private fun notifyHawkService(job: Job) {
        client.post()
            .uri("/api/dlp")
            .bodyValue(job)
            .retrieve()
            .bodyToMono(Void::class.java)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
        job.results?.values?.forEach {
            if (it is InspectResult) {
                client.post()
                    .uri("/api/dlp/${job.id}/result/inspect")
                    .bodyValue(it)
                    .retrieve()
                    .bodyToMono(Void::class.java)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe()
            }
        }
    }
}
