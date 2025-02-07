package org.ilyatyamin.yacontesthelper.autoupdate.service;

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest;

public interface SchedulingService {
    Boolean putTaskOnScheduling(AutoUpdateRequest request);
}
