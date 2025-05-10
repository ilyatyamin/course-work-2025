package org.ilyatyamin.yacontesthelper.client.dto

import org.ilyatyamin.yacontesthelper.autoupdate.dto.AutoUpdateInfo

data class GetUserAutoUpdatesRequest(
    val username: String,
)

data class GetUserAutoUpdatesResponse(
    val tasks: List<AutoUpdateInfo>,
)

