package org.ilyatyamin.yacontesthelper.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

public class BeanConfigs {
    @Bean
    ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
