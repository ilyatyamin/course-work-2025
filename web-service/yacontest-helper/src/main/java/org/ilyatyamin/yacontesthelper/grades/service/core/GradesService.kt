package org.ilyatyamin.yacontesthelper.grades.service.core

import org.ilyatyamin.yacontesthelper.grades.dto.GoogleSheetsRequest
import org.ilyatyamin.yacontesthelper.grades.dto.GradesRequest
import org.ilyatyamin.yacontesthelper.grades.dto.GradesResponse

interface GradesService {
    fun getGradesList(gradesRequest: GradesRequest?): GradesResponse

    fun generateGradesTable(tableId: Long?): ByteArray?

    fun writeToGoogleSheets(tableId: Long?, request: GoogleSheetsRequest?)
}
