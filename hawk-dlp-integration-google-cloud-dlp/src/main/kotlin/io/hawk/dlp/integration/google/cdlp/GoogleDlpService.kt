package io.hawk.dlp.integration.google.cdlp

import com.google.cloud.dlp.v2.DlpServiceClient
import com.google.privacy.dlp.v2.*
import com.google.privacy.dlp.v2.Table.Row
import io.hawk.dlp.common.*
import io.hawk.dlp.common.Finding
import io.hawk.dlp.common.InfoType
import io.hawk.dlp.common.InspectResult
import io.hawk.dlp.integration.*
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class GoogleDlpService(
    private val dlpClient: DlpServiceClient
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
            "Only the job types are supported for now: analyze + reference or inspect + table"

        if (request.resultTypes.size != 1) error(jobTypeError)

        if (request.content is TableDirectContent && ResultType.INSPECT in request.resultTypes) {
            inspectContent(job, request.content as TableDirectContent)
        } else if (request.content is ReferenceContent && ResultType.ANALYZE in request.resultTypes) {

        } else {
            error(jobTypeError)
        }
    }

    // TODO: create extract class for this
    private fun inspectContent(job: Job, tableContent: TableDirectContent) {
        val request = InspectContentRequest
            .newBuilder()
            .apply {
                item = ContentItem.newBuilder().apply {
                    table = Table.newBuilder().apply {
                        addAllHeaders(tableContent.headers.map {
                            FieldId.newBuilder().setName(it).build()
                        })
                        addAllRows(tableContent.rows.map { row ->
                            Row.newBuilder().addAllValues(row.map { value ->
                                // TODO: Handle other types than string
                                Value.newBuilder().setStringValue(value.toString()).build()
                            }).build()
                        })
                    }.build()
                }.build()
            }
            .build()
        // Synchronously execute job
        // We won't be able to do asynchronous (hybrid in google terms) jobs at the beginning,
        // because the results get saved in a big query table, and we would have to query it.
        val response = dlpClient.inspectContent(request)

        // First iteration on how to collect all useful data from Google DLP result.
        val findings = response.result.findingsList.map { finding ->
            // TODO: convert info type to hawk generalized info type
            val likelihood = (finding.likelihoodValue / 5.0).toFloat()
            val occurrences = finding.location.contentLocationsList.mapNotNull { location ->
                if (location.hasRecordLocation()) {
                    ColumnContainerOccurrence(
                        location.containerName, // {project_id}:{dataset_id}.{table_id}
                        location.recordLocation.fieldId.name
                    )
                } else if (location.hasDocumentLocation()) {
                    ContainerOccurrence(
                        location.containerName
                    )
                }
                // TODO: add more occurrences and expand this
                null
            }
            Finding(
                UUID.randomUUID(),
                resolveInfoType(finding.infoType.name),
                if (likelihood < 0) 0f else likelihood,
                occurrences
            )
        }
        job.results = listOf(InspectResult(UUID.randomUUID(), LocalDateTime.now(), findings))
        job.status = JobStatus.COMPLETED
    }

    private fun createAnalyzeDlpJob() {
        TODO("Not yet implemented")
    }

    private fun resolveInfoType(infoType: String): InfoType {
        TODO("Not yet implemented")
    }
}