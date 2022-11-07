package io.hawk.dlp.integration.amazon.macie2

import com.amazonaws.services.macie2.model.Cell
import com.amazonaws.services.macie2.model.ResourcesAffected
import com.amazonaws.services.macie2.model.S3Bucket
import com.amazonaws.services.macie2.model.S3Object
import io.hawk.dlp.common.ColumnContainerOccurrence

class AmazonColumnContainerOccurrence(
    resources: ResourcesAffected,
    cell: Cell,
): ColumnContainerOccurrence {
    override val container: String
    override val column: String
    override val volume: String
    override val filePath: String
    override val database: String? = null
    override val table: String? = null

    init {
        volume = resources.s3Bucket.name
        filePath = resources.s3Object.path
        container = "s3://$volume/$filePath"
        column = cell.columnName
    }
}