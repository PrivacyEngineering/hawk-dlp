package io.hawk.dlp.common

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Represents the result of a [Job]. While the [Job] specifies a data for a request that is analyzed
 * and processed by the underlying DLP implementation, this entity represents the final and
 * successful response of this Job. Final because some jobs might run asynchronous and give status
 * responses during processing. However, if the job fails, there might not be a result at all.
 * Concrete implementations of this class might be vendor specific.
 *
 * @see InspectResult For the result of a data inspection and identification job, that is supported
 * by many DLPs.
 * @see AnalyzeResult For the result of an e.g. k-anonymity analysis.
 */
interface Result {
    /**
     * Unique identifier of this result.
     */
    val id: UUID
    /**
     * The date-time, at which the result was created / collected.
     */
    val timestamp: LocalDateTime
}