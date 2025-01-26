package org.ilyatyamin.yacontesthelper.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestProblem;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;
import org.ilyatyamin.yacontesthelper.dto.yacontest.GetProblemsResponse;
import org.ilyatyamin.yacontesthelper.dto.yacontest.GetSubmissionListResponse;
import org.ilyatyamin.yacontesthelper.service.ContestFeignClient;
import org.ilyatyamin.yacontesthelper.service.YaContestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class YaContestServiceImpl implements YaContestService {
    private final ContestFeignClient contestFeignClient;

    @Value("${configs.get-submission-list.page-size}")
    private int getSubmissionListPageSize;

    public YaContestServiceImpl(ContestFeignClient contestFeignClient) {
        this.contestFeignClient = contestFeignClient;
    }

    @Override
    public List<String> getListOfProblems(String contestId, String yandexAuthKey) {
        String authHeader = String.format("OAuth %s", yandexAuthKey);

        GetProblemsResponse contestResponse = contestFeignClient.getListOfProblems(contestId, authHeader);
        return selectProblemListFromResponse(contestResponse);
    }

    @Override
    public List<ContestSubmission> getSubmissionList(String contestId, String yandexAuthKey) {
        String authHeader = String.format("OAuth %s", yandexAuthKey);

        List<ContestSubmission> totalSubmissions = new ArrayList<>();
        GetSubmissionListResponse contestResponse;
        int page = 1;

        do {
            contestResponse = contestFeignClient.getListOfSubmissionsByPage(contestId, authHeader, page, getSubmissionListPageSize);

            totalSubmissions.addAll(contestResponse.getSubmissions());

            page += 1;
        } while (!contestResponse.getSubmissions().isEmpty());

        return totalSubmissions;
    }

    private List<String> selectProblemListFromResponse(GetProblemsResponse response) {
        return response.getProblems()
                .stream()
                .map(ContestProblem::getAlias)
                .sorted()
                .toList();
    }
}
