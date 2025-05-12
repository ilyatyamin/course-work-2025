package org.ilyatyamin.yacontesthelper.report.service

import com.itextpdf.html2pdf.HtmlConverter
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension
import kotlinx.coroutines.*
import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmission
import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmissionWithCode
import org.ilyatyamin.yacontesthelper.grades.feign.ContestFeignClient
import org.ilyatyamin.yacontesthelper.grades.service.processor.SubmissionProcessorService
import org.ilyatyamin.yacontesthelper.grades.service.yacontest.YaContestService
import org.ilyatyamin.yacontesthelper.report.dto.ReportRequest
import org.ilyatyamin.yacontesthelper.report.dto.SaveFormat
import org.ilyatyamin.yacontesthelper.utils.enums.HeaderLevel
import org.ilyatyamin.yacontesthelper.utils.service.md.MarkdownFormatter
import org.ilyatyamin.yacontesthelper.utils.service.UtilsService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Service
class ReportServiceImpl(
    private val yaContestService: YaContestService,
    private val contestFeignClient: ContestFeignClient,
    private val submissionProcessorService: SubmissionProcessorService,
    private val utilsService: UtilsService
) : ReportService {
    companion object {
        private val log = LoggerFactory.getLogger(ReportServiceImpl::class.java)

        private val options = MutableDataSet().apply {
            set(Parser.EXTENSIONS, listOf<Extension>(TablesExtension.create()))
        }
    }

    override fun createReport(request: ReportRequest): ByteArray {
        val response = yaContestService.getSubmissionList(
            contestId = request.contestId,
            yandexAuthKey = request.yandexKey
        )
        val authHeader = "OAuth ${request.yandexKey}"

        val codeSubmissions: List<ContestSubmissionWithCode> = runBlocking {
            fillSubmissionByCode(response, request.contestId, authHeader)
        }

        val grades = submissionProcessorService.processSubmissionList(
            response,
            yaContestService.getListOfProblems(request.contestId, request.yandexKey),
            request.participants,
            utilsService.processLocalDateTime(request.deadline)
        )

        val submissionStatsOk = submissionProcessorService.getOkPercentageForTasks(grades)
        val markdownFormat = getMarkdownReport(request, grades, submissionStatsOk, codeSubmissions)!!

        return when (request.saveFormat) {
            SaveFormat.MD -> {
                markdownFormat.toByteArray()
            }

            SaveFormat.PDF -> {
                getPDFReport(markdownFormat)
            }
        }
    }

    override fun getMarkdownReport(
        request: ReportRequest,
        totalGrades: Map<String, Map<String, Double>?>,
        submissionStatsOk: Map<String, Double>,
        codeSubmissions: List<ContestSubmissionWithCode>
    ): String {
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
        val nowDt = formatter.format(ZonedDateTime.now(ZoneId.systemDefault()))

        val format = MarkdownFormatter.create()
            .addHeader("Отчет по посылкам", HeaderLevel.FIRST)
            .addHeader(
                "Сформирован $nowDt для контеста с id #${request.contestId}", HeaderLevel.SECOND
            )
            .addText("Крайний срок сдачи: ${request.deadline}")
            .addHeader("Результаты", HeaderLevel.THIRD)
            .addTable(totalGrades)
            .addHeader(
                "Статистика по вердиктам (количество посылок со статусом ОК среди всех посылок данной задачи)",
                HeaderLevel.THIRD
            )
            .addOneDimTable(submissionStatsOk)
            .addHeader("Исходные коды посылок со статусом OK: ", HeaderLevel.SECOND)

        // TODO: планировались графики. Как их встроить?
        for ((submission, code) in codeSubmissions) {
            if (submission.verdict == "OK") {
                format.addText("id=${submission.id}. Автор=${submission.author}. Задача #${submission.problemAlias}. Вердикт #${submission.verdict}")
                format.addCode(code)
            }
        }

        // TODO: планировалась проверка на плагиат. Как встроить?
        return format.get()
    }


    private suspend fun fillSubmissionByCode(
        submissions: List<ContestSubmission?>,
        contestId: String,
        authHeader: String,
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): List<ContestSubmissionWithCode> = coroutineScope.run {
        return submissions.filterNotNull().map { submission ->
            async {
                ContestSubmissionWithCode(
                    submission = submission,
                    code = if (submission.verdict == "OK") {
                        try {
                            contestFeignClient.getSubmissionCode(
                                contestId = contestId,
                                submissionId = submission.id.toString(),
                                authHeader = authHeader)
                        } catch (e: Exception) {
                            log.warn("Error getting code for submission ${submission.id}: ${e.message}")
                            null
                        }
                    } else {
                        null
                    }
                )
            }
        }.awaitAll()
    }

    private fun getPDFReport(markdownReport: String): ByteArray {
        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()

        val htmlReport: Node = parser.parse(markdownReport)
        val baos = ByteArrayOutputStream()

        HtmlConverter.convertToPdf(renderer.render(htmlReport), baos)
        return baos.toByteArray()
    }
}
