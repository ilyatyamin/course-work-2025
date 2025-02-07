package org.ilyatyamin.yacontesthelper.grades.dto;

public record GoogleSheetsRequest(
        String googleServiceAccountCredentials,
        String spreadsheetUrl,
        String listName
) {
}

