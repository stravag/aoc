package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import org.junit.jupiter.api.Test
import java.util.LinkedList
import java.util.Queue
import kotlin.test.assertEquals

class Day10 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(0, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input.sumOf { row ->
            val parts = row.split(" ")
            val lights = Lights.parse(parts[0])
            val buttons = parts.drop(1).dropLast(1).map { Button.parse(it) }
            bfs(lights, buttons)
        }
    }

    private fun bfs(startLights: Lights, buttons: List<Button>): Int {
        val visited = mutableSetOf<Lights>()
        val queue: Queue<Lights> = LinkedList()
        val result = mutableMapOf<Lights, Int>()

        visited.add(startLights)
        queue.offer(startLights)

        while (queue.isNotEmpty()) {
            val currentLights = queue.poll()
            result.computeIfAbsent(currentLights) { 0 }
            result.computeIfPresent(currentLights) { _, v -> v + 1 }
            if (currentLights.desiredState == currentLights.lights) return result.getValue(currentLights)
            for (nextLightState in buttons.map { it.press(currentLights) }) {
                if (nextLightState !in visited) {
                    visited.add(nextLightState)
                    queue.offer(nextLightState)
                }
            }
        }

        return 0
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

    private data class Button(
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
            return Lights(lights.desiredState, newLights)
        }


        companion object {
            fun parse(input: String): Button {
                val togglesLights = input
                    .drop(1).dropLast(1)
                    .split(",")
                    .map { it.toInt() }
                    .toSet()
                return Button(togglesLights)
            }
        }
    }

    private class Lights(
        val desiredState: List<Boolean>,
        val lights: List<Boolean> = List(desiredState.size) { false },
    ) {
        companion object {
            fun parse(input: String): Lights {
                val desiredState = input
                    .drop(1).dropLast(1)
                    .map { c ->
                        when (c) {
                            '.' -> false
                            '#' -> true
                            else -> error("unexpected char $c")
                        }
                    }
                return Lights(desiredState)
            }
        }
    }
}
