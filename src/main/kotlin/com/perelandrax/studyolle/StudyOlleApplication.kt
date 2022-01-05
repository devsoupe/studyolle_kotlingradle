package com.perelandrax.studyolle

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

val logger = KotlinLogging.logger {}

@SpringBootApplication
class StudyOlleApplication

fun main(args: Array<String>) {
    runApplication<StudyOlleApplication>(*args)
}
