package io.hawk.dlp.integration

import io.hawk.dlp.common.Occurrence
import io.hawk.dlp.common.InspectResult
import io.hawk.dlp.common.Finding

/**
 * Used in [InspectResultFormat]. To specify the result format for inspect jobs,
 * we can have different modes of [Occurrence] types. Since the structure of a [InspectResult]
 * contains a list of [Finding]s, which internally contain a list of [Occurrence]s, we can specify
 * the type of the findings. [Occurrence]s not matching the type will be filtered out.
 * Depending on the DLP implementation, some types need aggregation or are not supported at all.
 * One example for such a type [COLUMN], which forces all [Occurrence] to be [ColumnOccurrence]
 * compliant. Meaning if you input a document based content, you wouldn't get any occurrences /
 * findings. However, if you input a table based content, you would get all occurrences. In case of
 * DLP and Macie those occurrences need to be aggregated, as we get cell based occurrences by
 * default from the API.
 */
enum class OccurrenceType {
    /**
     * Groups all findings by column.
     * Meaning if the DLP implementations generates occurrences per cell, they will be aggregated to
     * one finding per column.
     */
    COLUMN
}