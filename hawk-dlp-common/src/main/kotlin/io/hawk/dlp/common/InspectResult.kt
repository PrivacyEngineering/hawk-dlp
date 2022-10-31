package io.hawk.dlp.common

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
    val findings: List<Finding>
) : Result