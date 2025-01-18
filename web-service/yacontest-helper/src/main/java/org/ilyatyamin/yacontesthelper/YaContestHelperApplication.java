package org.ilyatyamin.yacontesthelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class YaContestHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(YaContestHelperApplication.class, args);
    }

}
