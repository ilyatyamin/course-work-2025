package org.ilyatyamin.yacontesthelper.utils

class MarkdownFormatter private constructor() {
    private val sb = StringBuilder()

    companion object {
        private const val DEFAULT_INDENT_SIZE = 8
        fun create() = MarkdownFormatter()
    }

    fun get(): String = sb.toString()

    fun addHeader(title: String?, level: HeaderLevel): MarkdownFormatter = apply {
        sb.append("#".repeat(level.level))
            .append(" ")
            .append(title)
            .append("\n")
    }

    fun addText(text: String?): MarkdownFormatter = apply {
        sb.append(text)
            .append("\n")
    }

    fun addCode(code: String?): MarkdownFormatter = apply {
        sb.append("```\n")
            .append(code)
            .append("\n```\n")
    }

    fun <K, V, L> addTable(table: Map<K, Map<V, L>?>): MarkdownFormatter = apply {
        val keys = table.keys.toList()
        val cellLength = MarkdownTableHelper.calculateCellLength(keys)
        val headerLength =
            table.values.firstOrNull()?.keys?.let { MarkdownTableHelper.calculateCellLength(it.toList()) } ?: 0

        sb.append(MarkdownTableHelper.generateRow("", keys, headerLength, cellLength))
        sb.append(MarkdownTableHelper.generateSpecialTableDivide(table.size, headerLength, cellLength))

        val rows = table.values.firstOrNull()?.keys?.toList() ?: emptyList()

        for (row in rows) {
            val cells = keys.map { table[it]?.get(row) }
            sb.append(MarkdownTableHelper.generateRow(row.toString(), cells, headerLength, cellLength))
        }
    }

    fun <Key, Value> addOneDimTable(table: Map<Key, Value>): MarkdownFormatter = apply {
        val headerLength = DEFAULT_INDENT_SIZE
        val values = table.values.toList()
        val cellLength = MarkdownTableHelper.calculateCellLength(values)

        sb.append(MarkdownTableHelper.generateRow("", table.keys.toList(), headerLength, cellLength))
        sb.append(MarkdownTableHelper.generateSpecialTableDivide(table.size, headerLength, cellLength))
        sb.append(MarkdownTableHelper.generateRow("Amount", values, headerLength, cellLength))
    }
}