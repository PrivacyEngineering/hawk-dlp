package io.hawk.dlp.integration.google.cdlp2

import com.google.privacy.dlp.v2.ContentLocation
import io.hawk.dlp.common.ColumnContainerOccurrence

class GoogleColumnContainerOccurrence(
    location: ContentLocation
) : ColumnContainerOccurrence {
    override val container: String
    override val column: String

    // Since we can only take in direct content, we don't have a location to a file / database.
    override val volume: String? = null
    override val filePath: String? = null
    override val database: String? = null
    override val table: String? = null

    init {
        container = location.containerName
        column = location.recordLocation.fieldId.name
    }
}