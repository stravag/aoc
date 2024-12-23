package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day15 : AbstractDay() {

    @Test
    fun part1TestHash() {
        assertEquals(52, hash("HASH"))
    }

    @Test
    fun part1Test() {
        assertEquals(1320, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(516804, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(145, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(231844, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input
            .joinToString("")
            .split(",")
            .sumOf {
                hash(it)
            }
    }

    private fun compute2(input: List<String>): Int {
        val boxes = mutableMapOf<Int, MutableMap<String, Int>>()

        input
            .joinToString("")
            .split(",")
            .forEach { data ->
                val (_, label, operation, focalLength) = Regex("([a-zA-Z]+)([-=])([0-9])*").find(data)?.groupValues.orEmpty()
                val boxNumber = hash(label)
                when (operation) {
                    "-" -> boxes[boxNumber]?.remove(label)
                    "=" -> addEntry(boxes, boxNumber, label, focalLength)
                }
            }

        return boxes.entries.sumOf { (boxNumber, entries) ->
            var sumOfBox = 0
            entries.entries.forEachIndexed { index, entry ->
                sumOfBox += (boxNumber + 1) * (index + 1) * entry.value
            }
            sumOfBox
        }
    }

    private fun addEntry(
        boxes: MutableMap<Int, MutableMap<String, Int>>,
        boxNumber: Int,
        label: String,
        focalLength: String
    ) {
        boxes.compute(boxNumber) { _, entries ->
            entries?.also { it[label] = focalLength.toInt() }
                ?: mutableMapOf(label to focalLength.toInt())
        }
    }

    private fun hash(s: String): Int {
        return s.fold(0) { acc, c ->
            var newAcc = acc + c.code
            newAcc *= 17
            newAcc %= 256
            newAcc
        }
    }
}
