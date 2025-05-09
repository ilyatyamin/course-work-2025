package org.ilyatyamin.yacontesthelper.grades.controller

import org.ilyatyamin.yacontesthelper.error.YaContestException
import org.ilyatyamin.yacontesthelper.grades.dto.GoogleSheetsRequest
import org.ilyatyamin.yacontesthelper.grades.dto.GradesRequest
import org.ilyatyamin.yacontesthelper.grades.dto.GradesResponse
import org.ilyatyamin.yacontesthelper.grades.service.core.GradesService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/grades")
class GradesController(private val gradesService: GradesService) {
    @PostMapping
    fun getGrades(@RequestBody withList: GradesRequest?): ResponseEntity<GradesResponse> {
        val response: GradesResponse
        if (withList != null) {
            response = gradesService.getGradesList(withList)
        } else {
            throw YaContestException(422, "Incorrect body found")
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{tableId}/xlsx")
    fun getGradesExcelTable(@PathVariable tableId: Long?): ResponseEntity<ByteArray> {
        val excelSheet = gradesService.generateGradesTable(tableId)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        headers.setContentDispositionFormData("attachment", "data.xlsx")

        return ResponseEntity
            .ok()
            .headers(headers)
            .body(excelSheet)
    }

    @PostMapping("/{tableId}/googleSheets")
    fun getGradesGoogleSheets(
        @PathVariable tableId: Long?,
        @RequestBody request: GoogleSheetsRequest?
    ): ResponseEntity<Void> {
        gradesService.writeToGoogleSheets(tableId, request)
        return ResponseEntity.ok().build()
    }
}
