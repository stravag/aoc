package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.aoc2023.Day20.Pulse.HIGH
import ch.ranil.aoc.aoc2023.Day20.Pulse.LOW
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day20 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(32, compute1(testInput, 1))
        assertEquals(32000000, compute1(testInput, 1000))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(800830848, compute1(puzzleInput, 1000))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>, buttonPushes: Int): Long {
        val modules = input
            .map { Module.parse(it) }
            .associateBy { it.name }
            .trackInputsOfConjunctionModules()

        val pulseCount = mutableMapOf(
            LOW to 0L,
            HIGH to 0L,
        )
        repeat(buttonPushes) {
            val initialPulse = modules.getValue(Broadcast.NAME).handlePulse("irrelevant", LOW)
            pulseCount.computeIfPresent(LOW) { _, count -> count + 1 }

            val pulsesToProcess: MutableList<ModuleAnswer> = mutableListOf(initialPulse)
            while (pulsesToProcess.isNotEmpty()) {
                val (src, pulses) = pulsesToProcess.removeFirst()
                pulses.forEach { (dst, pulse) ->
                    pulseCount.computeIfPresent(pulse) { _, count -> count + 1 }
//                    println("$src -${pulse.name.lowercase()}-> $dst")
                    val nextPulse = modules[dst]?.handlePulse(src, pulse)
                    if (nextPulse?.nextPulses.orEmpty().isNotEmpty()) {
                        pulsesToProcess.add(requireNotNull(nextPulse))
                    }
                }
            }
        }
        return pulseCount.values.reduce { acc, l -> acc * l }
    }

    private fun Map<String, Module>.trackInputsOfConjunctionModules(): Map<String, Module> {
        this.forEach { (sourceName, module) ->
            module.targets.forEach { targetName ->
                val target = this[targetName]
                if (target is Conjunction) target.trackInput(sourceName)
            }
        }
        return this
    }

    private fun compute2(input: List<String>): Int {
        TODO()
    }

    private sealed interface Module {
        val name: String
        val targets: List<String>
        fun handlePulse(sourceModule: String, pulse: Pulse): ModuleAnswer =
            ModuleAnswer(name, handlePulseInternal(sourceModule, pulse))

        fun handlePulseInternal(sourceModule: String, pulse: Pulse): List<NextPulse>

        companion object {
            fun parse(s: String): Module {
                val (name, targetsStr) = s.split(" -> ")
                val targets = targetsStr.split(",").map { it.trim() }
                return when {
                    name == "broadcaster" -> Broadcast(targets)
                    name.startsWith("%") -> FlipFlop(name.drop(1), targets)
                    name.startsWith("&") -> Conjunction(name.drop(1), targets)
                    else -> throw IllegalArgumentException("not a module: $s")
                }
            }
        }
    }

    private data class Broadcast(
        override val targets: List<String>,
    ) : Module {
        override val name: String = NAME

        override fun handlePulseInternal(sourceModule: String, pulse: Pulse): List<NextPulse> {
            return targets.map { NextPulse(it, pulse) }
        }

        companion object {
            const val NAME = "broadcast"
        }
    }

    private data class FlipFlop(
        override val name: String,
        override val targets: List<String>,
        private var on: Boolean = false,
    ) : Module {
        override fun handlePulseInternal(sourceModule: String, pulse: Pulse): List<NextPulse> {
            return if (pulse == LOW) {
                if (on) {
                    on = false
                    targets.map { NextPulse(it, LOW) }
                } else {
                    on = true
                    targets.map { NextPulse(it, HIGH) }
                }
            } else {
                emptyList()
            }
        }
    }

    private data class Conjunction(
        override val name: String,
        override val targets: List<String>,
        private var pulseHistory: MutableMap<String, Pulse> = mutableMapOf(),
    ) : Module {
        override fun handlePulseInternal(sourceModule: String, pulse: Pulse): List<NextPulse> {
            pulseHistory[sourceModule] = pulse
            return if (pulseHistory.values.all { it == HIGH }) {
                targets.map { NextPulse(it, LOW) }
            } else {
                targets.map { NextPulse(it, HIGH) }
            }
        }

        fun trackInput(sourceModule: String) {
            pulseHistory[sourceModule] = LOW
        }
    }

    private data class ModuleAnswer(
        val nameOfAnsweringModule: String,
        val nextPulses: List<NextPulse>,
    )

    private data class NextPulse(
        val dstModule: String,
        val pulse: Pulse,
    )

    private enum class Pulse {
        LOW, HIGH
    }
}
