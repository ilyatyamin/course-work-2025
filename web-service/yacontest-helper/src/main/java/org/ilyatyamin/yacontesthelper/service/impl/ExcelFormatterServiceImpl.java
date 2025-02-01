package org.ilyatyamin.yacontesthelper.service.impl;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.ilyatyamin.yacontesthelper.service.ExcelFormatterService;
import org.springframework.stereotype.Service;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ExcelFormatterServiceImpl implements ExcelFormatterService {
    private final static String SHEET_NAME = "Grades";

    @Override
    public byte[] generateGradesTable(Map<String, Map<String, Double>> grades) {
        List<String> tasks = grades.keySet().stream().sorted().toList();
        List<String> students = new ArrayList<>();
        if (!grades.isEmpty()) {
            for (var entry : grades.entrySet()) {
                students.addAll(entry.getValue().keySet());
                break;
            }
        }

        try (HSSFWorkbook table = createExcelTable(tasks, students, grades)) {
            return table.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HSSFWorkbook createExcelTable(List<String> tasks, List<String> students,
                                         Map<String, Map<String, Double>> grades) {
        HSSFWorkbook table = new HSSFWorkbook();
        HSSFSheet sheet = table.createSheet(SHEET_NAME);

        // init header
        int rowCounter = 0;
        HSSFRow row = sheet.createRow(rowCounter);
        row.createCell(0).setCellValue("");

        int columnCounter = 1;
        for (String task : tasks) {
            row.createCell(columnCounter).setCellValue(task);
            ++columnCounter;
        }
        ++rowCounter;

        for (String student : students) {
            fillTableByRow(sheet, rowCounter, tasks, student, grades);
            ++rowCounter;
        }

        return table;
    }

    private void fillTableByRow(HSSFSheet sheet,
                                int rowCounter,
                                List<String> tasks,
                                String student,
                                Map<String, Map<String, Double>> grades) {
        HSSFRow row = sheet.createRow(rowCounter);
        row.createCell(0).setCellValue(student);

        int columnCounter = 1;
        for (String task : tasks) {
            row.createCell(columnCounter).setCellValue(grades.get(task).get(student));
            ++columnCounter;
        }
    }
}
