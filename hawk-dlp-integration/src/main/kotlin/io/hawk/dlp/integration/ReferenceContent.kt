package io.hawk.dlp.integration

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * A [Content], that carries a reference to the data that should be analyzed.
 * This reference could be a document in a bucket or a database table.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = FileReferenceContent::class, name = "file"),
)
interface ReferenceContent : Content