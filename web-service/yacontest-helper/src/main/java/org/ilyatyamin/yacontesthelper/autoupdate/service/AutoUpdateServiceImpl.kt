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
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class AutoUpdateServiceImpl(
    private val googleSheetsService: GoogleSheetsService,
    private val schedulingService: SchedulingService,
    private val updateTaskRepository: UpdateTaskRepository,
    private val userService: UserService
) : AutoUpdateService {
    companion object {
        private val taskIdGenerator = AtomicLong(0)
    }

    override fun setOnAutoUpdate(autoUpdateRequest: AutoUpdateRequest): AutoUpdateResponse {
        if (!checkIfWritingInSheetPossible(autoUpdateRequest.credentialsGoogle, autoUpdateRequest.spreadsheetUrl
            )
        ) {
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

    private fun checkIfWritingInSheetPossible(googleCredentials: String, spreadsheetUrl: String): Boolean {
        try {
            val userSheets = googleSheetsService.getSpreadsheets(googleCredentials)
            return googleSheetsService.isSpreadSheetExists(userSheets, spreadsheetUrl)
        } catch (e: Exception) {
            return false
        }
    }
}
