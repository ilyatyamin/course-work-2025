package org.ilyatyamin.yacontesthelper.service;

import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;

import java.util.List;

public interface YaContestService {
    List<String> getListOfProblems(String contestId, String yandexAuthKey);
    List<ContestSubmission> getSubmissionList(String contestId, String yandexAuthKey);
}
