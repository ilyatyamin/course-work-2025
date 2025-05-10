package org.ilyatyamin.yacontesthelper.autoupdate.service;

import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;
import org.ilyatyamin.yacontesthelper.autoupdate.task.AutoUpdateTask;
import org.ilyatyamin.yacontesthelper.grades.service.core.GradesService;
import org.ilyatyamin.yacontesthelper.grades.service.sheets.GoogleSheetsService;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class SchedulingServiceImpl implements SchedulingService {
    private final ThreadPoolTaskScheduler updateGoogleSheetsScheduler;
    private final GoogleSheetsService googleSheetsService;
    private final GradesService gradesService;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks;

    public SchedulingServiceImpl(ThreadPoolTaskScheduler updateGoogleSheetsScheduler,
                                 GoogleSheetsService googleSheetsService,
                                 GradesService gradesService) {
        this.updateGoogleSheetsScheduler = updateGoogleSheetsScheduler;
        this.googleSheetsService = googleSheetsService;
        this.gradesService = gradesService;
        this.scheduledTasks = new HashMap<>();
    }

    @Override
    public void putTaskOnScheduling(Long taskId, AutoUpdateRequest request) {
        AutoUpdateTask task = new AutoUpdateTask(googleSheetsService, gradesService, taskId, request);
        Trigger trigger = new CronTrigger(request.getCronExpression());
        try {
            var future = updateGoogleSheetsScheduler.schedule(task, trigger);
            scheduledTasks.put(taskId, future);
            log.info("Scheduled task with id {} successfully.", taskId);
        } catch (Exception e) {
            log.warn("Error while scheduling task with id {}.", taskId, e);
            throw e;
        }
    }

    @Override
    public void removeTaskFromScheduling(Long taskId) {
        if (scheduledTasks.containsKey(taskId)) {
            scheduledTasks.get(taskId).cancel(true);
            scheduledTasks.remove(taskId);
            log.info("Removed scheduled task with id {} successfully.", taskId);
        } else {
            log.info("Try to remove scheduled task with id {}. It not exists.", taskId);
        }
    }
}
