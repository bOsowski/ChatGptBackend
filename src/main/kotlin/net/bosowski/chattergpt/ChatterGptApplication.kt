package net.bosowski.chattergpt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatterGptApplication

fun main(args: Array<String>) {
    runApplication<ChatterGptApplication>(*args)
}
