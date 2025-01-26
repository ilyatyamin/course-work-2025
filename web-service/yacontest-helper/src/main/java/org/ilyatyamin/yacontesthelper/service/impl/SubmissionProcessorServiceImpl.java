package org.ilyatyamin.yacontesthelper.service.impl;

import lombok.AllArgsConstructor;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;
import org.ilyatyamin.yacontesthelper.service.SubmissionProcessorService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class SubmissionProcessorServiceImpl implements SubmissionProcessorService {
    @Override
    public Map<String, Map<String, Double>> processSubmissionList(List<ContestSubmission> submissionList, List<String> problemList, List<String> participants) {
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
            resultTable.get(submission.getProblemAlias()).replace(submission.getAuthor(), submission.getScore());
        }

        return resultTable;
    }
}
