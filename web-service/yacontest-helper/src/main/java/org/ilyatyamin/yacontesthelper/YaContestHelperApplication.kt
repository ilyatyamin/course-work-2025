package org.ilyatyamin.yacontesthelper

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
open class YaContestHelperApplication

fun main(args: Array<String>) {
    SpringApplication.run(YaContestHelperApplication::class.java, *args)
}
