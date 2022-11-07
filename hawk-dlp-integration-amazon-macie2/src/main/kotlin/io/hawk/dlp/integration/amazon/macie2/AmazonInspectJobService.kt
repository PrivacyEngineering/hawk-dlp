package io.hawk.dlp.integration.amazon.macie2

import com.amazonaws.services.macie2.AmazonMacie2
import com.amazonaws.services.macie2.model.*
import io.hawk.dlp.common.Finding
import io.hawk.dlp.common.InspectResult
import io.hawk.dlp.integration.FileReferenceContent
import io.hawk.dlp.integration.InspectResultFormat
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

        localMacieJobs.forEach { (macieJobId, job) ->
            try {
                checkMacieJob(job, macieJobId)
            } catch (throwable: Throwable) {
                job.error = throwable.message
                job.status = JobStatus.FAILED
            }
        }
    }

    private fun checkMacieJob(job: Job, macieJobId: String) {
        val status = getMacieJobStatus(macieJobId)
        val cancelled = status == MacieJobStatus.CANCELLED.name
        val complete = status == MacieJobStatus.COMPLETE.name

        if (cancelled || complete) macieJobsLock.withLock {
            macieJobs.remove(macieJobId)
        }
        if (cancelled) processCancelledMacieJob(job)
        if (complete) processCompleteMacieJob(job, macieJobId)
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
        val inspectResult = getFindings(listFindingsIds(macieJobId))

        job.results = job.request.resultFormats
            .mapNotNull { it as? InspectResultFormat }
            .map { it.copy(id = UUID.randomUUID()) }
            .associateWith { inspectResult.copy(id = it.id!!) }
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

    private fun getFindings(findingsIds: List<String>): InspectResult {
        val response = macie2Client.getFindings(GetFindingsRequest().withFindingIds(findingsIds))
        var additionalOccurrences = false
        val findings = response.findings.flatMap {
            val resourcesAffected = it.resourcesAffected
            val severity = it.severity.score
            if (it.classificationDetails.result.additionalOccurrences)
                additionalOccurrences = true

            it.classificationDetails.result.sensitiveData.flatMap { data ->
                data.detections.map { detection ->
                    convertFinding(
                        resourcesAffected,
                        severity,
                        detection
                    )
                }
            }
        }.filter { it.occurrences.isNotEmpty() }

        return InspectResult(
            UUID.randomUUID(),
            LocalDateTime.now(),
            findings,
            additionalOccurrences
        )
    }

    private fun convertFinding(
        resourcesAffected: ResourcesAffected,
        severity: Long,
        detection: DefaultDetection
    ) = Finding(
        UUID.randomUUID(),
        infoTypeService.translateAmazonInfoType(detection.type),
        null,
        detection.occurrences
            .cells
            .map { AmazonColumnContainerOccurrence(resourcesAffected, it) }
            .distinctBy { it.container + it.column },
        mapOf("severity" to severity)
    )
}