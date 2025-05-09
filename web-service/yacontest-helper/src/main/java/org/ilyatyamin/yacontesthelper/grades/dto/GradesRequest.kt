package org.ilyatyamin.yacontesthelper.grades.dto

data class GradesRequest(
    val contestId: String,
    val participantsList: List<String>,
    val deadline: String?,
    val yandexKey: String
)
