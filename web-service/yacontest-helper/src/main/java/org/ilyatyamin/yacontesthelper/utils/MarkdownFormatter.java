package org.ilyatyamin.yacontesthelper.utils;

import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarkdownFormatter {
    private static final Integer DEFAULT_INDENT_SIZE = 8;
    private MarkdownFormatter() {
    }

    private final StringBuilder sb = new StringBuilder();

    public static MarkdownFormatter create() {
        return new MarkdownFormatter();
    }

    public String get() {
        return sb.toString();
    }

    public MarkdownFormatter addHeader(String title, HeaderLevel level) {
        sb.append("#".repeat(level.getLevel()));
        sb.append(" ");
        sb.append(title);
        sb.append("\n");
        return this;
    }

    public MarkdownFormatter addText(String text) {
        sb.append(text);
        sb.append("\n");
        return this;
    }

    public MarkdownFormatter addCode(String code) {
        sb.append("```\n");
        sb.append(code);
        sb.append("\n```\n");
        return this;
    }

    public <K, V, L> MarkdownFormatter addTable(Map<K, Map<V, L>> table) {
        int cellLength = MarkdownTableHelper.calculateCellLength(table.keySet().stream().toList());
        int headerLength = table.values().stream()
                .findFirst()
                .map(map -> MarkdownTableHelper.calculateCellLength(map.keySet().stream().toList()))
                .orElse(0);

        sb.append(MarkdownTableHelper.generateRow("", table.keySet().stream().toList(), headerLength, cellLength));
        sb.append(MarkdownTableHelper.generateSpecialTableDivide(table.size(), headerLength, cellLength));

        List<V> rows = new ArrayList<>();
        for (var entry : table.entrySet()) {
            rows.addAll(entry.getValue().keySet());
            break;
        }

        for (var row : rows) {
            List<L> cells = new ArrayList<>();
            for (var entry : table.keySet()) {
                cells.add(table.get(entry).get(row));
            }

            sb.append(MarkdownTableHelper.generateRow(row.toString(), cells, headerLength, cellLength));
        }

        return this;
    }

    public <Key, Value> MarkdownFormatter addOneDimTable(Map<Key, Value> table) {
        int headerLength = DEFAULT_INDENT_SIZE;
        int cellLength = MarkdownTableHelper.calculateCellLength(table.values().stream().toList());

        sb.append(MarkdownTableHelper.generateRow("", table.keySet().stream().toList(), headerLength, cellLength));
        sb.append(MarkdownTableHelper.generateSpecialTableDivide(table.size(), headerLength, cellLength));
        sb.append(MarkdownTableHelper.generateRow("Amount", table.values().stream().toList(), headerLength, cellLength));

        return this;
    }


}
