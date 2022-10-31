package io.hawk.dlp.integration

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * A [Content], that carries the data that should be analyzed directly in it.
 * Rather than a [ReferenceContent] that only carries a reference to the data.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = TableDirectContent::class, name = "table"),
)
interface DirectContent : Content