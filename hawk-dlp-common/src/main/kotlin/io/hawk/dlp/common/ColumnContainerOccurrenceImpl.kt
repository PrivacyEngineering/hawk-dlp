package io.hawk.dlp.common

data class ColumnContainerOccurrenceImpl(
    override val column: String,
    override val container: String,
    override val volume: String?,
    override val filePath: String?,
    override val database: String?,
    override val table: String?
) : ColumnContainerOccurrence