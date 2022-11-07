package io.hawk.dlp.integration

import java.util.*
import io.hawk.dlp.common.InspectResult

data class InspectResultFormat(
    override val id: UUID? = null,
    /**
     * Type of [InspectResult.findings] that should be returned.
     */
    val occurrenceType: OccurrenceType
) : ResultFormat