package org.ilyatyamin.yacontesthelper.grades.dto

data class ContestProblem(
    val alias : String,
    val name: String,
    val problemType: String,
    val testCount: Long
)

data class GetProblemsResponse(
    val problems: List<ContestProblem>
)

data class ContestSubmission(
    val author: String,
    val authorId: Long,
    val compiler: String,
    val id: Long,
    val memory: Long,
    val problemAlias: String,
    val problemId: String,
    val score: Double,
    val submissionTime: String,
    val test: Long,
    val time: Long,
    val timeFromStart: Long,
    val verdict: String
)

data class GetSubmissionListResponse(
    val count: Long,
    val submissions: List<ContestSubmission>
)

data class ContestSubmissionWithCode(
    var submission: ContestSubmission,
    var code: String?
)

data class StyleSheetsSettings(
    val fontSize: Int = 11,
    val isBoldEnabled: Boolean = false,
    val isItalicEnabled: Boolean = false
) {
    companion object {
        fun getBoldStandard() : StyleSheetsSettings {
            return StyleSheetsSettings(11, true, false)
        }
    }
}
