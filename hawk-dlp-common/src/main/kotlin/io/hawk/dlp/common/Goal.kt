package io.hawk.dlp.common

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
    JsonSubTypes.Type(value = InspectGoal::class, name = "inspect"),
)
@JsonInclude(JsonInclude.Include.NON_NULL)
interface Goal {
    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: UUID?
}