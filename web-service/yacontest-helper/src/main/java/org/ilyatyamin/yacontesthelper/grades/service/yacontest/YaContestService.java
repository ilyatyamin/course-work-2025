package org.ilyatyamin.yacontesthelper.grades.service.yacontest;

import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmission;

import java.util.List;

public interface YaContestService {
    List<String> getListOfProblems(String contestId, String yandexAuthKey);
    List<ContestSubmission> getSubmissionList(String contestId, String yandexAuthKey);
}
