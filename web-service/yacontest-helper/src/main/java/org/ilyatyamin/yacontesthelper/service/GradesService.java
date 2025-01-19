package org.ilyatyamin.yacontesthelper.service;

import org.ilyatyamin.yacontesthelper.dto.grades.GradesRequest;
import org.ilyatyamin.yacontesthelper.dto.grades.GradesResponse;

public interface GradesService {
    GradesResponse getGradesList(GradesRequest gradesRequest);
}
