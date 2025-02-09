package org.ilyatyamin.yacontesthelper.grades.service.mapper

import com.google.api.services.sheets.v4.model.CellData
import com.google.api.services.sheets.v4.model.CellFormat
import com.google.api.services.sheets.v4.model.ExtendedValue
import com.google.api.services.sheets.v4.model.TextFormat
import org.ilyatyamin.yacontesthelper.grades.dto.StyleSheetsSettings
import org.ilyatyamin.yacontesthelper.grades.service.sheets.GoogleSheetsServiceImpl

fun styleSheetSettingsToTextFormat(styleSheetSettings: StyleSheetsSettings): TextFormat {
    return TextFormat()
        .setBold(styleSheetSettings.isBoldEnabled)
        .setItalic(styleSheetSettings.isItalicEnabled)
        .setFontSize(styleSheetSettings.fontSize)
}

fun cellToCellData(cell: GoogleSheetsServiceImpl.Cell): CellData {
    return CellData()
        .setUserEnteredValue(ExtendedValue().setStringValue(cell.data))
        .setUserEnteredFormat(CellFormat().setTextFormat(styleSheetSettingsToTextFormat(cell.settings)))
}