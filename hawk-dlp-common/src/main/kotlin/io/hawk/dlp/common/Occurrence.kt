package io.hawk.dlp.common

/**
 * Locator for a specific e.g. column in table or section in a document.
 *
 * @see ContainerOccurrence For a location, that only consists of the container
 * (file path or database + table name etc.)
 * @see ColumnContainerOccurrence For a location, that consists of the container name and the column name
 * in case of a table-based container.
 */
interface Occurrence