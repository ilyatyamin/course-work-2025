package org.ilyatyamin.yacontesthelper.service;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UtilsService {
    Optional<LocalDateTime> processLocalDateTime(String localDateTime);
}
