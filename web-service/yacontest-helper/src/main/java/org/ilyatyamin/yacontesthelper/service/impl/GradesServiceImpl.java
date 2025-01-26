package org.ilyatyamin.yacontesthelper.service.impl;

import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.dao.GradesResult;
import org.ilyatyamin.yacontesthelper.dto.grades.GradesRequest;
import org.ilyatyamin.yacontesthelper.dto.grades.GradesResponse;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;
import org.ilyatyamin.yacontesthelper.repository.GradesResultRepository;
import org.ilyatyamin.yacontesthelper.service.GradesService;
import org.ilyatyamin.yacontesthelper.service.SubmissionProcessorService;
import org.ilyatyamin.yacontesthelper.service.YaContestService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class GradesServiceImpl implements GradesService {
    private GradesResultRepository gradesResultRepository;

    private YaContestService yaContestService;
    private SubmissionProcessorService submissionProcessorService;

    @Override
    public GradesResponse getGradesList(GradesRequest gradesRequest) {
        List<String> problemList = yaContestService.getListOfProblems(gradesRequest.contestId(), gradesRequest.yandexKey());
        List<ContestSubmission> submissionList = yaContestService.getSubmissionList(gradesRequest.contestId(), gradesRequest.yandexKey());

        var resultTable = submissionProcessorService.processSubmissionList(
                submissionList,
                problemList,
                gradesRequest.participants(),
                gradesRequest.deadline() != null ? Optional.of(LocalDateTime.parse(gradesRequest.deadline(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        : Optional.empty()
        );

        // TODO: после регистрации допилить userId
        GradesResult result = gradesResultRepository.saveAndFlush(
                new GradesResult(null, resultTable.toString())
        );

        return new GradesResponse(result.getId(), resultTable);
    }
}
