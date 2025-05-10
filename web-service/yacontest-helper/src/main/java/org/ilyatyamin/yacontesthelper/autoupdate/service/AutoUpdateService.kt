package org.ilyatyamin.yacontesthelper.autoupdate.service

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateDeleteRequest
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateInfo
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateRequest
import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateResponse

interface AutoUpdateService {
    fun setOnAutoUpdate(autoUpdateRequest: AutoUpdateRequest): AutoUpdateResponse
    fun removeFromAutoUpdate(request: AutoUpdateDeleteRequest)

    fun getAutoUpdateInfo(username: String?): List<AutoUpdateInfo>
}
