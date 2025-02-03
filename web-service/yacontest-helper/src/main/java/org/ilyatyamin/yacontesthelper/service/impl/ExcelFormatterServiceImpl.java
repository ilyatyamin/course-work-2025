package org.ilyatyamin.yacontesthelper.service.impl;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ilyatyamin.yacontesthelper.service.ExcelFormatterService;
import org.ilyatyamin.yacontesthelper.service.SubmissionProcessorService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ExcelFormatterServiceImpl implements ExcelFormatterService {
    private final static String SHEET_NAME = "Grades";
    private final SubmissionProcessorService submissionProcessorService;

    @Override
    public byte[] generateGradesTable(Map<String, Map<String, Double>> grades) {
        var lists = submissionProcessorService.getKeysAndValuesInMap(grades);
        List<String> tasks = lists.getFirst();
        List<String> students = lists.getSecond();

        try (Workbook table = createExcelTable(tasks, students, grades)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            table.write(byteArrayOutputStream);
            byte[] excelBytes = byteArrayOutputStream.toByteArray();
            table.close();

            return excelBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Workbook createExcelTable(List<String> tasks, List<String> students,
                                     Map<String, Map<String, Double>> grades) {
        XSSFWorkbook table = new XSSFWorkbook();
        Sheet sheet = table.createSheet(SHEET_NAME);

        // init header
        int rowCounter = 0;
        Row row = sheet.createRow(rowCounter);
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

    private void fillTableByRow(Sheet sheet,
                                int rowCounter,
                                List<String> tasks,
                                String student,
                                Map<String, Map<String, Double>> grades) {
        Row row = sheet.createRow(rowCounter);
        row.createCell(0).setCellValue(student);

        int columnCounter = 1;
        for (String task : tasks) {
            row.createCell(columnCounter).setCellValue(grades.get(task).get(student));
            ++columnCounter;
        }
    }
}
