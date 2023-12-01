package ch.ranil.aoc

import java.io.File

abstract class AbstractDay {

    private val day: String get() = this::class.java.simpleName
    private val year: String get() = this::class.java.`package`.name.split(".").last()
    val puzzleInput: List<String> get() = File("src/test/resources/$year/$day/puzzle.txt").readLines()
    val testInput get() = File("src/test/resources/$year/$day/test.txt").readLines()
    val test2Input get() = File("src/test/resources/$year/$day/test2.txt").readLines()
}
