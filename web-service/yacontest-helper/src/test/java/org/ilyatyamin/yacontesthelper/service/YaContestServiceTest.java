package org.ilyatyamin.yacontesthelper.service;

import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestProblem;
import org.ilyatyamin.yacontesthelper.dto.yacontest.ContestSubmission;
import org.ilyatyamin.yacontesthelper.dto.yacontest.GetProblemsResponse;
import org.ilyatyamin.yacontesthelper.dto.yacontest.GetSubmissionListResponse;
import org.ilyatyamin.yacontesthelper.service.impl.YaContestServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class YaContestServiceTest {
    @Mock
    private ContestFeignClient contestFeignClient = Mockito.mock(ContestFeignClient.class);

    @Test
    void testGetListOfProblems() {
        String contestId = "1";
        String authKey = "key";
        List<ContestProblem> problemList = List.of(
                new ContestProblem("D", "A", "program", 10),
                new ContestProblem("B", "B", "program", 10),
                new ContestProblem("C", "C", "program", 10),
                new ContestProblem("A", "D", "program", 10)
        );
        YaContestService yaContestService = new YaContestServiceImpl(contestFeignClient);

        Mockito.when(contestFeignClient.getListOfProblems(contestId, String.format("OAuth %s", authKey)))
                .thenReturn(new GetProblemsResponse(problemList));
        List<String> result = yaContestService.getListOfProblems(contestId, authKey);

        assertThat(result).isEqualTo(List.of("A", "B", "C", "D"));
    }

    @Test
    void testGetSubmissionList() {
        String contestId = "1";
        String authKey = "key";
        YaContestService yaContestService = new YaContestServiceImpl(contestFeignClient);

        List<String> aliases = List.of("A", "B", "C");
        List<ContestSubmission> expectedSubmissionList = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            ContestSubmission generated = generateSubmission(aliases.get(i));
            expectedSubmissionList.add(generated);

            Mockito.when(contestFeignClient.getListOfSubmissionsByPage(contestId, authKey, i + 1, 1))
                    .thenReturn(new GetSubmissionListResponse(1, List.of(generated)));
        }
        Mockito.when(contestFeignClient.getListOfSubmissionsByPage(contestId, authKey, 4, 1))
                .thenReturn(new GetSubmissionListResponse(1, List.of()));
        List<ContestSubmission> result = yaContestService.getSubmissionList(contestId, String.format("OAuth %s", authKey));

        assertThat(result).isEqualTo(expectedSubmissionList);
    }

    private ContestSubmission generateSubmission(String alias) {
        return new ContestSubmission("A", 1, "A", 1, 1, alias, "A", 1, "A", 1, 1, 1, "OK");
    }
}
