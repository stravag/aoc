package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day05 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(35, compute1(testInput))
        assertEquals(261668924, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(46, compute2(testInput))
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val (seeds, maps) = parseInput(input)
        val seedValues = seeds.toMutableList()

        seeds.forEachIndexed { index, element ->
            var seed = element
            while (seed.type != "location") {
                val map = maps.getValue(seed.type)
                var mapped: Element? = null
                map.forEach { (range, mapping) ->
                    val (delta, dstType) = mapping
                    if (range.contains(seed.value)) {
                        mapped = Element(dstType, seed.value + delta)
                        return@forEach
                    }
                }
                seedValues[index] = mapped ?: seed.shift()
                seed = seedValues[index]
            }
        }
        return seedValues.minOf { it.value }
    }

    private fun compute2(input: List<String>): Long {
        val (seedRangesRaw, maps) = parseInput(input)
        val seedRanges = seedRangesRaw
            .map { it.value }
            .windowed(2, 2) { (from, range) ->
                from..(from + range)
            }

        val range1 = 0L..1123123123123L
        val range2 = 1123123123L..2123123123123L

        val intersect = range1.intersect(range2)
        return input.size.toLong()
    }

    private fun parseInput(input: List<String>): Pair<List<Element>, Map<String, List<Pair<LongRange, Pair<Long, String>>>>> {
        val seeds = input.first()
            .split(" ")
            .drop(1)
            .map { Element("seed", it.toLong()) }

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
                srcStart..(srcStart + range) to (delta to dstType)
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
