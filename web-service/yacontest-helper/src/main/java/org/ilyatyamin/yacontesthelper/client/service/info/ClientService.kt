package org.ilyatyamin.yacontesthelper.client.service.info

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateInfo
import org.ilyatyamin.yacontesthelper.client.dto.GetUserInfoResponse

interface ClientService {
    fun getUserInfo(username: String): GetUserInfoResponse

    fun getAutoUpdateList(username: String): List<AutoUpdateInfo>
}