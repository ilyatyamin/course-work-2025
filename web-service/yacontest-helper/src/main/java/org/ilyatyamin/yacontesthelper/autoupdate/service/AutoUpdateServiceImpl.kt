package org.ilyatyamin.yacontesthelper.autoupdate.service

import org.ilyatyamin.yacontesthelper.autoupdate.dao.TaskStatus
import org.ilyatyamin.yacontesthelper.autoupdate.dao.UpdateTaskDao
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateDeleteRequest
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateInfo
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateResponse
import org.ilyatyamin.yacontesthelper.autoupdate.repository.UpdateTaskRepository
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.error.YaContestException
import org.ilyatyamin.yacontesthelper.grades.service.sheets.GoogleSheetsService
import org.ilyatyamin.yacontesthelper.security.service.UserService
import org.ilyatyamin.yacontesthelper.utils.service.secure.EncryptorService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class AutoUpdateServiceImpl(
    private val googleSheetsService: GoogleSheetsService,
    private val schedulingService: SchedulingService,
    private val updateTaskRepository: UpdateTaskRepository,
    private val userService: UserService,
    private val encryptorService: EncryptorService
) : AutoUpdateService {
    companion object {
        private val taskIdGenerator = AtomicLong(0)
        private const val SHEET_PATTERN = "/spreadsheets/d/([a-zA-Z0-9-_]+)"
        private val log = LoggerFactory.getLogger(AutoUpdateServiceImpl::class.java)
    }

    override fun setOnAutoUpdate(autoUpdateRequest: AutoUpdateRequest): AutoUpdateResponse {
        autoUpdateRequest.spreadsheetUrl = getSpreadsheetId(autoUpdateRequest.spreadsheetUrl)

        val isWritingPossible = checkIfWritingInSheetPossible(
            googleCredentials = autoUpdateRequest.credentialsGoogle,
            spreadsheetUrl = autoUpdateRequest.spreadsheetUrl
        )
        if (!isWritingPossible) {
            throw YaContestException(
                HttpStatus.NOT_FOUND.value(),
                ExceptionMessages.GOOGLE_SHEETS_NOT_FOUND.message
            )
        }

        val taskId = taskIdGenerator.incrementAndGet()
        schedulingService.putTaskOnScheduling(taskId, autoUpdateRequest)

        val userId = userService.getCurrentUserId()
        val dao = updateTaskRepository.save(
            UpdateTaskDao(
                null, userId, taskId,
                autoUpdateRequest.cronExpression,
                autoUpdateRequest.spreadsheetUrl,
                autoUpdateRequest.credentialsGoogle,
                TaskStatus.ACTIVE
            )
        )
        return AutoUpdateResponse(dao.id)
    }

    override fun removeFromAutoUpdate(request: AutoUpdateDeleteRequest) {
        if (!updateTaskRepository.existsById(request.id)) {
            throw YaContestException(
                HttpStatus.NOT_FOUND.value(),
                ExceptionMessages.UPDATE_TASK_NOT_FOUND.message
            )
        }
        val dao = updateTaskRepository.getReferenceById(request.id)
        schedulingService.removeTaskFromScheduling(dao.taskId)
    }

    override fun getAutoUpdateInfo(username: String?): List<AutoUpdateInfo> {
        val dao = userService.getByUsername(username!!)
        return updateTaskRepository.findAllByOwnerId(dao.id)
            .map {
                AutoUpdateInfo(
                    id = it.id,
                    ownerId = it.ownerId,
                    cron = it.cronExpression,
                    updateUrl = it.updateUrl,
                    status = it.status
                )
            }
    }

    private fun getSpreadsheetId(spreadsheetUrl: String): String {
        val spreadsheetId = Regex(SHEET_PATTERN).find(spreadsheetUrl)?.groupValues?.getOrNull(1)
        if (spreadsheetId == null) {
            throw YaContestException(
                HttpStatus.BAD_REQUEST.value(),
                ExceptionMessages.GOOGLE_SHEETS_INCORRECT_URL.message
            )
        } else {
            log.warn("spreadSheetId: $spreadsheetUrl")
            return spreadsheetId
        }
    }

    private fun checkIfWritingInSheetPossible(googleCredentials: String, spreadsheetUrl: String): Boolean {
        try {
            val userSheets = googleSheetsService.getSpreadsheets(googleCredentials)
            return googleSheetsService.isSpreadSheetExists(userSheets, spreadsheetUrl)
        } catch (e: Exception) {
            return false
        }
    }
}
