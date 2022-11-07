package io.hawk.dlp.integration

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
     * Contains a list of results that should be produced by this job.
     */
    @field:Size(min = 1)
    val resultFormats: List<ResultFormat>,
    /**
     * The content that should be analyzed by the DLP implementation.
     */
    @field:NotNull
    val content: Content,
)