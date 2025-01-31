package org.ilyatyamin.yacontesthelper.service;

import java.util.Map;

public interface ExcelFormatterService {
    byte[] generateGradesTable(Map<String, Map<String, Double>> grades);
}
