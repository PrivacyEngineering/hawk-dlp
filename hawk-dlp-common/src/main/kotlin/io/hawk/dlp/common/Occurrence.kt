package io.hawk.dlp.common

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Locator for a specific e.g. column in table or section in a document.
 *
 * @see ContainerOccurrence For a location, that only consists of the container
 * (file path or database + table name etc.)
 * @see ColumnContainerOccurrence For a location, that consists of the container name and the column name
 * in case of a table-based container.
 */
interface Occurrence {
    /**
     * Type of Occurrence. Used for serialization / deserialization, to determine the format / available
     * properties.
     */
    val type: String
}