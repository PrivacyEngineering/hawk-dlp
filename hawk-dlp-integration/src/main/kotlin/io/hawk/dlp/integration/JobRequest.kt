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
     * A list of results, the job should return.
     */
    @Size(min = 1)
    val resultTypes: List<ResultType>,
    /**
     * The content that should be analyzed by the DLP implementation.
     */
    @NotNull
    val content: Content,
)