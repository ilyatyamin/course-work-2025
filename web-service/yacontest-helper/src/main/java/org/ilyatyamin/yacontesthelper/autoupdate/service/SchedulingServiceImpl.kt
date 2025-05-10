package org.ilyatyamin.yacontesthelper.autoupdate.service

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest
import org.ilyatyamin.yacontesthelper.autoupdate.task.AutoUpdateTask
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.error.YaContestException
import org.ilyatyamin.yacontesthelper.grades.service.core.GradesService
import org.ilyatyamin.yacontesthelper.grades.service.sheets.GoogleSheetsService
import org.ilyatyamin.yacontesthelper.utils.service.secure.EncryptorService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Service
import java.util.concurrent.ScheduledFuture

@Service
class SchedulingServiceImpl(
    private val updateGoogleSheetsScheduler: ThreadPoolTaskScheduler,
    private val googleSheetsService: GoogleSheetsService,
    private val gradesService: GradesService,
    private val encryptorService: EncryptorService
) : SchedulingService {
    private val scheduledTasks: MutableMap<Long?, ScheduledFuture<*>?> = mutableMapOf()

    companion object {
        private val log = LoggerFactory.getLogger(SchedulingServiceImpl::class.java)
    }

    override fun putTaskOnScheduling(taskId: Long?, request: AutoUpdateRequest) {
        val task = AutoUpdateTask(googleSheetsService, gradesService, encryptorService, taskId, request)
        val trigger: Trigger = CronTrigger(request.cronExpression)
        try {
            val future = updateGoogleSheetsScheduler.schedule(task, trigger)
            scheduledTasks[taskId] = future
            log.info("Scheduled task with id {} successfully.", taskId)
        } catch (e: Exception) {
            log.warn("Error while scheduling task with id {}.", taskId, e)
            throw e
        }
    }

    override fun removeTaskFromScheduling(taskId: Long?) {
        if (scheduledTasks.containsKey(taskId)) {
            scheduledTasks[taskId]!!.cancel(true)
            scheduledTasks.remove(taskId)
            log.info("Removed scheduled task with id {} successfully.", taskId)
        } else {
            log.info("Try to remove scheduled task with id {}. It not exists.", taskId)
            throw YaContestException(
                code = HttpStatus.NOT_FOUND.value(),
                message = ExceptionMessages.UPDATE_TASK_NOT_FOUND.message
            )
        }
    }
}
