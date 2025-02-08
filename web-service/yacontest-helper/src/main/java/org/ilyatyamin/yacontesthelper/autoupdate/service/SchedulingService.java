package org.ilyatyamin.yacontesthelper.autoupdate.service;

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateDeleteRequest;
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;

public interface SchedulingService {
    void putTaskOnScheduling(Long taskId, AutoUpdateRequest request);
    void removeTaskFromScheduling(Long taskId);
}
