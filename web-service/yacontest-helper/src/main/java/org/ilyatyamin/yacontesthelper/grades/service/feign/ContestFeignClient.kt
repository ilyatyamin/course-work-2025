package org.ilyatyamin.yacontesthelper.grades.service.feign

import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.ilyatyamin.yacontesthelper.configs.BeanConfigs
import org.ilyatyamin.yacontesthelper.grades.dto.GetProblemsResponse
import org.ilyatyamin.yacontesthelper.grades.dto.GetSubmissionListResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    value = "yaContestClient",
    url = "\${configs.base-contest-url}",
    configuration = [BeanConfigs::class]
)
interface ContestFeignClient {
    @GetMapping(value = ["/contests/{contestId}/problems"])
    fun getListOfProblems(
        @PathVariable contestId: String?,
        @RequestHeader("Authorization") authHeader: String?
    ): GetProblemsResponse

    @GetMapping(value = ["/contests/{contestId}/submissions?page={pageId}&pageSize={pageSize}"])
    fun getListOfSubmissionsByPage(
        @PathVariable contestId: String?,
        @RequestHeader("Authorization") authHeader: String?,
        @PathVariable pageId: Int?,
        @PathVariable pageSize: Int?
    ): GetSubmissionListResponse

    @GetMapping(value = ["/contests/{contestId}/submissions/{submissionId}/source"])
    @RateLimiter(name = "submissions-yacontest")
    fun getSubmissionCode(
        @PathVariable contestId: String?,
        @PathVariable submissionId: String?,
        @RequestHeader("Authorization") authHeader: String?
    ): String
}
