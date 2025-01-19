package org.ilyatyamin.yacontesthelper.dto;

import lombok.AllArgsConstructor;

import javax.annotation.Nullable;
import java.util.List;

@AllArgsConstructor
public class GradesRequest {
    private String contestId;
    private List<String> participants;
    @Nullable
    private String deadline;
    private String yandexKey;
}
