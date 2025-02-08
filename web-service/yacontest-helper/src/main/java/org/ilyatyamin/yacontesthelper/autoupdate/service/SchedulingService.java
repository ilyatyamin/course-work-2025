package org.ilyatyamin.yacontesthelper.autoupdate.service;

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;

public interface SchedulingService {
    void putTaskOnScheduling(Long taskId, AutoUpdateRequest request);
}
