package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day14 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(
            136,
            compute1(
                """
                O....#....
                O.OO#....#
                .....##...
                OO.#O....O
                .O.....O#.
                O.#..O.#.#
                ..O..#O..O
                .......O..
                #....###..
                #OO..#....
                """.trimIndent().lines(),
            ),
        )
    }

    @Test
    fun part1Puzzle() {
        assertEquals(110565, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(
            """
                .....#....
                ....#...O#
                ...OO##...
                .OO#......
                .....OOO#.
                .O#...O#.#
                ....O#....
                ......OOOO
                #...O###..
                #..OO#....
            """.trimIndent().lines().toPlatform(),
            """
                O....#....
                O.OO#....#
                .....##...
                OO.#O....O
                .O.....O#.
                O.#..O.#.#
                ..O..#O..O
                .......O..
                #....###..
                #OO..#....
            """.trimIndent().lines().toPlatform().cycle(),
        )
    }

    @Test
    fun part2Puzzle() {
        assertEquals(89845, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val width = input.first().length
        var weights = 0
        for (x in 0..<width) {
            val columnWeight = input
                .map { it[x] }
                .tilt { it.sortedDescending() }
                .countLoad()
            weights += columnWeight
        }

        return weights
    }

    private fun compute2(input: List<String>): Int {
        val cycleCount = 1000000000
        val platformCache = mutableMapOf<Platform, Int>()

        var currentPlatform = input.toPlatform()
        platformCache[currentPlatform] = 0

        for (i in 0..<cycleCount) {
            currentPlatform = currentPlatform.cycle()
            if (platformCache.contains(currentPlatform)) {
                val repeatingCyclePosition = platformCache.getValue(currentPlatform)
                val cyclesNeeded = cycleCount % repeatingCyclePosition
                repeat(cyclesNeeded) {
                    currentPlatform = currentPlatform.cycle()
                }
                return currentPlatform.countPlatformLoad()
            } else {
                platformCache[currentPlatform] = i + 1
            }
        }

        return currentPlatform.countPlatformLoad()
    }

    private fun List<String>.toPlatform(): Platform = this.map { it.toList() }

    private fun Platform.cycle(): Platform {
        val tiltedPlatform = this.map { it.toMutableList() }.toMutableList()
        val width = this.first().size

        // tilt north
        for (x in 0..<width) {
            tiltedPlatform
                .map { it[x] }
                .tilt { it.sortedDescending() }
                .forEachIndexed { y, c -> tiltedPlatform[y][x] = c }
        }

        // tilt west
        tiltedPlatform.forEachIndexed { y, row ->
            val tiltedRow = row.tilt { it.sortedDescending() }
            tiltedRow
                .forEachIndexed { x, c -> tiltedPlatform[y][x] = c }
        }

        // tilt south
        for (x in 0..<width) {
            tiltedPlatform
                .map { it[x] }
                .tilt { it.sorted() }
                .forEachIndexed { y, c -> tiltedPlatform[y][x] = c }
        }

        // tilt east
        // tilt west
        tiltedPlatform.forEachIndexed { y, row ->
            row.tilt { it.sorted() }
                .forEachIndexed { x, c -> tiltedPlatform[y][x] = c }
        }

        return tiltedPlatform
    }

    private fun List<Char>.tilt(sort: (List<Char>) -> List<Char>): List<Char> {
        var i = 0
        val tilted = mutableListOf<Char>()
        while (i < size) {
            val sortedPart = sort(drop(i).takeWhile { it != '#' })
            i += sortedPart.size
            tilted.addAll(sortedPart)
            this.getOrNull(i++)?.let { rock -> tilted.add(rock) }
        }

        return tilted
    }

    private fun Platform.countPlatformLoad(): Int {
        val width = this.first().size
        var weights = 0
        for (x in 0..<width) {
            val columnWeight = this
                .map { it[x] }
                .countLoad()
            weights += columnWeight
        }
        return weights
    }

    private fun List<Char>.countLoad(): Int {
        val weight = foldIndexed(0) { index, acc, c ->
            val positionWeight = size - index
            if (c == 'O') positionWeight + acc else acc
        }
        return weight
    }
}

private typealias Platform = List<List<Char>>
