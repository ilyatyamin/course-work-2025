package org.ilyatyamin.yacontesthelper.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.dto.scheduling.AutoUpdateRequest;
import org.ilyatyamin.yacontesthelper.repository.UpdateTaskRepository;
import org.ilyatyamin.yacontesthelper.service.SchedulingService;
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
