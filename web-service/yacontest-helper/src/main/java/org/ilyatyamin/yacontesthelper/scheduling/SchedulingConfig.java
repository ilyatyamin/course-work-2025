package org.ilyatyamin.yacontesthelper.scheduling;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulingConfig {
    private static final int THREAD_POOL_SIZE = 5;

    @Bean
    public ThreadPoolTaskScheduler updateGoogleSheetsScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(THREAD_POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("updateGoogleSheetsScheduler");
        return threadPoolTaskScheduler;
    }
}
