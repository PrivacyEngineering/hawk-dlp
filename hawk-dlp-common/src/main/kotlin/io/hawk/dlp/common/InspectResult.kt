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
     * A map of additional properties, that are not part of the finding spec itself.
     */
    @JsonAnyGetter
    @JsonAnySetter
    val additional: Map<String, Any?>? = null
) : Result