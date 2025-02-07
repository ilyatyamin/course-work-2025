package org.ilyatyamin.yacontesthelper.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class UtilsServiceImpl implements UtilsService {
    @Override
    public Optional<LocalDateTime> processLocalDateTime(String localDateTime) {
        return localDateTime != null ?
                Optional.of(LocalDateTime.parse(localDateTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                : Optional.empty();
    }
}
