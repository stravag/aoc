package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.math.min
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

class Day05 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(35, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(261668924, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(46, compute2(testInput))
    }

    @Test
    @Disabled("brute-forced")
    fun part2Puzzle() {
        assertEquals(24261545, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val (seeds, maps) = parseInput(input)
        return seeds.minOf {
            minLocationValue(it, it, maps)
        }
    }

    private fun minLocationValue(
        seedStart: Long,
        seedEnd: Long,
        maps: Map<String, List<Pair<LongRange, Pair<Long, String>>>>,
    ): Long {
        println("Calculating minLocation for: ${seedEnd - seedStart + 1} seeds")
        var minLocationValue = Long.MAX_VALUE
        val millis = measureTimeMillis {
            for (seed in seedStart..seedEnd) {
                var nextElement = Element("seed", seed)
                while (nextElement.type != "location") {
                    val map = maps.getValue(nextElement.type)
                    var mapped: Element? = null
                    for ((range, mapping) in map) {
                        val (delta, dstType) = mapping
                        if (range.contains(nextElement.value)) {
                            mapped = Element(dstType, nextElement.value + delta)
                            break
                        }
                    }
                    nextElement = mapped ?: nextElement.shift()
                }
                minLocationValue = min(minLocationValue, nextElement.value)
            }
        }
        println("Calculated in ${millis}ms")
        return minLocationValue
    }

    private fun compute2(input: List<String>): Long {
        val (seedRangesRaw, maps) = parseInput(input)
        return seedRangesRaw
            .chunked(2) { (from, range) -> from to range }
            .minOf { (from, range) ->
                minLocationValue(from, from + range, maps)
            }
    }

    private fun parseInput(input: List<String>): Pair<List<Long>, Map<String, List<Pair<LongRange, Pair<Long, String>>>>> {
        val seeds = input.first()
            .split(" ")
            .drop(1)
            .map { it.toLong() }

        mutableMapOf<Pair<String, String>, List<LongRange>>()

        val map = parseMaps(input.drop(2))

        return seeds to map
    }

    private fun parseMaps(input: List<String>): Map<String, List<Pair<LongRange, Pair<Long, String>>>> {
        if (input.size <= 1) return emptyMap()
        val mapInput = input
            .takeWhile { it.isNotBlank() }

        val restInput = input.drop(mapInput.size + 1)
        val parsedMap = parseMap(mapInput)
        return parsedMap + parseMaps(restInput)
    }

    private fun parseMap(input: List<String>): Map<String, List<Pair<LongRange, Pair<Long, String>>>> {
        val (srcType, _, dstType) = input.first().split(" ").first().split("-")
        val mappings = input
            .drop(1)
            .takeWhile { it.isNotBlank() }
            .map { line ->
                val (dstStart, srcStart, range) = line.split(" ").map { it.toLong() }
                val delta = dstStart - srcStart
                srcStart..<(srcStart + range) to (delta to dstType)
            }

        return mapOf(srcType to mappings)
    }

    data class Element(
        val type: String,
        val value: Long,
    ) {
        fun shift(): Element {
            return Element(typeMap.getValue(type), value)
        }

        companion object {
            private val typeMap = mapOf(
                "seed" to "soil",
                "soil" to "fertilizer",
                "fertilizer" to "water",
                "water" to "light",
                "light" to "temperature",
                "temperature" to "humidity",
                "humidity" to "location",
            )
        }
    }
}
