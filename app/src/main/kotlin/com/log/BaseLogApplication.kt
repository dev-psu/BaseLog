package com.log

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class BaseLogApplication

fun main(args: Array<String>) {
    runApplication<BaseLogApplication>(*args)
}
