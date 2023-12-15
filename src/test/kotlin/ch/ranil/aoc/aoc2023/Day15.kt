package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
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
        assertEquals(0, compute2(puzzleInput))
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
        val boxes = HashMap<Int, LinkedHashMap<String, Int>>()

        input
            .joinToString("")
            .split(",")
            .forEach {
                val boxNumber = hash(it)
                if (it.contains("-") && boxes.contains(boxNumber)) {
                    val label = it.replace("-", "")
                    boxes.getValue(boxNumber).remove(label)
                } else if (it.contains("=")){
                    val (label, focalLengthStr) = it.split("=")
                    val linkedMap = boxes[boxNumber] ?: LinkedHashMap()
                    linkedMap[label] = focalLengthStr.toInt()
                    boxes[boxNumber] = linkedMap
                }
            }

        return boxes.entries.sumOf { (boxNumber, data) ->
            var sumOfBox = 0
            data.entries.forEachIndexed { index, entry ->
                sumOfBox += boxNumber * index * entry.value
            }
            sumOfBox
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
