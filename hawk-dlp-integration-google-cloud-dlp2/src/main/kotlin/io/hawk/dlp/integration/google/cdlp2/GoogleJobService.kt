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
            "Only the following job types are supported for now: analyze + reference or inspect + table"

        if (request.resultFormats.size != 1) error(jobTypeError)

        if (request.content is TableDirectContent &&
            request.resultFormats.first() is InspectResultFormat
        ) {
            inspectJobService.executeJob(job, request.content as TableDirectContent)
        } else if (request.content is ReferenceContent &&
            request.resultFormats.first() is AnalyzeResultFormat
        ) {
            analyzeJobService.executeJob(job, request.content as ReferenceContent)
        } else {
            error(jobTypeError)
        }
    }
}