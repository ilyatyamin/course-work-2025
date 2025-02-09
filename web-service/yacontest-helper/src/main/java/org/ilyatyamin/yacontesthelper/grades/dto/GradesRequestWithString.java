package org.ilyatyamin.yacontesthelper.grades.dto;

import javax.annotation.Nullable;

public record GradesRequestWithString(
        String contestId,
        String participants,
        @Nullable String deadline,
        String yandexKey,
        @Nullable String delimiterForParticipantList
) {}
