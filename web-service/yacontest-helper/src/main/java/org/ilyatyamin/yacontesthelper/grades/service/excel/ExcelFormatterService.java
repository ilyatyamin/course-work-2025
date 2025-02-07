package org.ilyatyamin.yacontesthelper.grades.service.excel;

import java.util.Map;

public interface ExcelFormatterService {
    byte[] generateGradesTable(Map<String, Map<String, Double>> grades);
}
