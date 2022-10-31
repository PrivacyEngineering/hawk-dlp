package io.hawk.dlp.integration

import jakarta.validation.constraints.Size

/**
 * A [DirectContent], that carries a table.
 */
data class TableDirectContent(
    @Size(min = 1, max = 20)
    val headers: List<String>,
    val rows: List<List<Any?>>
) : DirectContent {

    override fun valid(): Boolean {
        rows.forEach {
            if (it.size != headers.size) return false
        }
        return true
    }
}