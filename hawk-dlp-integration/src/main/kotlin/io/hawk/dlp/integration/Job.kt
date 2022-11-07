package io.hawk.dlp.integration

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.hawk.dlp.common.Result
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
    val created: LocalDateTime = LocalDateTime.now(),
    /**
     * The request that was used to create this job.
     * Encapsulates the input data.
     */
    @field:JsonIgnore
    val request: JobRequest,
    /**
     * The status of this job.
     */
    var status: JobStatus = JobStatus.CREATED,
    /**
     * The list result of results for this job.
     * Present when [status] is [JobStatus.COMPLETED].
     */
    @field:JsonIgnore
    var results: Map<ResultFormat, Result>? = null,
    /**
     * The error of this job.
     * Present when [status] is [JobStatus.FAILED].
     */
    var error: String? = null
) {
    @get:JsonProperty("results", access = JsonProperty.Access.READ_ONLY)
    val resultsFormats: List<ResultFormat> get() = results?.keys?.toList() ?: emptyList()
}