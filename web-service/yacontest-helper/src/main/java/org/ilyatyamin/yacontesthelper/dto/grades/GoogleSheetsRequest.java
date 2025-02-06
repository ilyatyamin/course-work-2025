package org.ilyatyamin.yacontesthelper.dto.grades;

public record GoogleSheetsRequest(
        String googleServiceAccountCredentials,
        String spreadsheetUrl,
        String listName
) {
}

