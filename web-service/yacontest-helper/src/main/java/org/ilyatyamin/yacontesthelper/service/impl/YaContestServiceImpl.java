package org.ilyatyamin.yacontesthelper.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.configs.ExceptionMessages;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestProblem;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;
import org.ilyatyamin.yacontesthelper.dto.yacontest.GetProblemsResponse;
import org.ilyatyamin.yacontesthelper.dto.yacontest.GetSubmissionListResponse;
import org.ilyatyamin.yacontesthelper.exceptions.YaContestException;
import org.ilyatyamin.yacontesthelper.service.ContestFeignClient;
import org.ilyatyamin.yacontesthelper.service.YaContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class YaContestServiceImpl implements YaContestService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new KotlinModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private ContestFeignClient contestFeignClient;

    @Value("${configs.get-submission-list.page-size}")
    private int getSubmissionListPageSize;

    @Value("${configs.get-submission-list.url}")
    private String getSubmissionListURL;

    @Override
    public List<String> getListOfProblems(String contestId, String yandexAuthKey) {
        String authHeader = String.format("OAuth %s", yandexAuthKey);

        GetProblemsResponse contestResponse = contestFeignClient.getListOfProblems(contestId, authHeader);
        return selectProblemListFromResponse(contestResponse);
    }

    @Override
    public List<ContestSubmission> getSubmissionList(String contestId, String yandexAuthKey) {
        String authHeader = String.format("OAuth %s", yandexAuthKey);
        HttpEntity<String> request = getEntityWithAuthHeader(authHeader);

        List<ContestSubmission> totalSubmissions = new ArrayList<>();
        List<ContestSubmission> currentPageSubmissions = new ArrayList<>();

        int page = 1;

        do {
            String requestUrl = String.format(getSubmissionListURL, contestId);

            Map<String, String> queryTemplates = new HashMap<>();
            queryTemplates.put("page", String.valueOf(page));
            queryTemplates.put("pageSize", String.valueOf(getSubmissionListPageSize));
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, String.class, queryTemplates);

            if (response.getStatusCode().is2xxSuccessful()) {
                try {
                    GetSubmissionListResponse contestResponse = objectMapper.readValue(response.getBody(), GetSubmissionListResponse.class);
                    currentPageSubmissions = contestResponse.getSubmissions();
                    totalSubmissions.addAll(contestResponse.getSubmissions());

                    log.info("Get submission list for contest {}. page = {}, got {} submissions",
                            contestId, page, contestResponse.getSubmissions().size());
                } catch (JsonProcessingException e) {
                    throw new YaContestException(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage());
                }
            } else {
                processUnsuccessfulResponse(response);
            }

            page += 1;
            break;
        } while (!currentPageSubmissions.isEmpty());

        return totalSubmissions;
    }

    private List<String> processUnsuccessfulResponse(ResponseEntity<String> response) {
        if (response.getStatusCode() == HttpStatusCode.valueOf(401)) {
            throw new YaContestException(HttpStatus.UNAUTHORIZED.value(), ExceptionMessages.YACONTEST_WRONG_KEY.name());
        } else if (response.getStatusCode() == HttpStatusCode.valueOf(403)) {
            throw new YaContestException(HttpStatus.FORBIDDEN.value(), ExceptionMessages.NO_ACCESS_TO_CONTEST.name());
        } else if (response.getStatusCode() == HttpStatusCode.valueOf(404)) {
            throw new YaContestException(HttpStatus.NOT_FOUND.value(), ExceptionMessages.CONTEST_NOT_FOUND.name());
        }
        throw new YaContestException(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody());
    }

    private HttpEntity<String> getEntityWithAuthHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    private List<String> selectProblemListFromResponse(GetProblemsResponse response) {
        return response.getProblems()
                .stream()
                .map(ContestProblem::getAlias)
                .sorted()
                .toList();
    }
}
