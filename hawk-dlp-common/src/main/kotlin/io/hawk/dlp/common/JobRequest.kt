package io.hawk.dlp.common

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * A job request represents a request that can be made to created a [Job], which internally starts a
 * DLP job in the underlying implementation.
 * Currently, there is only one implementation of a [JobRequest], which is the [DirectJobRequest].
 * This request is used, when a [Job] should be directly executed.
 * In the future, there can implementations like scheduled / trigger job request that represents Job
 * Triggers (Google) / Scheduled Jobs (AWS).
 */
abstract class JobRequest(
    /**
     * Type of request. E.g. direct. Important for serialization / deserialization.
     */
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val type: String,

    /**
     * Contains a list of results that should be produced by this job.
     */
    @field:Size(min = 1)
    val goals: List<Goal>,
    /**
     * The content that should be analyzed by the DLP implementation.
     */
    @field:NotNull
    val content: Content,
    /**
     * Optional reference that should describe the origin / the source of the data that gets analyzed.
     * Helpful in case of [DirectContent] or when analyzing OLAP data and providing a reference to the OLTP.
     */
    @field:Size(max = 255)
    val sourceReference: String? = null,
)