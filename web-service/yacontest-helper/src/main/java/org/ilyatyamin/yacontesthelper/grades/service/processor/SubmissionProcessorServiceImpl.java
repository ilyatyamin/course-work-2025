package org.ilyatyamin.yacontesthelper.grades.service.processor;

import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilyatyamin.yacontesthelper.grades.dto.ContestSubmission;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class SubmissionProcessorServiceImpl implements SubmissionProcessorService {
    @Override
    public Map<String, Map<String, Double>> processSubmissionList(List<ContestSubmission> submissionList, List<String> problemList,
                                                                  List<String> participants, Optional<LocalDateTime> deadline) {
        Map<String, Map<String, Double>> resultTable = new LinkedHashMap<>();
        List<String> totalParticipants = submissionList.stream()
                .map(ContestSubmission::getAuthor)
                .toList();
        List<String> finalParticipantList = isParticipantsListEmpty(participants) ? totalParticipants : participants;
        log.info("Total participants: {}", finalParticipantList);
        log.info("Total participants: {}", isParticipantsListEmpty(participants));

        // form start table
        for (String problem : problemList) {
            Map<String, Double> key = new LinkedHashMap<>();
            for (String participant : finalParticipantList) {
                key.put(participant, 0.0);
            }

            resultTable.put(problem, key);
        }

        // fill table by submissions
        for (ContestSubmission submission : submissionList) {
            if (finalParticipantList.contains(submission.getAuthor()) && (deadline.isEmpty() || !isSubmissionDelayed(submission.getSubmissionTime(), deadline.get()))) {
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
        for (String participant : finalParticipantList) {
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
            totalResults.put(problem, Double.valueOf("%.2f".formatted((totalSolved / percentage.size()) * 100)));
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
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            LocalDateTime authorTime = LocalDateTime.parse(authorSubmissionTime, formatter);
            OffsetDateTime authorTimeWithZone = authorTime.atOffset(ZoneOffset.UTC);
            return deadline.isBefore(authorTimeWithZone.toLocalDateTime());
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + authorSubmissionTime);
            log.error("Error parsing date: {}", e.getMessage());
            return false;
        }
    }

    private boolean isParticipantsListEmpty(List<String> participants) {
        return participants == null || participants.isEmpty() || participants.stream().allMatch(String::isEmpty);
    }
}
