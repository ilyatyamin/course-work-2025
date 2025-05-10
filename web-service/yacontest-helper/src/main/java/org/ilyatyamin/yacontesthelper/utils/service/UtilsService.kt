package org.ilyatyamin.yacontesthelper.utils.service

import java.time.LocalDateTime
import java.util.*

interface UtilsService {
    fun processLocalDateTime(localDateTime: String?): Optional<LocalDateTime>
}
