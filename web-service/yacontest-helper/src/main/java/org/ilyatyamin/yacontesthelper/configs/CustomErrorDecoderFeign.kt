package org.ilyatyamin.yacontesthelper.configs

import feign.Response
import feign.codec.ErrorDecoder
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages
import org.ilyatyamin.yacontesthelper.error.YaContestException
import org.springframework.http.HttpStatus

class CustomErrorDecoderFeign : ErrorDecoder {
    override fun decode(s: String, response: Response): Exception {
        if (response.status() == 401) {
            return YaContestException(HttpStatus.FORBIDDEN.value(), ExceptionMessages.YACONTEST_WRONG_KEY.message)
        } else if (response.status() == 403) {
            return YaContestException(HttpStatus.FORBIDDEN.value(), ExceptionMessages.NO_ACCESS_TO_CONTEST.message)
        } else if (response.status() == 404) {
            return YaContestException(HttpStatus.NOT_FOUND.value(), ExceptionMessages.CONTEST_NOT_FOUND.message)
        }
        return YaContestException(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.body().toString())
    }
}
