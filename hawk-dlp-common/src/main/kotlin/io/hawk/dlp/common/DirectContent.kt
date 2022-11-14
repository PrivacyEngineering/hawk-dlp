package io.hawk.dlp.common

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * A [Content], that carries the data that should be analyzed directly in it.
 * Rather than a [ReferenceContent] that only carries a reference to the data.
 */
interface DirectContent : Content