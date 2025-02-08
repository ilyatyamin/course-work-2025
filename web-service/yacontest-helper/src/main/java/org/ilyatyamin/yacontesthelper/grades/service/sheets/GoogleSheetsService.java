package org.ilyatyamin.yacontesthelper.grades.service.sheets;

import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public interface GoogleSheetsService {
    <K, V, M> void writeToSheet(final String credentials,
                                final String spreadsheetName,
                                final String sheetName,
                                final Map<K, Map<V, M>> results);

    Boolean isSpreadSheetExists(Sheets userCatalog, String spreadSheetName);

    Sheets getSpreadsheets(String credentials) throws GeneralSecurityException, IOException;

}
