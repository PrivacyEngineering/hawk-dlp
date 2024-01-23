package io.hawk.dlp.integration.google.cdlp2

import com.google.privacy.dlp.v2.ContentLocation
import io.hawk.dlp.common.ColumnContainerOccurrence

class GoogleColumnContainerOccurrence(
    location: ContentLocation
) : ColumnContainerOccurrence(
    container = location.containerName,
    column = location.recordLocation.fieldId.name,
    volume = null,
    filePath = null,
    database = null,
    table = null
)