package org.ilyatyamin.yacontesthelper.configs

import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.web.filter.CommonsRequestLoggingFilter


class BeanConfigs {
    @Bean
    fun errorDecoder(): ErrorDecoder {
        return CustomErrorDecoderFeign()
    }

    @Bean
    fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(true)
        loggingFilter.setMaxPayloadLength(64000)
        return loggingFilter
    }
}
