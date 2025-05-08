package org.ilyatyamin.yacontesthelper.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
open class SchedulingConfig {
    @Bean
    open fun updateGoogleSheetsScheduler(): ThreadPoolTaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = THREAD_POOL_SIZE
        threadPoolTaskScheduler.threadNamePrefix = "updateGoogleSheetsScheduler"
        return threadPoolTaskScheduler
    }

    companion object {
        private const val THREAD_POOL_SIZE = 5
    }
}
