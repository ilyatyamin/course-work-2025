package org.ilyatyamin.yacontesthelper.grades.dto;

import java.util.Map;

public record GradesResponse(
    Long tableId,
    Map<String, Map<String, Double>> results
) {}
