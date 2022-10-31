package io.hawk.dlp.common

import java.util.*

/**
 * The generalized finding of a data (type) inspection / identification job.
 * A finding consists of a list of [Occurrence]s, where each occurrence points to a certain data
 * source, e.g. table-column. All [Occurrence]s of a Finding are homogenous among its [InfoType],
 * which represents the type of data identified at this specific source, e.g. a E-Mail address.
 */
class Finding(
    /**
     * Unique identifier for a specific finding.
     */
    val id: UUID,
    /**
     * Type of data identified, e.g. E-Mail address, private keys...
     */
    val infoType: InfoType,
    /**
     * A value between 0 and 1, where a bigger value indicates a bigger likelihood, that the info
     * type actually matches. If no value is given TODO
     */
    val likelihood: Float?,
    /**
     * A list of sources / references to data that contains the [infoType].
     */
    val occurrences: List<Occurrence>
)