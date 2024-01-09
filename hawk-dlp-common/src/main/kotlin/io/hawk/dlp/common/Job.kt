package io.hawk.dlp.common

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a job of a data loss prevention implementation.
 * E.g. a job to create a risk score for a given data set or a job to identify info types.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Job(
    /**
     * The unique identifier of this job.
     */
    val id: UUID = UUID.randomUUID(),
    /**
     * Time when this job was created.
     */
    val created: LocalDateTime = LocalDateTime.now()
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * The request that was used to create this job.
     * Encapsulates the input data.
     */
    @field:JsonIgnore
    var request: JobRequest? = null

    /**
     * The status of this job.
     */
    var status: JobStatus = JobStatus.CREATED
        private set

    /**
     * The list result of results for this job.
     * Present when [status] is [JobStatus.COMPLETED].
     */
    @field:JsonIgnore
    var results: Map<Goal, Result>? = null
        private set

    /**
     * The error of this job.
     * Present when [status] is [JobStatus.FAILED].
     */
    var error: String? = null
        private set

    @get:JsonProperty("results", access = JsonProperty.Access.READ_ONLY)
    val fulfilledGoals: List<Goal> get() = results?.keys?.toList() ?: emptyList()

    @Transient
    val statusListeners = mutableListOf<(job: Job) -> Unit>()

    fun begin() {
        logger.debug("Job[{}] Begin executing", id)
        status = JobStatus.IN_PROGRESS
        notifyListeners()
    }

    fun completed(results: Map<Goal, Result>) {
        if (logger.isTraceEnabled) {
            logger.info("Job[{}] completed: results={}", id, results)
        } else {
            logger.info("Job[{}] completed: {} results", id, results.size)
        }
        this.results = results
        status = JobStatus.COMPLETED
        notifyListeners()
    }

    fun failed(throwable: Throwable, errorPrefix: String? = null) {
        error = if (errorPrefix == null) throwable.message else "$errorPrefix: ${throwable.message}"
        if (logger.isTraceEnabled) {
            logger.info("Job[$id] failed ${errorPrefix ?: ""}", throwable)
        } else {
            logger.info("Job[{}] failed: {}", id, error)
        }
        status = JobStatus.FAILED
        notifyListeners()
    }

    fun failed(error: String) {
        logger.info("Job[{}] failed: {}", id, error)
        this.error = error
        status = JobStatus.FAILED
        notifyListeners()
    }

    @Synchronized
    fun addListener(listener: (job: Job) -> Unit) {
        statusListeners.add(listener)
    }

    @Synchronized
    private fun notifyListeners() {
        statusListeners.forEach { it(this) }
    }
}