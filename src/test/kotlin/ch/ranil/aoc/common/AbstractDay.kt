package ch.ranil.aoc.common

import org.junit.jupiter.api.BeforeEach
import java.io.File

abstract class AbstractDay {
    private val day: String get() = this::class.java.simpleName
    private val year: String get() = this::class.java.packageName.split(".").last()

    protected val puzzleInput: List<String> get() = File("src/test/resources/$year/$day/puzzle.txt").readLines()
    protected val testInput get() = File("src/test/resources/$year/$day/test.txt").readLines()
    protected val test2Input get() = File("src/test/resources/$year/$day/test2.txt").readLines()

    protected var debug: Boolean = false

    fun debug(block: () -> Unit) {
        if (debug) {
            block()
        }
    }

    @BeforeEach
    fun setUp() {
        debug = false
    }
}
