package io.hawk.dlp.common

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * A [Content], that carries a reference to the data that should be analyzed.
 * This reference could be a document in a bucket or a database table.
 */
interface ReferenceContent : Content