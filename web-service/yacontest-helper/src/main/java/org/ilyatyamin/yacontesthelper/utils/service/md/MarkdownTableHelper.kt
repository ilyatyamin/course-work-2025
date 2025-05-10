package org.ilyatyamin.yacontesthelper.utils.service.md

import kotlin.math.max

internal object MarkdownTableHelper {
    fun <T> generateRow(
        header: String, values: List<T>,
        headerLength: Int, cellLength: Int
    ): String {
        val sb = StringBuilder()
        sb.append("| ")
        sb.append(header)
        sb.append(" ".repeat(max((headerLength - header.length).toDouble(), 0.0).toInt()))
        sb.append("| ")
        for (value in values) {
            sb.append(value.toString())
            sb.append(" ".repeat(max((cellLength - value.toString().length).toDouble(), 0.0).toInt()))
            sb.append("| ")
        }
        sb.append("\n")
        return sb.toString()
    }

    fun generateSpecialTableDivide(cellCount: Int, headerLength: Int, cellLength: Int): String {
        val sb = StringBuilder()
        sb.append("|:")
        sb.append("-".repeat(headerLength))
        for (i in 0..<cellCount) {
            sb.append("|:")
            sb.append("-".repeat(cellLength))
        }
        sb.append("|\n")
        return sb.toString()
    }

    fun <T> calculateCellLength(list: List<T>): Int {
        var cellLength = 0
        for (value in list) {
            if (value.toString().length > cellLength) {
                cellLength = value.toString().length
            }
        }
        return cellLength + 1
    }
}
