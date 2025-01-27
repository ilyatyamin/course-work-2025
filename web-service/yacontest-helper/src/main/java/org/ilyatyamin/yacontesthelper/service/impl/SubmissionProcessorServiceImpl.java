package org.ilyatyamin.yacontesthelper.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;
import org.ilyatyamin.yacontesthelper.service.SubmissionProcessorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class SubmissionProcessorServiceImpl implements SubmissionProcessorService {
    @Override
    public Map<String, Map<String, Double>> processSubmissionList(List<ContestSubmission> submissionList, List<String> problemList,
                                                                  List<String> participants, Optional<LocalDateTime> deadline) {
        // form start table
        Map<String, Map<String, Double>> resultTable = new HashMap<>();
        for (String problem : problemList) {
            Map<String, Double> key = new HashMap<>();
            for (String participant : participants) {
                key.put(participant, 0.0);
            }

            resultTable.put(problem, key);
        }

        // fill table by submissions
        for (ContestSubmission submission : submissionList) {
            if (participants.contains(submission.getAuthor()) && (deadline.isEmpty() || !isSubmissionDelayed(submission.getSubmissionTime(), deadline.get()))) {
                Double previousGrade = resultTable.get(submission.getProblemAlias()).get(submission.getAuthor());
                if (previousGrade < submission.getScore() && !submission.getVerdict().equals("OK")) {
                    resultTable.get(submission.getProblemAlias()).replace(submission.getAuthor(), submission.getScore());
                } else if (submission.getVerdict().equals("OK")) {
                    resultTable.get(submission.getProblemAlias()).replace(submission.getAuthor(), 1.0);
                }
            }
        }

        // calculate total
        Map<String, Double> totalResults = new HashMap<>();
        for (String participant : participants) {
            Double total = 0.0;
            for (String problem : problemList) {
                total += resultTable.get(problem).get(participant);
            }
            totalResults.put(participant, total);
        }
        resultTable.put("total", totalResults);

        return resultTable;
    }

    private Boolean isSubmissionDelayed(String authorSubmissionTime,
                                        LocalDateTime deadline) {

        LocalDateTime authorTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(authorSubmissionTime, LocalDateTime::from);
        return deadline.isBefore(authorTime);
    }
}
