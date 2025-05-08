package org.ilyatyamin.yacontesthelper.configs

import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean

class BeanConfigs {
    @Bean
    fun errorDecoder(): ErrorDecoder {
        return CustomErrorDecoderFeign()
    }
}
