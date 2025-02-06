package org.ilyatyamin.yacontesthelper.service;

import java.util.Map;

public interface GoogleSheetsService {
    <K, V, M> void writeToSheet(final String credentials,
                                final String spreadsheetName,
                                final String sheetName,
                                final Map<K, Map<V, M>> results);
}
