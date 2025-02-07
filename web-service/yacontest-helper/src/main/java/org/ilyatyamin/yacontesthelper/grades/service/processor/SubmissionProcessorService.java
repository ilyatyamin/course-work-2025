package org.ilyatyamin.yacontesthelper.grades.service.processor;

import kotlin.Pair;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SubmissionProcessorService {
    Map<String, Map<String, Double>> processSubmissionList(
            List<ContestSubmission> submissionList,
            List<String> problemList,
            List<String> participants,
            Optional<LocalDateTime> deadline
    );

    Map<String, Double> getOkPercentageForTasks(Map<String, Map<String, Double>> submissions);

    <K, V, M> Pair<List<K>, List<V>> getKeysAndValuesInMap(Map<K, Map<V, M>> grades);
}
