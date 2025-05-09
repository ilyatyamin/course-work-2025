package org.ilyatyamin.yacontesthelper.grades.service.yacontest

import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmission

interface YaContestService {
    fun getListOfProblems(contestId: String?, yandexAuthKey: String?): List<String?>
    fun getSubmissionList(contestId: String?, yandexAuthKey: String?): List<ContestSubmission?>
}
