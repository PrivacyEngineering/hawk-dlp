package io.hawk.dlp.integration

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
    property = "kind"
)
@JsonSubTypes(
    Type(value = DirectContent::class, name = "direct"),
    Type(value = ReferenceContent::class, name = "reference")
)
interface Content {
    /**
     * Check if the content is valid.
     * This should be replaced by JSR-303 validation in the future.
     */
    fun valid(): Boolean = true
}