package org.ilyatyamin.yacontesthelper.dto.grades;

import java.util.Map;

public record GradesResponse(
    Long tableId,
    Map<String, Map<String, Double>> results
) {}
