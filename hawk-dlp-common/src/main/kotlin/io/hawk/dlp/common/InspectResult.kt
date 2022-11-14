package io.hawk.dlp.common

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

/**
 * A [Result] for a data (type) inspection / identification job.
 * This entity contains a list of data types identified in the underlying source by the DLP job.
 */
data class InspectResult(
    override val id: UUID,
    override val timestamp: LocalDateTime,
    /**
     * The list of occurrences, grouped by info type (and possibly likelihood).
     */
    val findings: List<Finding>,
    /**
     * If true, there might be more [findings] and the findings returned are an arbitrary subset of
     * all findings. The findings list might be truncated because the input items were too large, or
     * because the server reached the maximum amount of resources allowed for a single API call.
     * For best results, divide the input into smaller batches.
     */
    val findingsTruncated: Boolean,
    /**
     * A map of additional properties, that are not part of the finding spec itself.
     */
    @get:JsonAnyGetter
    @get:JsonAnySetter
    val additional: Map<String, Any?>? = null
) : Result