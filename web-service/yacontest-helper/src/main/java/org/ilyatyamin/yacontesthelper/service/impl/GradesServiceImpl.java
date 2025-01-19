package org.ilyatyamin.yacontesthelper.service.impl;

import org.ilyatyamin.yacontesthelper.dto.grades.GradesRequest;
import org.ilyatyamin.yacontesthelper.dto.grades.GradesResponse;
import org.ilyatyamin.yacontesthelper.repository.GradesResultRepository;
import org.ilyatyamin.yacontesthelper.service.GradesService;
import org.springframework.stereotype.Service;

@Service
public class GradesServiceImpl implements GradesService {
    private GradesResultRepository gradesResultRepository;

    @Override
    public GradesResponse getGradesList(GradesRequest gradesRequest) {
        return null;
    }
}
