package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printlnColor
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.LinkedList
import java.util.Queue
import kotlin.test.assertEquals

class Day10 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(7, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(477, compute1(puzzleInput))
    }

    @Test
    @Disabled
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(testInput))
    }

    @Test
    @Disabled
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return input.sumOf { row ->
            val parts = row.split(" ")
            val lights = Lights.parse(parts[0])
            val buttons = parts.drop(1).dropLast(1).map { Button.parse(it) }
            val presses = bfs(lights, buttons)
            Debug.debug { printlnColor("Solution found after: $presses presses", PrintColor.BLUE) }
            presses
        }
    }

    private fun bfs(desiredLights: Lights, buttons: List<Button>): Long {
        val visited = mutableSetOf<Lights>()
        val queue: Queue<Pair<Lights, Long>> = LinkedList()
        val startLights = Lights(desiredLights.lights.map { false })
        visited.add(startLights)
        queue.offer(startLights to 0L)
        Debug.debug { printlnColor("Desired Lights: $desiredLights", PrintColor.YELLOW) }
        while (queue.isNotEmpty()) {
            val (currentLights, presses) = queue.poll()
            Debug.debug { println("Current Lights: $currentLights ($presses)") }
            if (currentLights == desiredLights) {
                Debug.debug {
                    printlnColor(
                        "Current Lights: $currentLights == $desiredLights ($presses)",
                        PrintColor.GREEN
                    )
                }
                return presses
            }
            for (button in buttons) {
                val nextLightState = button.press(currentLights)
                if (nextLightState !in visited) {
                    visited.add(nextLightState)
                    queue.offer(nextLightState to presses + 1)
                }
            }
        }

        return 0L
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

    private data class Button(
        val name: String,
        val togglesLights: Set<Int>,
    ) {
        fun press(lights: Lights): Lights {
            val newLights = lights
                .lights
                .mapIndexed { i, l ->
                    when {
                        i in togglesLights -> !l
                        else -> l
                    }
                }
            return Lights(newLights)
        }


        companion object {
            fun parse(input: String): Button {
                val togglesLights = input
                    .drop(1).dropLast(1)
                    .split(",")
                    .map { it.toInt() }
                    .toSet()
                return Button(input, togglesLights)
            }
        }
    }

    private data class Lights(
        val lights: List<Boolean>,
    ) {
        override fun toString(): String =
            lights
                .joinToString("") { if (it) "#" else "." }
                .let { return "[$it]" }

        companion object {
            fun parse(input: String): Lights {
                val lights = input
                    .drop(1).dropLast(1)
                    .map { c ->
                        when (c) {
                            '.' -> false
                            '#' -> true
                            else -> error("unexpected char $c")
                        }
                    }
                return Lights(lights)
            }
        }
    }
}
