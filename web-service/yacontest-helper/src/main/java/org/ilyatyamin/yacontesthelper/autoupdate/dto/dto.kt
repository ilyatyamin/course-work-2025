package org.ilyatyamin.yacontesthelper.autoupdate.dto

import org.ilyatyamin.yacontesthelper.autoupdate.dao.TaskStatus

data class AutoUpdateInfo(
    val id: Long,
    val ownerId: Long,
    val cron: String,
    val updateUrl: String,
    val status: TaskStatus
)

data class AutoUpdateRequest(
    val contestId: String,
    val participants: List<String>,
    val deadline: String?,
    val yandexKey: String,
    val credentialsGoogle: String,
    val spreadsheetUrl: String,
    val sheetName: String,
    val cronExpression: String
)

data class AutoUpdateResponse(
    val id: Long
)

data class AutoUpdateDeleteRequest(
    val id : Long
)