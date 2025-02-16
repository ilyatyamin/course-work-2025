package org.ilyatyamin.yacontesthelper.report.dto

enum class SaveFormat(val value: String) {
    PDF("PDF"),
    MD("MD")
}

data class ReportRequest(
    val contestId : String,
    val participants : List<String>,
    val deadline : String?,
    val yandexKey : String,
    val isPlagiatCheckNeeded : Boolean,
    val mossKey : String?,
    val saveFormat : SaveFormat
)