package io.hawk.dlp.common

import java.util.*

data class InspectGoal(
    override val id: UUID? = null,
    /**
     * Type of [InspectResult.findings] that should be returned.
     */
    val occurrenceType: OccurrenceType
) : Goal