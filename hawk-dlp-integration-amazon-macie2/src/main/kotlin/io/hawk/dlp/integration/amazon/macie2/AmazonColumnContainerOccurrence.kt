package io.hawk.dlp.integration.amazon.macie2

import com.amazonaws.services.macie2.model.Cell
import com.amazonaws.services.macie2.model.ResourcesAffected
import com.fasterxml.jackson.annotation.JsonIgnoreType
import io.hawk.dlp.common.ColumnContainerOccurrence

class AmazonColumnContainerOccurrence(resources: ResourcesAffected, cell: Cell) : ColumnContainerOccurrence(
    volume = resources.s3Bucket.name,
    filePath = resources.s3Object.path,
    container = "s3://${resources.s3Bucket.name}/${resources.s3Object.path}",
    column = cell.columnName,
    database = null,
    table = null
)