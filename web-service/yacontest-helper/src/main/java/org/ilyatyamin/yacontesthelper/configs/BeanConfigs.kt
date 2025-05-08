package org.ilyatyamin.yacontesthelper.configs;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class BeanConfigs {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoderFeign();
    }
}
