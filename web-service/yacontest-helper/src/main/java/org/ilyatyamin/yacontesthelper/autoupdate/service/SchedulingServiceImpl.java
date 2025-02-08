package org.ilyatyamin.yacontesthelper.autoupdate.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;
import org.ilyatyamin.yacontesthelper.autoupdate.task.AutoUpdateTask;
import org.ilyatyamin.yacontesthelper.grades.service.core.GradesService;
import org.ilyatyamin.yacontesthelper.grades.service.sheets.GoogleSheetsService;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SchedulingServiceImpl implements SchedulingService {
    private ThreadPoolTaskScheduler updateGoogleSheetsScheduler;
    private GoogleSheetsService googleSheetsService;
    private GradesService gradesService;

    @Override
    public void putTaskOnScheduling(Long taskId, AutoUpdateRequest request) {
        AutoUpdateTask task = new AutoUpdateTask(googleSheetsService, gradesService, taskId, request);
        Trigger trigger = new CronTrigger(request.getCronExpression());
        try {
            updateGoogleSheetsScheduler.schedule(task, trigger);
            log.info("Scheduled task with id {} successfully.", taskId);
        } catch (Exception e) {
            log.warn("Error while scheduling task with id {}.", taskId, e);
            throw e;
        }
    }
}
