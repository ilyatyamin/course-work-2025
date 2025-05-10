package org.ilyatyamin.yacontesthelper.autoupdate.service

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest

interface SchedulingService {
    fun putTaskOnScheduling(taskId: Long?, request: AutoUpdateRequest)
    fun removeTaskFromScheduling(taskId: Long?)
}
