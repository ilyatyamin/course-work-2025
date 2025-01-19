package org.ilyatyamin.yacontesthelper.dto.yacontest

data class ContestProblem(
    val alias : String,
    val name: String,
    val problemType: String,
    val testCount: Long
);

data class GetProblemsResponse(
    val problems: List<ContestProblem>
)