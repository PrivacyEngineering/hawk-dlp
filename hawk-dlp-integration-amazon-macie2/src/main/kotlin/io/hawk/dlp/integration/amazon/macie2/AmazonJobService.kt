package io.hawk.dlp.integration.amazon.macie2

import io.hawk.dlp.common.*
import io.hawk.dlp.integration.*
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class AmazonJobService(
    private val inspectJobService: AmazonInspectJobService
) {
    @EventListener
    fun jobCreated(event: JobCreatedEvent) {
        event.job.begin()
        try {
            handleJobCreated(event.job)
        } catch (throwable: Throwable) {
            event.job.failed(throwable, "Amazon Job Failed")
        }
    }

    private fun handleJobCreated(job: Job) {
        val request = job.request as? DirectJobRequest
            ?: error("Only direct job requests are supported")

        val jobTypeError =
            "Only the following job types are supported for now: inspect + file reference"

        if (request.goals.size != 1) error(jobTypeError)

        if (request.content is FileReferenceContent &&
            request.goals.first() is InspectGoal
        ) {
            inspectJobService.executeJob(job, request.content as FileReferenceContent)
        } else {
            error(jobTypeError)
        }
    }
}