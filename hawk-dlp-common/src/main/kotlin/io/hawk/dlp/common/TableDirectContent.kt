package io.hawk.dlp.common

import jakarta.validation.constraints.Size

/**
 * A [DirectContent], that carries a table.
 */
data class TableDirectContent(
    @Size(min = 1, max = 20)
    val headers: List<String>,
    val cells: List<List<Any?>>
) : DirectContent {

    override fun valid(): Boolean {
        cells.forEach {
            if (it.size != headers.size) return false
        }
        return true
    }
}