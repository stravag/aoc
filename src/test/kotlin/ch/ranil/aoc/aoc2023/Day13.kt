package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day13 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(405, compute1(testInput))
    }

    @Test
    fun part1Test2() {
        assertEquals(
            0, compute1(
                """
            #....#.##
            ...##.#.#
            ###..#...
            #....#.##
            ######...
            ..##.##..
            ..#...#.#
            ..#...#.#
            ..##.##..
            ######...
            #....#.##
            ###..#...
            ...##.#.#
            #...##.##
            #...##.##
        """.trimIndent().lines()
            )
        )
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
        return splitPatterns(input)
            .sumOf { pattern ->
                val reflectionPoint = pattern.findReflectionPoint()
                reflectionPoint
            }
    }

    private fun Pattern.findReflectionPoint(): Int {
        fun scan(lines: List<String>): Int {
            var reflectionPoint = 0
            while (reflectionPoint + 1 < lines.size) {
                val p1 = lines[reflectionPoint]
                val p2 = lines[reflectionPoint + 1]
                if (p1 == p2) {
                    // check outwards to see if everything reflects
                    var i = 1
                    while (reflectionPoint - i >= 0 && reflectionPoint + i + 1 < lines.size) {
                        val q1 = lines[reflectionPoint - i]
                        val q2 = lines[reflectionPoint + i + 1]
                        if (q1 != q2) return 0 // not a perfect reflection
                        i++
                    }
                    return reflectionPoint + 1
                }
                reflectionPoint++
            }
            return 0
        }

        // scan horizontally
        val horizontalReflection = scan(this)
        if (horizontalReflection > 0) return horizontalReflection * 100

        val verticalReflection = scan(this.columns())
        if (verticalReflection > 0) return verticalReflection

        println("No reflection found in pattern:\n${this.joinToString("\n")}")
        return 0
    }

    fun compute2(input: List<String>): Long {
        input.forEach {

        }
        return 0
    }

    private fun splitPatterns(input: List<String>): List<Pattern> {
        val patterns = mutableListOf<List<String>>()
        var i = 0
        while (i < input.size) {
            val pattern = input
                .drop(i)
                .takeWhile { it.isNotBlank() }
            patterns += pattern
            i += pattern.size + 1
        }
        return patterns
    }

}

private typealias Pattern = List<String>

private fun Pattern.columns(): List<String> {
    val columns = mutableListOf<String>()
    for (i in this.first().indices) {
        columns += this.joinToString("") { it[i].toString() }
    }
    return columns
}
