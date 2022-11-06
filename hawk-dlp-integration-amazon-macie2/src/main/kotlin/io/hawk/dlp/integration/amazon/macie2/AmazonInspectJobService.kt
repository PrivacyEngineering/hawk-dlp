package io.hawk.dlp.integration.amazon.macie2

import com.amazonaws.services.macie2.AmazonMacie2
import com.amazonaws.services.macie2.model.*
import io.hawk.dlp.common.ColumnContainerOccurrence
import io.hawk.dlp.common.Finding
import io.hawk.dlp.common.InspectResult
import io.hawk.dlp.integration.FileReferenceContent
import io.hawk.dlp.integration.Job
import io.hawk.dlp.integration.JobStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import com.amazonaws.services.macie2.model.JobStatus as MacieJobStatus

@Service
class AmazonInspectJobService(
    private val macie2Client: AmazonMacie2,
    private val infoTypeService: InfoTypeService
) {
    private val macieJobsLock = ReentrantLock()
    private val macieJobs = HashMap<String, Job>()

    fun executeJob(job: Job, reference: FileReferenceContent) {
        val macieJobId = createMacieJob(reference)
        macieJobsLock.withLock {
            macieJobs[macieJobId] = job
        }
    }

    private fun createMacieJob(reference: FileReferenceContent): String {
        val request = CreateClassificationJobRequest()
            .withJobType(JobType.ONE_TIME)
            .withS3JobDefinition(
                S3JobDefinition()
                    .withBucketDefinitions(
                        S3BucketDefinitionForJob()
                            .withBuckets(reference.files)
                    )
            )
        return macie2Client.createClassificationJob(request).jobId
    }

    @Scheduled(initialDelay = 2_000L, fixedDelay = 2_000L)
    fun checkMacieJobs() {
        // Copy jobs map to avoid concurrent modification
        val localMacieJobs = macieJobsLock.withLock {
            if (macieJobs.isEmpty()) return
            macieJobs.toMap()
        }

        localMacieJobs.forEach {
            val status = getMacieJobStatus(it.key)
            val cancelled = status == MacieJobStatus.CANCELLED.name
            val complete = status == MacieJobStatus.COMPLETE.name

            if (cancelled || complete) macieJobsLock.withLock {
                macieJobs.remove(it.key)
            }
            if (cancelled) processCancelledMacieJob(it.value)
            if (complete) processCompleteMacieJob(it.value, it.key)
        }
    }

    private fun getMacieJobStatus(macieJobId: String) = macie2Client
        .describeClassificationJob(
            DescribeClassificationJobRequest()
                .withJobId(macieJobId)
        )
        .jobStatus

    private fun processCancelledMacieJob(job: Job) {
        job.error = "Macie Job cancelled"
        job.status = JobStatus.FAILED
    }

    private fun processCompleteMacieJob(job: Job, macieJobId: String) {
        job.results = listOf(
            InspectResult(
                UUID.randomUUID(),
                LocalDateTime.now(),
                getFindings(listFindingsIds(macieJobId))
            )
        )
        job.status = JobStatus.COMPLETED
    }

    private fun listFindingsIds(macieJobId: String): List<String> {
        val request = ListFindingsRequest()
            .withFindingCriteria(
                FindingCriteria()
                    .addCriterionEntry(
                        "classificationDetails.jobId",
                        CriterionAdditionalProperties().withEq(macieJobId)
                    )
            )
        return macie2Client.listFindings(request).findingIds
    }

    private fun getFindings(findingsIds: List<String>): List<Finding> {
        val request = GetFindingsRequest()
            .withFindingIds(findingsIds)
        return macie2Client.getFindings(request).findings.flatMap {
            val containerName = it.classificationDetails.detailedResultsLocation
            val severity = it.severity.score

            it.classificationDetails.result.sensitiveData.flatMap { data ->
                data.detections.map { detection ->
                    convertFinding(
                        containerName,
                        severity,
                        detection
                    )
                }
            }
        }
    }

    private fun convertFinding(
        containerName: String,
        severity: Long,
        detection: DefaultDetection
    ) = Finding(
        UUID.randomUUID(),
        infoTypeService.translateAmazonInfoType(detection.type),
        null,
        detection.occurrences.cells.map {
            ColumnContainerOccurrence(
                containerName,
                it.columnName
            )
        },
        mapOf("severity" to severity)
    )
}