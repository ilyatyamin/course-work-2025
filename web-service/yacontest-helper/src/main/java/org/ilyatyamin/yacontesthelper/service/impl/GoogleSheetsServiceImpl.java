package org.ilyatyamin.yacontesthelper.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.service.GoogleSheetsService;
import org.ilyatyamin.yacontesthelper.service.SubmissionProcessorService;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

@AllArgsConstructor
public class GoogleSheetsServiceImpl implements GoogleSheetsService {
    private static final String APPLICATION_NAME = "YaContestHelper--GoogleSheetsAPI";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final Integer COLUMN_START_INDEX = 0;

    private final SubmissionProcessorService submissionProcessorService;

    public <K, V, M> void writeToSheet(final String credentials,
                                       final String spreadsheetName,
                                       final String sheetName,
                                       final Map<K, Map<V, M>> results) {
        var lists = submissionProcessorService.getKeysAndValuesInMap(results);
        List<K> keys = lists.getFirst();
        List<V> keysInValues = lists.getSecond();

        try {
            Sheets userCatalog = getSpreadsheets(credentials);
            if (isSpreadSheetExists(userCatalog, spreadsheetName)) {
                Integer listIndex = getListIndexInSpreadsheet(userCatalog, spreadsheetName, sheetName);

                int rowCounter = 0;
                if (listIndex != -1) {
                    List<String> firstData = new ArrayList<>();
                    firstData.add(" ");
                    firstData.addAll(keys.stream().map(K::toString).toList());

                    writeDataInRange(userCatalog, firstData, spreadsheetName, listIndex, rowCounter, COLUMN_START_INDEX);
                    ++rowCounter;

                    for (V value : keysInValues) {
                        List<String> data = new ArrayList<>();
                        data.add(value.toString());
                        for (K key : keys) {
                            data.add(results.get(key).get(value).toString());
                        }
                        writeDataInRange(userCatalog, data, spreadsheetName, listIndex, rowCounter, COLUMN_START_INDEX);
                        ++rowCounter;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean isSpreadSheetExists(Sheets userCatalog, String spreadSheetName) {
        try {
            userCatalog.spreadsheets().get(spreadSheetName).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Integer getListIndexInSpreadsheet(Sheets userCatalog,
                                             String spreadSheetName,
                                             String listName) {
        try {
            List<Sheet> sheetWithName = userCatalog.spreadsheets().get(spreadSheetName).execute().getSheets();
            int index = 0;
            for (Sheet sheet : sheetWithName) {
                if (sheet.getProperties().getTitle().equals(listName)) {
                    return index;
                }
                ++index;
            }
        } catch (IOException ignored) {}
        return -1;
    }

    private static Credential getCredentials(final String credentials)
            throws IOException {
        InputStream serviceAccountStream = new ByteArrayInputStream(credentials.getBytes());
        return GoogleCredential.fromStream(serviceAccountStream).createScoped(SCOPES);
    }

    private Sheets getSpreadsheets(String credentials) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                getCredentials(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private <T> void writeDataInRange(Sheets service, List<T> data,
                                      String spreadSheetId, int sheetIndex,
                                      int rowIndex, int columnIndex) {
        List<Request> requests = new ArrayList<>();
        List<CellData> cellData = data.stream()
                .map(value -> new CellData()
                        .setUserEnteredValue(new ExtendedValue().setStringValue(value.toString()))
                ).toList();

        requests.add(new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(sheetIndex)
                                .setRowIndex(rowIndex)
                                .setColumnIndex(columnIndex))
                        .setRows(List.of(new RowData().setValues(cellData)))
                        .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));

        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(requests);
        try {
            service.spreadsheets().batchUpdate(spreadSheetId, batchUpdateRequest).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
