package org.ilyatyamin.yacontesthelper.autoupdate.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;
import org.ilyatyamin.yacontesthelper.autoupdate.repository.UpdateTaskRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class SchedulingServiceImpl implements SchedulingService {
    private UpdateTaskRepository updateTaskRepository;
    private ThreadPoolTaskScheduler updateGoogleSheetsScheduler;

    @Override
    public Boolean putTaskOnScheduling(AutoUpdateRequest request) {
        return null;
    }
}
