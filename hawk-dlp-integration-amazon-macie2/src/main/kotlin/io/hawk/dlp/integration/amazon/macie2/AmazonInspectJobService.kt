package io.hawk.dlp.integration.amazon.macie2

import com.amazonaws.services.macie2.AmazonMacie2
import com.amazonaws.services.macie2.model.*
import io.hawk.dlp.common.Finding
import io.hawk.dlp.common.InspectResult
import io.hawk.dlp.common.FileReferenceContent
import io.hawk.dlp.common.InspectGoal
import io.hawk.dlp.common.Job
import io.hawk.dlp.common.JobStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import com.amazonaws.services.macie2.model.JobStatus as MacieJobStatus

@Service
class AmazonInspectJobService(
    private val macie2Client: AmazonMacie2,
    private val infoTypeService: InfoTypeService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val macieJobsLock = ReentrantLock()
    private val macieJobs = ConcurrentHashMap<String, Job>()
    private val bucketFilePathRegex = Regex("^s3://([A-Za-z-0-9.,_]+).*$")
    @Value("\${hawk.amazon.macie2.account-id}")
    lateinit var accountId: String

    fun executeJob(job: Job, reference: FileReferenceContent) {
        val macieJobId = createMacieJob(job, reference)
        macieJobsLock.withLock {
            macieJobs[macieJobId] = job
        }
    }

    private fun createMacieJob(job: Job, reference: FileReferenceContent): String {
        logger.debug("Job[{}] Executing Amazon Inspect Job", job.id)
        // TODO: parse s3 job definition so, that path file and file path gets also included
        val bucketNames = reference.files.mapNotNull {
            bucketFilePathRegex.matchEntire(it)?.groupValues?.getOrNull(1)
        }
        val request = CreateClassificationJobRequest()
            .withName("Hawk ${job.id}")
            .withJobType(JobType.ONE_TIME)
            .withS3JobDefinition(
                S3JobDefinition()
                    .withBucketDefinitions(
                        S3BucketDefinitionForJob()
                            .withAccountId(accountId)
                            .withBuckets(bucketNames)
                    )
            )
        logger.trace("Job[{}] ClassificationJobRequest {}", job.id, request)
        val response = macie2Client.createClassificationJob(request)
        logger.trace("Job[{}] ClassificationJobResponse {}", job.id, response)
        return response.jobId
    }

    @Scheduled(initialDelay = 10_000L, fixedDelay = 10_000L)
    fun checkMacieJobs() {
        // Copy jobs map to avoid concurrent modification
        val localMacieJobs = macieJobsLock.withLock {
            if (macieJobs.isEmpty()) return
            macieJobs.toMap()
        }

        logger.debug("Check Job Status in Macie for {}", localMacieJobs.values.map { it.id })

        localMacieJobs.forEach { (macieJobId, job) ->
            try {
                logger.trace("Job[{}] Check Macie Job status", job.id)
                checkMacieJob(job, macieJobId)
            } catch (throwable: Throwable) {
                job.failed(throwable, "Failed checking job")
                macieJobsLock.withLock {
                    macieJobs.remove(macieJobId)
                }
            }
        }
    }

    private fun checkMacieJob(job: Job, macieJobId: String) {
        val status = getMacieJobStatus(macieJobId)
        logger.trace("Job[{}] Macie Job status is {}", job.id, status)
        val cancelled = status == MacieJobStatus.CANCELLED.name
        val complete = status == MacieJobStatus.COMPLETE.name

        if (cancelled || complete) macieJobsLock.withLock {
            macieJobs.remove(macieJobId)
        }
        if (cancelled) job.failed("Macie Job cancelled")
        if (complete) processCompleteMacieJob(job, macieJobId)
    }

    private fun getMacieJobStatus(macieJobId: String) = macie2Client
        .describeClassificationJob(
            DescribeClassificationJobRequest()
                .withJobId(macieJobId)
        )
        .jobStatus

    private fun processCompleteMacieJob(job: Job, macieJobId: String) {
        val inspectResult = getFindings(job, listFindingsIds(job, macieJobId))

        job.completed(
            job.request!!.goals
                .mapNotNull { it as? InspectGoal }
                .map { it.copy(id = UUID.randomUUID()) }
                .associateWith { inspectResult.copy(id = it.id!!) }
        )
    }

    private fun listFindingsIds(job: Job, macieJobId: String): List<String> {
        val request = ListFindingsRequest()
            .withFindingCriteria(
                FindingCriteria()
                    .addCriterionEntry(
                        "classificationDetails.jobId",
                        CriterionAdditionalProperties().withEq(macieJobId)
                    )
            )
        logger.trace("Job[{}] Send Findings query for Macie Job {}", job.id, request)
        val response = macie2Client.listFindings(request)
        logger.trace("Job[{}] Got Findings query response for Macie Job {}", job.id, response)
        return response.findingIds
    }

    private fun getFindings(job: Job, findingsIds: List<String>): InspectResult {
        val response = macie2Client.getFindings(GetFindingsRequest().withFindingIds(findingsIds))
        logger.trace("Job[{}] GetFindings response {}", job.id, response)
        var additionalOccurrences = false
        // TODO
        // do we need to add another groupBy layer to make findings grouped by info type and occurrences distinct
        // by container + column name.
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