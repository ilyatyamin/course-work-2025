package org.ilyatyamin.yacontesthelper.grades.feign

import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import org.ilyatyamin.yacontesthelper.configs.BeanConfigs
import org.ilyatyamin.yacontesthelper.grades.dto.GetProblemsResponse
import org.ilyatyamin.yacontesthelper.grades.dto.GetSubmissionListResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
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
    @Retryable(maxAttempts = 5, backoff = Backoff(delay = 1000))
    fun getListOfProblems(
        @PathVariable contestId: String?,
        @RequestHeader("Authorization") authHeader: String?
    ): GetProblemsResponse

    @GetMapping(value = ["/contests/{contestId}/submissions?page={pageId}&pageSize={pageSize}"])
    @Retryable(maxAttempts = 5, backoff = Backoff(delay = 1000))
    fun getListOfSubmissionsByPage(
        @PathVariable contestId: String?,
        @RequestHeader("Authorization") authHeader: String?,
        @PathVariable pageId: Int?,
        @PathVariable pageSize: Int?
    ): GetSubmissionListResponse

    @GetMapping(value = ["/contests/{contestId}/submissions/{submissionId}/source"])
    @Retryable(maxAttempts = 5, backoff = Backoff(delay = 50))
    @RateLimiter(name = "submissions-yacontest")
    fun getSubmissionCode(
        @PathVariable contestId: String?,
        @PathVariable submissionId: String?,
        @RequestHeader("Authorization") authHeader: String?
    ): String
}
