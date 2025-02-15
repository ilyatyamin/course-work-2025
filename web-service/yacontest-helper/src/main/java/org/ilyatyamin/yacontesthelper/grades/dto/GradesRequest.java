package org.ilyatyamin.yacontesthelper.grades.dto;

import javax.annotation.Nullable;
import java.util.List;

public record GradesRequest(
        String contestId,
        List<String> participantsList,
        @Nullable String deadline,
        String yandexKey
) {}
