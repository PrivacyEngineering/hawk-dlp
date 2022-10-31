package io.hawk.dlp.common

/**
 * A [Occurrence] that provides a locator by container and column.
 * Note: The container must be a table based container, that can be treated as such by the
 * underlying DLP implementation.
 *
 * @see ContainerOccurrence For the container specification.
 */
class ColumnContainerOccurrence(
    container: String,
    val column: String
) : ContainerOccurrence(container)