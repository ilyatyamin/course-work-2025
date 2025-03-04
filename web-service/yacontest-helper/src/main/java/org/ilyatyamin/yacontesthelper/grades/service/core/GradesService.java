package org.ilyatyamin.yacontesthelper.grades.service.core;

import org.ilyatyamin.yacontesthelper.grades.dto.GoogleSheetsRequest;
import org.ilyatyamin.yacontesthelper.grades.dto.GradesRequest;
import org.ilyatyamin.yacontesthelper.grades.dto.GradesRequestWithString;
import org.ilyatyamin.yacontesthelper.grades.dto.GradesResponse;

public interface GradesService {
    GradesResponse getGradesList(GradesRequest gradesRequest);

    GradesResponse getGradesList(GradesRequestWithString gradesRequest);

    byte[] generateGradesTable(Long tableId);

    void writeToGoogleSheets(Long tableId, GoogleSheetsRequest request);
}
