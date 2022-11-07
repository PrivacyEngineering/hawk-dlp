package io.hawk.dlp.common

/**
 * A [ContainerOccurrence] that provides a data locator for columns inside a table-based container.
 * Such a container could be database table or a csv file for example.
 *
 * @see ContainerOccurrence Which describe the location of the table / file itself.
 */
interface ColumnContainerOccurrence : ContainerOccurrence {
    /**
     * Name of the column of the occurrence inside the table.
     */
    val column: String
}