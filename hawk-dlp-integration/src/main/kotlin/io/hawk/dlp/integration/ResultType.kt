package io.hawk.dlp.integration

import io.hawk.dlp.common.Result
import io.hawk.dlp.common.InspectResult
import io.hawk.dlp.common.AnalyzeResult

/**
 * Represents a target [Result] for a [Job].
 */
enum class ResultType {
    /**
     * A job that returns a [InspectResult].
     */
    INSPECT,

    /**
     * A job that returns a [AnalyzeResult].
     */
    ANALYZE
}