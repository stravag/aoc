package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.aoc2023.Day20.Pulse.HIGH
import ch.ranil.aoc.aoc2023.Day20.Pulse.LOW
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day20 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(0, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        TODO()
    }

    private fun compute2(input: List<String>): Int {
        TODO()
    }

    private sealed interface Module {
        val name: String
        fun handlePulse(sourceModule: String, pulse: Pulse): Map<String, Pulse>
    }

    private data class Broadcast(
        override val name: String,
        private val targetModules: List<String>,
    ) : Module {
        override fun handlePulse(sourceModule: String, pulse: Pulse): Map<String, Pulse> {
            return targetModules.associateWith { pulse }
        }
    }

    private data class FlipFlop(
        override val name: String,
        private val targetModule: String,
        private var on: Boolean = false,
    ) : Module {
        override fun handlePulse(sourceModule: String, pulse: Pulse): Map<String, Pulse> {
            return if (pulse == LOW) {
                if (on) {
                    on = false
                    mapOf(targetModule to LOW)
                } else {
                    on = true
                    mapOf(targetModule to HIGH)
                }
            } else {
                emptyMap()
            }
        }
    }

    private data class Conjunction(
        override val name: String,
        private val targetModule: String,
        private var pulseHistory: MutableMap<String, Pulse> = mutableMapOf(),
    ) : Module {
        override fun handlePulse(sourceModule: String, pulse: Pulse): Map<String, Pulse> {
            pulseHistory[sourceModule] = pulse
            return if (pulseHistory.values.all { it == HIGH }) {
                mapOf(targetModule to HIGH)
            } else {
                mapOf(targetModule to LOW)
            }
        }

        fun trackInput(sourceModule: String) {
            pulseHistory[sourceModule] = LOW
        }
    }

    private enum class Pulse(val bool: Boolean) {
        LOW(false), HIGH(true)
    }
}
