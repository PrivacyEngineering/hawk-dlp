package io.hawk.dlp.integration

import java.util.*
import io.hawk.dlp.common.InspectResult

data class AnalyzeResultFormat(
    override val id: UUID? = null,
) : ResultFormat