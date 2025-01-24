package org.ilyatyamin.yacontesthelper.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ilyatyamin.yacontesthelper.configs.ExceptionMessages;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestProblem;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;
import org.ilyatyamin.yacontesthelper.dto.yacontest.GetProblemsResponse;
import org.ilyatyamin.yacontesthelper.exceptions.YaContestException;
import org.ilyatyamin.yacontesthelper.service.YaContestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class YaContestServiceImpl implements YaContestService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("configs.get-problem-url")
    private String getListOfProblemsURL;

    @Value("configs.get-submission-list.page-size")
    private int getSubmissionListPageSize;

    @Override
    public List<String> getListOfProblems(String contestId, String yandexAuthKey) {
        String authHeader = String.format("OAuth %s", yandexAuthKey);
        String requestUrl = String.format(getListOfProblemsURL, contestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                GetProblemsResponse contestResponse = objectMapper.readValue(response.getBody(), GetProblemsResponse.class);
                return selectProblemListFromResponse(contestResponse);
            } catch (JsonProcessingException e) {
                throw new YaContestException(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage());
            }
        } else if (response.getStatusCode() == HttpStatusCode.valueOf(401)) {
            throw new YaContestException(HttpStatus.UNAUTHORIZED.value(), ExceptionMessages.YACONTEST_WRONG_KEY.name());
        } else if (response.getStatusCode() == HttpStatusCode.valueOf(403)) {
            throw new YaContestException(HttpStatus.FORBIDDEN.value(), ExceptionMessages.NO_ACCESS_TO_CONTEST.name());
        } else if (response.getStatusCode() == HttpStatusCode.valueOf(404)) {
            throw new YaContestException(HttpStatus.NOT_FOUND.value(), ExceptionMessages.CONTEST_NOT_FOUND.name());
        } else {
            throw new YaContestException(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody());
        }
    }

    @Override
    public List<ContestSubmission> getSubmissionList(String contestId, String yandexAuthKey) {
        return List.of();
    }

    private List<String> selectProblemListFromResponse(GetProblemsResponse response) {
        return response.getProblems()
                .stream()
                .map(ContestProblem::getAlias)
                .sorted()
                .toList();
    }
}
