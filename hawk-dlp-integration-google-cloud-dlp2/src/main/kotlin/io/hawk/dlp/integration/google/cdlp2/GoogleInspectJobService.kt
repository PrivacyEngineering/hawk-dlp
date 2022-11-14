package io.hawk.dlp.integration.google.cdlp2

import com.google.cloud.dlp.v2.DlpServiceClient
import com.google.privacy.dlp.v2.ContentLocation
import com.google.privacy.dlp.v2.LocationName
import io.hawk.dlp.common.*
import io.hawk.dlp.common.InspectGoal
import io.hawk.dlp.common.Job
import io.hawk.dlp.common.TableDirectContent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.math.max

@Service
class GoogleInspectJobService(
    private val dlpClient: DlpServiceClient,
    private val infoTypeService: InfoTypeService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${hawk.google.cdlp2.project}")
    private lateinit var project: String

    @Value("\${hawk.google.cdlp2.location}")
    private lateinit var location: String

    fun executeJob(job: Job, tableContent: TableDirectContent) {
        logger.debug("Job[{}] Executing Google Inspect Job", job.id)
        val request = buildInspectRequest(tableContent)
        logger.trace("Job[{}] Got inspect request {}", job.id, request)

        // Synchronously execute job
        // We won't be able to do asynchronous (hybrid in google terms) jobs at the beginning,
        // because the results get saved in a big query table, and we would have to query it.
        val response = dlpClient.inspectContent(request)
        logger.trace("Job[{}] Got inspect response {}", job.id, response)

        val columnFindings = response.result
            .findingsList
            .map { finding ->
                Finding(
                    UUID.randomUUID(),
                    infoTypeService.translateGoogleInfoType(finding.infoType.name),
                    convertLikelihood(finding.likelihoodValue),
                    finding.location
                        .contentLocationsList
                        .mapNotNull(::convertOccurrence)
                )
            }
            .groupBy { it.infoType }
            .map { (_, findings) ->
                if (findings.size == 1) findings.first()
                else Finding(
                    UUID.randomUUID(),
                    findings.first().infoType,
                    findings.sumOf { it.likelihood ?: 0.0 } / findings.size,
                    findings
                        .flatMap { it.occurrences }
                        .map { it as ColumnContainerOccurrence }
                        .distinctBy { it.container + it.column }
                )
            }
            .filter { it.occurrences.isNotEmpty() }

        job.completed(
            job.request.goals
                .mapNotNull { it as? InspectGoal }
                .map { it.copy(id = UUID.randomUUID()) }
                .associateWith {
                    InspectResult(
                        it.id!!,
                        LocalDateTime.now(),
                        columnFindings,
                        response.result.findingsTruncated
                    )
                }
        )
    }

    private fun buildInspectRequest(content: TableDirectContent) = inspectContentRequest {
        parent = LocationName.of(project, location).toString()
        contentItem {
            table {
                content.headers.forEach {
                    header {
                        name = it
                    }
                }
                content.cells.forEach {
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

    private fun convertLikelihood(likelihood: Int) = max(0.0, (likelihood / 5.0))

    private fun convertOccurrence(location: ContentLocation): ColumnContainerOccurrence? {
        return if (location.hasRecordLocation()) {
            GoogleColumnContainerOccurrence(location)
        } else {
            // TODO: add more occurrences and expand this
            null
        }
    }
}