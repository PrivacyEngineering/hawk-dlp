package io.hawk.dlp.integration

import io.hawk.dlp.common.Job
import io.hawk.dlp.common.JobRequest
import io.hawk.dlp.common.JobStatus
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(javaClass)

    // TODO: add cron job to clear old jobs, to prevent memory leak
    private val jobs: MutableMap<UUID, Job> = ConcurrentHashMap()

    fun createJob(request: JobRequest): Job {
        val job = Job()
        job.request = request
        logger.info("Job[{}] created: {} {}", job.id, request.content.javaClass, request.goals)
        jobs[job.id] = job

        CompletableFuture.runAsync {
            try {
                if (!request.content.valid()) {
                    job.failed("Content validation failed")
                    return@runAsync
                }

                eventPublisher.publishEvent(JobCreatedEvent(job))
            } catch (throwable: Throwable) {
                job.failed(throwable, "Unexpected error")
            }
        }
        return job
    }

    fun listJobs() = jobs.values.toList()

    fun getJob(id: UUID) = jobs[id]
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found")
}