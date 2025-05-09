package org.ilyatyamin.yacontesthelper.grades.service.yacontest

import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmission
import org.ilyatyamin.yacontesthelper.grades.dto.GetProblemsResponse
import org.ilyatyamin.yacontesthelper.grades.dto.GetSubmissionListResponse
import org.ilyatyamin.yacontesthelper.grades.service.feign.ContestFeignClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class YaContestServiceImpl(
    private val contestFeignClient: ContestFeignClient,
    @Value("\${configs.get-submission-list.page-size}") private val getSubmissionListPageSize: Int
) : YaContestService {
    override fun getListOfProblems(contestId: String?, yandexAuthKey: String?): List<String?> {
        val authHeader = "OAuth $yandexAuthKey"
        val contestResponse = contestFeignClient.getListOfProblems(contestId, authHeader)

        return selectProblemListFromResponse(contestResponse)
    }

    override fun getSubmissionList(contestId: String?, yandexAuthKey: String?): List<ContestSubmission?> {
        val authHeader = "OAuth $yandexAuthKey"
        val totalSubmissions = mutableListOf<ContestSubmission>()
        var contestResponse: GetSubmissionListResponse
        var page = 1

        do {
            contestResponse = contestFeignClient.getListOfSubmissionsByPage(
                contestId = contestId,
                authHeader = authHeader,
                pageId = page,
                pageSize = getSubmissionListPageSize
            )

            totalSubmissions.addAll(contestResponse.submissions)
            page += 1
        } while (contestResponse.submissions.isNotEmpty())

        return totalSubmissions
    }

    private fun selectProblemListFromResponse(response: GetProblemsResponse?): List<String?> {
        return (response?.problems ?: listOf())
            .map { it.alias }.sorted().toList()
    }
}
