package io.hawk.dlp.integration

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.UUID

/**
 * A format, in which the result should be returned.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = InspectResultFormat::class, name = "inspect"),
)
@JsonInclude(JsonInclude.Include.NON_NULL)
interface ResultFormat {
    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: UUID?
}