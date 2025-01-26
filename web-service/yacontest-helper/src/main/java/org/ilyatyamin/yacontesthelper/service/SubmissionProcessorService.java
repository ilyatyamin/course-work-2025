package org.ilyatyamin.yacontesthelper.service;

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
}
