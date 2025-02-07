package org.ilyatyamin.yacontesthelper.grades.service.processor;

import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmission;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @Override
    public Map<String, Double> getOkPercentageForTasks(Map<String, Map<String, Double>> submissions) {
        Map<String, Double> totalResults = new HashMap<>();
        for (String problem : submissions.keySet()) {
            if (problem.equals("total")) continue;

            Map<String, Double> percentage = submissions.get(problem);
            Double totalSolved = 0.0;
            for (Double val : percentage.values()) {
                totalSolved += val;
            }
            totalResults.put(problem, (totalSolved / percentage.size()) * 100);
        }
        return totalResults;
    }

    @Override
    public <K, V, M> Pair<List<K>, List<V>> getKeysAndValuesInMap(Map<K, Map<V, M>> grades) {
        List<K> tasks = grades.keySet().stream().sorted().toList();
        List<V> students = new ArrayList<>();
        if (!grades.isEmpty()) {
            for (var entry : grades.entrySet()) {
                students.addAll(entry.getValue().keySet());
                break;
            }
        }
        return new Pair<>(tasks, students);
    }


    private Boolean isSubmissionDelayed(String authorSubmissionTime,
                                        LocalDateTime deadline) {

        LocalDateTime authorTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(authorSubmissionTime, LocalDateTime::from);
        return deadline.isBefore(authorTime);
    }
}
