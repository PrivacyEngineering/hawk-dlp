package io.hawk.dlp.integration.google.cdlp2

import com.google.privacy.dlp.v2.*
import com.google.privacy.dlp.v2.Table.Row
import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// TODO:
// Request Google Cloud DLP Java client to also generate Kotlin stubs.
// This should be possible with ProtoBuf.

fun inspectContentRequest(builder: InspectContentRequest.Builder.() -> Unit): InspectContentRequest =
    InspectContentRequest.newBuilder().apply(builder).build()

fun InspectContentRequest.Builder.contentItem(builder: ContentItem.Builder.() -> Unit) {
    item = ContentItem.newBuilder().apply(builder).build()
}

fun ContentItem.Builder.table(builder: Table.Builder.() -> Unit) {
    table = Table.newBuilder().apply(builder).build()
}

fun Table.Builder.header(builder: FieldId.Builder.() -> Unit) {
    addHeaders(FieldId.newBuilder().apply(builder).build())
}

fun Table.Builder.row(builder: Row.Builder.() -> Unit) {
    addRows(Row.newBuilder().apply(builder).build())
}

fun Row.Builder.cell(builder: Value.Builder.() -> Unit) {
    addValues(Value.newBuilder().apply(builder).build())
}

fun Value.Builder.setValue(value: Any?) {
    when (value) {
        is String -> {
            val date = runCatching { LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME) }
                .getOrNull()

            if (date != null) {
                timestampValue {
                    seconds = date.toEpochSecond(ZoneOffset.UTC)
                    nanos = date.nano
                }
            } else {
                stringValue = value
            }
        }

        is Int -> integerValue = value.toLong()
        is Long -> integerValue = value
        is Float -> floatValue = value.toDouble()
        is Double -> floatValue = value
        is Boolean -> booleanValue = value
    }
}

// Timestamp

fun Value.Builder.timestampValue(builder: Timestamp.Builder.() -> Unit) {
    timestampValue = Timestamp.newBuilder().apply(builder).build()
}
