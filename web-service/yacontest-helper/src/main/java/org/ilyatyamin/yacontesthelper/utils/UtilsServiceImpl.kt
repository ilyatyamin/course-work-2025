package org.ilyatyamin.yacontesthelper.utils

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@Service
class UtilsServiceImpl : UtilsService {
    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    override fun processLocalDateTime(localDateTime: String?): Optional<LocalDateTime> {
        return try {
            Optional.of(LocalDateTime.parse(localDateTime.toString(), formatter))
        } catch (e: DateTimeParseException) {
            println("Error parsing LocalDateTime: ${e.message}")
            Optional.empty()
        }
    }
}