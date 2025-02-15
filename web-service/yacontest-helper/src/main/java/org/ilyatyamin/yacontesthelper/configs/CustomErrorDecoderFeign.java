package org.ilyatyamin.yacontesthelper.configs;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.ilyatyamin.yacontesthelper.error.ExceptionMessages;
import org.ilyatyamin.yacontesthelper.error.YaContestException;
import org.springframework.http.HttpStatus;

public class CustomErrorDecoderFeign implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        if (response.status() == 401) {
            return new YaContestException(HttpStatus.UNAUTHORIZED.value(), ExceptionMessages.YACONTEST_WRONG_KEY.name());
        } else if (response.status() == 403) {
            return new YaContestException(HttpStatus.FORBIDDEN.value(), ExceptionMessages.NO_ACCESS_TO_CONTEST.name());
        } else if (response.status() == 404) {
            return new YaContestException(HttpStatus.NOT_FOUND.value(), ExceptionMessages.CONTEST_NOT_FOUND.name());
        }
        return new YaContestException(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.body().toString());
    }
}
