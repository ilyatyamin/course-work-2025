package org.ilyatyamin.yacontesthelper.service;

import org.ilyatyamin.yacontesthelper.dto.grades.GoogleSheetsRequest;
import org.ilyatyamin.yacontesthelper.dto.grades.GradesRequest;
import org.ilyatyamin.yacontesthelper.dto.grades.GradesResponse;

public interface GradesService {
    GradesResponse getGradesList(GradesRequest gradesRequest);

    byte[] generateGradesTable(Long tableId);

    void writeToGoogleSheets(Long tableId, GoogleSheetsRequest request);
}
