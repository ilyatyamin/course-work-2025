package org.ilyatyamin.yacontesthelper.grades.service.sheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages;
import org.ilyatyamin.yacontesthelper.error.YaContestException;
import org.ilyatyamin.yacontesthelper.grades.dto.StyleSheetsSettings;
import org.ilyatyamin.yacontesthelper.grades.service.mapper.MapperKt;
import org.ilyatyamin.yacontesthelper.grades.service.processor.SubmissionProcessorService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

import static org.ilyatyamin.yacontesthelper.grades.service.mapper.MapperKt.cellToCellData;

@AllArgsConstructor
@Service
@Slf4j
public class GoogleSheetsServiceImpl implements GoogleSheetsService {
    private static final String APPLICATION_NAME = "YaContestHelper--GoogleSheetsAPI";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final Integer COLUMN_START_INDEX = 0;
    private static final StyleSheetsSettings DEFAULT_STYLE = new StyleSheetsSettings();
    private static final StyleSheetsSettings BOLD_STYLE = StyleSheetsSettings.Companion.getBoldStandard();

    private final SubmissionProcessorService submissionProcessorService;

    @Transactional
    public <K, V, M> void writeToSheet(final String credentials,
                                       final String spreadsheetName,
                                       final String sheetName,
                                       final Map<K, Map<V, M>> results) {
        List<Request> requests = new ArrayList<>();

        var lists = submissionProcessorService.getKeysAndValuesInMap(results);
        List<K> keys = lists.getFirst();
        List<V> keysInValues = lists.getSecond();
        int rowCounter = 0;

        try {
            Sheets userCatalog = getSpreadsheets(credentials);
            if (isSpreadSheetExists(userCatalog, spreadsheetName)) {
                Integer listIndex = getListIndexInSpreadsheet(userCatalog, spreadsheetName, sheetName);
                if (listIndex == -1) {
                    requests.addAll(createNewSheet(sheetName));
                    listIndex = getListIndexInSpreadsheet(userCatalog, spreadsheetName, sheetName);
                }

                List<Cell> firstData = keys.stream()
                        .map(value -> new Cell(value.toString(), BOLD_STYLE))
                        .toList();

                requests.addAll(writeDataInRange(new Cell("", DEFAULT_STYLE), firstData,
                        listIndex, rowCounter, COLUMN_START_INDEX));
                ++rowCounter;

                for (V value : keysInValues) {
                    List<Cell> data = new ArrayList<>();
                    Cell header = new Cell(value.toString(), BOLD_STYLE);
                    for (K key : keys) {
                        data.add(new Cell(results.get(key).get(value).toString(), DEFAULT_STYLE));
                    }
                    requests.addAll(writeDataInRange(header, data,
                            listIndex, rowCounter, COLUMN_START_INDEX));
                    ++rowCounter;
                }
            } else {
                throw new YaContestException(HttpStatus.NOT_FOUND.value(),
                        ExceptionMessages.GOOGLE_SHEETS_NOT_FOUND.getMessage());
            }

            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(requests);
            userCatalog.spreadsheets().batchUpdate(spreadsheetName, batchUpdateRequest).execute();
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
            for (Sheet sheet : sheetWithName) {
                if (sheet.getProperties().getTitle().equals(listName)) {
                    return sheet.getProperties().getSheetId();
                }
            }
        } catch (IOException ignored) {
        }
        return -1;
    }

    private static Credential getCredentials(final String credentials)
            throws IOException {
        InputStream serviceAccountStream = new ByteArrayInputStream(credentials.getBytes());
        return GoogleCredential.fromStream(serviceAccountStream).createScoped(SCOPES);
    }


    public Sheets getSpreadsheets(String credentials) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                getCredentials(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private List<Request> createNewSheet(String sheetName) {
        SheetProperties sheetProperties = new SheetProperties().setTitle(sheetName);
        AddSheetRequest addSheetRequest = new AddSheetRequest().setProperties(sheetProperties);
        Request request = new Request().setAddSheet(addSheetRequest);

        return List.of(request);
    }

    private List<Request> writeDataInRange(Cell header, List<Cell> data,
                                           int sheetIndex, int rowIndex, int columnIndex) {
        List<Request> requests = new ArrayList<>();

        List<CellData> cellData = new ArrayList<>();
        cellData.add(cellToCellData(header));
        cellData.addAll(data.stream()
                .map(MapperKt::cellToCellData)
                .toList());

        requests.add(new Request()
                .setUpdateCells(new UpdateCellsRequest()
                        .setStart(new GridCoordinate()
                                .setSheetId(sheetIndex)
                                .setRowIndex(rowIndex)
                                .setColumnIndex(columnIndex))
                        .setRows(List.of(new RowData().setValues(cellData)))
                        .setFields("userEnteredValue,userEnteredFormat")));

        requests.addAll(getRequestForAutoResize(sheetIndex, columnIndex));
        return requests;
    }

    private List<Request> getRequestForAutoResize(int sheetIndex, int columnIndex) {
        return List.of(new Request()
                .setAutoResizeDimensions(new AutoResizeDimensionsRequest()
                        .setDimensions(new DimensionRange()
                                .setSheetId(sheetIndex)
                                .setDimension("COLUMNS")
                                .setStartIndex(columnIndex)
                                .setEndIndex(columnIndex + 2)))
        );
    }

    public record Cell(
            String data,
            StyleSheetsSettings settings
    ) {
    }
}
