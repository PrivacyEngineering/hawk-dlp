package io.hawk.dlp.integration.google.cdlp2

import com.google.cloud.dlp.v2.DlpServiceClient
import com.google.privacy.dlp.v2.ContentLocation
import io.hawk.dlp.common.*
import io.hawk.dlp.integration.Job
import io.hawk.dlp.integration.JobStatus
import io.hawk.dlp.integration.TableDirectContent
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.math.max

@Service
class GoogleInspectJobService(
    private val dlpClient: DlpServiceClient,
    private val infoTypeService: InfoTypeService
) {
    fun executeJob(job: Job, tableContent: TableDirectContent) {
        // Synchronously execute job
        // We won't be able to do asynchronous (hybrid in google terms) jobs at the beginning,
        // because the results get saved in a big query table, and we would have to query it.
        val response = dlpClient.inspectContent(
            buildInspectRequest(tableContent)
        )

        val findings = response.result.findingsList.map { finding ->
            Finding(
                UUID.randomUUID(),
                infoTypeService.translateGoogleInfoType(finding.infoType.name),
                convertLikelihood(finding.likelihoodValue),
                finding.location.contentLocationsList.mapNotNull(::convertOccurrence)
            )
        }

        job.results = listOf(InspectResult(UUID.randomUUID(), LocalDateTime.now(), findings))
        job.status = JobStatus.COMPLETED
    }

    private fun buildInspectRequest(content: TableDirectContent) = inspectContentRequest {
        contentItem {
            table {
                content.headers.forEach {
                    header {
                        name = it
                    }
                }
                content.rows.forEach {
                    row {
                        it.forEach { value ->
                            cell {
                                setValue(value)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun convertLikelihood(likelihood: Int) = max(0.0f, (likelihood / 5.0).toFloat())

    private fun convertOccurrence(location: ContentLocation): Occurrence? {
        return if (location.hasRecordLocation()) {
            ColumnContainerOccurrence(
                location.containerName, // {project_id}:{dataset_id}.{table_id}
                location.recordLocation.fieldId.name
            )
        } else if (location.hasDocumentLocation()) {
            ContainerOccurrence(
                location.containerName
            )
        } else {
            // TODO: add more occurrences and expand this
            null
        }
    }
}