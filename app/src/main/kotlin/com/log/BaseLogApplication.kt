package com.log

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BaseLogApplication

fun main(args: Array<String>) {
    runApplication<BaseLogApplication>(*args)
}
