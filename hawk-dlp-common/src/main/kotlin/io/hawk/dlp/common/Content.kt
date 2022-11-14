package io.hawk.dlp.common

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Represents a source for content that should be scanned by the DLP implementation.
 * The class represents a content passed along a [JobRequest].
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    Type(value = TableDirectContent::class, name = "direct-table"),
    Type(value = FileReferenceContent::class, name = "reference-file")
)
interface Content {
    /**
     * Check if the content is valid.
     * This should be replaced by JSR-303 validation in the future.
     */
    fun valid(): Boolean = true
}