package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.Debug.debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.AbstractMap
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day10 : AbstractDay() {

    @Test
    fun part1TestDummy() {
        Debug.enable()

        assertEquals(
            4, compute1(
                """
                ..90..9
                ...1.98
                ...2..7
                6543456
                765.987
                876....
                987....
            """.trimIndent().lines()
            )
        )

        assertEquals(
            2, compute1(
                """
                ...0...
                ...1...
                ...2...
                6543456
                7.....7
                8.....8
                9.....9
            """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(36, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(778, compute1(puzzleInput))
    }

    @Test
    fun part2TestDummy() {
        Debug.enable()
        assertEquals(
            3, compute2(
                """
            .....0.
            ..4321.
            ..5..2.
            ..6543.
            ..7..4.
            ..8765.
            ..9....
        """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(81, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1925, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val map = Map(input)
        return map.countTrails().toLong()
    }

    private fun compute2(input: List<String>): Long {
        val map = Map(input)
        return map.countTrailRatings().toLong()
    }

    private class Map(input: List<String>) : AbstractMap(input) {
        fun countTrails(): Int {
            return allPoints()
                .filter { heightAt(it) == 0 }
                .sumOf { trailhead -> countTrails(trailhead) }
        }

        fun countTrailRatings(): Int {
            return allPoints()
                .filter { heightAt(it) == 0 }
                .sumOf { trailhead -> countTrailRatings(trailhead) }
        }

        private fun countTrails(point: Point, seen: MutableSet<Point> = mutableSetOf()): Int {

            if (heightAt(point) == 9 && !seen.contains(point)) {
                seen.add(point)
                debug {
                    println("Found a path")
                    print(seen)
                }
                return 1
            }

            seen.add(point)
            val trails = listOf(point.north(), point.east(), point.south(), point.west())
                .filter { heightAt(it) - heightAt(point) == 1 }
                .filterNot(seen::contains)
                .sumOf { candidate ->
                    countTrails(candidate, seen)
                }

            return trails
        }

        private fun countTrailRatings(point: Point, seen: Set<Point> = setOf(point)): Int {

            if (heightAt(point) == 9) {
                debug {
                    println("Found a path")
                    print(seen + point)
                }
                return 1
            }

            val trails = listOf(point.north(), point.east(), point.south(), point.west())
                .filter { heightAt(it) - heightAt(point) == 1 }
                .filterNot(seen::contains)
                .sumOf { candidate ->
                    countTrailRatings(candidate, seen + candidate)
                }

            return trails
        }

        private fun heightAt(point: Point): Int {
            return this.charForOrNull(point)?.digitToIntOrNull() ?: -1
        }

        private fun print(path: Set<Point>) {
            printMap { point, c ->
                when {
                    path.contains(point) -> printColor(c, PrintColor.GREEN)
                    else -> print(c)
                }
            }
        }
    }
}
