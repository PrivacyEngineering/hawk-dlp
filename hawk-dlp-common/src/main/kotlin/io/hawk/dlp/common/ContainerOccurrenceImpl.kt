package io.hawk.dlp.common

data class ContainerOccurrenceImpl(
    override val container: String,
    override val volume: String?,
    override val filePath: String?,
    override val database: String?,
    override val table: String?
) : ContainerOccurrence
