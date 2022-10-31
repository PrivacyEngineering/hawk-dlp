package io.hawk.dlp.integration

import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@Service
class JobService(
    private val eventPublisher: ApplicationEventPublisher
) {
    // TODO: add cron job to clear old jobs, to prevent memory leak
    private val jobs: MutableMap<UUID, Job> = ConcurrentHashMap()

    fun createJob(request: JobRequest): Job {
        val job = Job(request = request)
        jobs[job.id] = job
        CompletableFuture.runAsync {
            eventPublisher.publishEvent(JobCreatedEvent(job))
        }
        return job
    }

    fun getJob(id: UUID) = jobs[id]
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found")
}