package io.hawk.dlp.integration.amazon.macie2

import io.hawk.dlp.integration.*
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class AmazonJobService(
    private val inspectJobService: AmazonInspectJobService
) {
    @EventListener(JobCreatedEvent::class)
    fun jobCreated(job: Job) {
        job.status = JobStatus.IN_PROGRESS
        try {
            handleJobCreated(job)
        } catch (throwable: Throwable) {
            job.status = JobStatus.FAILED
            job.error = throwable.message
        }
    }

    private fun handleJobCreated(job: Job) {
        val request = job.request as? DirectJobRequest
            ?: error("Only direct job requests are supported")

        val jobTypeError =
            "Only the following job types are supported for now: inspect + file reference"

        if (request.resultTypes.size != 1) error(jobTypeError)

        if (request.content is FileReferenceContent && ResultType.INSPECT in request.resultTypes) {
            inspectJobService.executeJob(job, request.content as FileReferenceContent)
        } else {
            error(jobTypeError)
        }
    }
}