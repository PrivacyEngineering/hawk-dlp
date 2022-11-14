package io.hawk.dlp.integration.google.cdlp2

import com.google.privacy.dlp.v2.*
import io.hawk.dlp.common.*
import io.hawk.dlp.integration.*
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.util.*

@Service
class GoogleJobService(
    private val inspectJobService: GoogleInspectJobService,
    private val analyzeJobService: GoogleAnalyzeJobService
) {

    @EventListener
    fun jobCreated(event: JobCreatedEvent) {
        event.job.begin()
        try {
            handleJobCreated(event.job)
        } catch (throwable: Throwable) {
            event.job.failed(throwable, "Google Job failed")
        }
    }

    private fun handleJobCreated(job: Job) {
        val request = job.request as? DirectJobRequest
            ?: error("Only direct job requests are supported")

        val jobTypeError =
            "Only the following job types are supported for now: analyze + reference or inspect + table"

        if (request.goals.size != 1) error(jobTypeError)

        if (request.content is TableDirectContent &&
            request.goals.first() is InspectGoal
        ) {
            inspectJobService.executeJob(job, request.content as TableDirectContent)
        } else if (request.content is ReferenceContent &&
            request.goals.first() is AnalyzeGoal
        ) {
            analyzeJobService.executeJob(job, request.content as ReferenceContent)
        } else {
            error(jobTypeError)
        }
    }
}