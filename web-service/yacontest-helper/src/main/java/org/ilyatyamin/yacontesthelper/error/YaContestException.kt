package org.ilyatyamin.yacontesthelper.error

class YaContestException(val code: Int, message: String?) : RuntimeException(message)
