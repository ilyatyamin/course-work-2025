package org.ilyatyamin.yacontesthelper.service;

import org.ilyatyamin.yacontesthelper.dto.scheduling.AutoUpdateRequest;
import org.springframework.scheduling.config.Task;

public interface SchedulingService {
    Boolean putTaskOnScheduling(AutoUpdateRequest request);
}
