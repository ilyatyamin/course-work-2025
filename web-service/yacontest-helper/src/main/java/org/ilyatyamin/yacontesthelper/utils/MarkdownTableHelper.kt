package org.ilyatyamin.yacontesthelper.utils;

import java.util.List;

class MarkdownTableHelper {
    public static <T> String generateRow(String header, List<T> values,
                                         Integer headerLength, Integer cellLength) {
        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        sb.append(header);
        sb.append(" ".repeat(Math.max(headerLength - header.length(), 0)));
        sb.append("| ");
        for (T value : values) {
            sb.append(value.toString());
            sb.append(" ".repeat(Math.max(cellLength - value.toString().length(), 0)));
            sb.append("| ");
        }
        sb.append("\n");
        return sb.toString();
    }

    public static String generateSpecialTableDivide(Integer cellCount, Integer headerLength, Integer cellLength) {
        StringBuilder sb = new StringBuilder();
        sb.append("|:");
        sb.append("-".repeat(headerLength));
        for (int i = 0; i < cellCount; i++) {
            sb.append("|:");
            sb.append("-".repeat(cellLength));
        }
        sb.append("|\n");
        return sb.toString();
    }

    public static <T> Integer calculateCellLength(List<T> list) {
        int cellLength = 0;
        for (T value : list) {
            if (value.toString().length() > cellLength) {
                cellLength = value.toString().length();
            }
        }
        return cellLength + 1;
    }
}
