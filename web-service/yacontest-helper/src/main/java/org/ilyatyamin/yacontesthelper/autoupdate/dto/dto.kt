package org.ilyatyamin.yacontesthelper.autoupdate.dto

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