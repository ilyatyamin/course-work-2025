package org.ilyatyamin.yacontesthelper.service;

import org.ilyatyamin.yacontesthelper.configs.BeanConfigs;
import org.ilyatyamin.yacontesthelper.dto.yacontest.GetProblemsResponse;
import org.ilyatyamin.yacontesthelper.dto.yacontest.GetSubmissionListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "yaContestClient",
        url = "${configs.base-contest-url}",
        configuration = BeanConfigs.class)
public interface ContestFeignClient {
    @GetMapping(value = "/contests/{contestId}/problems")
    GetProblemsResponse getListOfProblems(@PathVariable String contestId,
                                          @RequestHeader("Authorization") String authHeader);

    @GetMapping(value = "/contests/{contestId}/submissions?page={pageId}&pageSize={pageSize}")
    GetSubmissionListResponse getListOfSubmissionsByPage(@PathVariable String contestId,
                                                         @RequestHeader("Authorization") String authHeader,
                                                         @PathVariable Integer pageId,
                                                         @PathVariable Integer pageSize);
}
