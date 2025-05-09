package org.ilyatyamin.yacontesthelper.grades.service.core

import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.error.YaContestException
import org.ilyatyamin.yacontesthelper.grades.dao.GradesResult
import org.ilyatyamin.yacontesthelper.grades.dto.GoogleSheetsRequest
import org.ilyatyamin.yacontesthelper.grades.dto.GradesRequest
import org.ilyatyamin.yacontesthelper.grades.dto.GradesResponse
import org.ilyatyamin.yacontesthelper.grades.repository.GradesResultRepository
import org.ilyatyamin.yacontesthelper.grades.service.excel.ExcelFormatterServiceImpl
import org.ilyatyamin.yacontesthelper.grades.service.processor.SubmissionProcessorService
import org.ilyatyamin.yacontesthelper.grades.service.sheets.GoogleSheetsService
import org.ilyatyamin.yacontesthelper.grades.service.yacontest.YaContestService
import org.ilyatyamin.yacontesthelper.utils.UtilsService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class GradesServiceImpl(
    private val gradesResultRepository: GradesResultRepository,
    private val yaContestService: YaContestService,
    private val submissionProcessorService: SubmissionProcessorService,
    private val excelFormatterService: ExcelFormatterServiceImpl,
    private val googleSheetsService: GoogleSheetsService,
    private val utilsService: UtilsService
) : GradesService {
    companion object {
        private val log = LoggerFactory.getLogger(GradesServiceImpl::class.java)
    }

    override fun getGradesList(gradesRequest: GradesRequest?): GradesResponse {
        val problemList = yaContestService.getListOfProblems(
            gradesRequest?.contestId, gradesRequest?.yandexKey
        )
        log.info("Got problem list for request: {}", problemList)

        val submissionList = yaContestService.getSubmissionList(gradesRequest?.contestId, gradesRequest?.yandexKey)
        log.info("Got {} submissions for request", submissionList.size)

        val resultTable = submissionProcessorService.processSubmissionList(
            submissionList,
            problemList,
            gradesRequest?.participantsList,
            utilsService.processLocalDateTime(gradesRequest?.deadline)
        )

        // TODO: после регистрации допилить userId
        val result = gradesResultRepository.saveAndFlush(
            GradesResult(null, resultTable)
        )

        return GradesResponse(result.id, resultTable)
    }

    override fun generateGradesTable(tableId: Long?): ByteArray? {
        // TODO: проверка что таблица с таким tableId доступна вообще данному пользователю
        if (!gradesResultRepository.existsById(tableId!!)) {
            throw YaContestException(HttpStatus.NOT_FOUND.value(), ExceptionMessages.GRADES_TABLE_NOT_FOUND.message)
        }
        val result = gradesResultRepository.getReferenceById(tableId)

        return excelFormatterService.generateGradesTable(result.payload)
    }

    override fun writeToGoogleSheets(tableId: Long?, request: GoogleSheetsRequest?) {
        // TODO: проверка что таблица с таким tableId доступна вообще данному пользователю
        if (!gradesResultRepository.existsById(tableId!!)) {
            throw YaContestException(HttpStatus.NOT_FOUND.value(), ExceptionMessages.GRADES_TABLE_NOT_FOUND.message)
        }
        val result = gradesResultRepository.getReferenceById(tableId)

        googleSheetsService.writeToSheet(
            request?.googleServiceAccountCredentials,
            request?.spreadsheetUrl,
            request?.listName,
            result.payload
        )
    }
}
