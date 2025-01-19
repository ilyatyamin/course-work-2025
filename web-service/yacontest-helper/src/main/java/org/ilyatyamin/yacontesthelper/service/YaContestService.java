package org.ilyatyamin.yacontesthelper.service;

import java.util.List;

public interface YaContestService {
    List<String> getListOfProblems(String contestId, String yandexAuthKey);
}
