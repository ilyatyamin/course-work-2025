package org.ilyatyamin.yacontesthelper.configs

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "YaContest Helper API",
        description = "API для помощника работы с Яндекс Контестом",
        version = "1.0.0",
        contact = Contact(name = "Ilya Tyamin", email = "tyaminilya@gmail.com")
    )
)
open class SwaggerConfig
