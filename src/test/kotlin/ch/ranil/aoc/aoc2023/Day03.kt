package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day03 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(4361, compute1(testInput))
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part1Alternate() {
        assertEquals(4361, compute1Alt(testInput))
        assertEquals(0, compute1Alt(puzzleInput))
    }

    @Test
    fun part1AlternateTests() {
        assertEquals(listOf(123), parseSchematics(".123".lines()).partNumbers.map { it.number })
        assertEquals(listOf(123), parseSchematics(".123$".lines()).partNumbers.map { it.number })
        assertEquals(listOf(12, 34), parseSchematics(".12.34.".lines()).partNumbers.map { it.number })
        assertEquals(listOf(123), parseSchematics("123".lines()).partNumbers.map { it.number })
    }

    @Test
    fun part2() {
        assertEquals(0, compute2(testInput))
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val numbers = input.flatMapIndexed { y, line ->
            line.flatMapIndexed { x, c ->
                getNumbersForPosition(c, input, Point(x, y))
            }
        }
        return numbers.sum()
    }

    private fun compute1Alt(input: List<String>): Int {
        val schematic = parseSchematics(input)
        val partNumbers = schematic.partNumbers.filter { (_, partPositions) ->
            schematic.symbols.any { (_, symbolPosition) ->
                partPositions.any { it.isAdjacentTo(symbolPosition) }
            }
        }
        return partNumbers.sumOf { it.number }
    }

    private fun parseSchematics(input: List<String>): Schematic {
        val schematic = Schematic()
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                val pos = Point(x, y)
                if (char.isDigit()) {
                    schematic.trackNumber(char.digitToInt(), pos)
                } else if (char == '.') {
                    schematic.completeNumber()
                } else {
                    schematic.trackSymbol(char, pos)
                }
            }
            schematic.completeNumber()
        }
        return schematic
    }

    private fun getNumbersForPosition(
        c: Char,
        input: List<String>,
        pos: Point,
    ) = when {
        c.isDigit() || c == '.' -> emptyList()
        else -> findNumberForSymbol(input, pos)
    }

    private fun findNumberForSymbol(input: List<String>, point: Point): List<Long> {
        val topRow = input.getOrNull(point.y - 1).orEmpty()
        val currRow = input.getOrNull(point.y).orEmpty()
        val bottomRow = input.getOrNull(point.y + 1).orEmpty()

        var topNums = listOfNotNull(searchNumberInStringAtPos(topRow, point.x, Direction.BOTH))
        // if nothing straight above, check diagonal
        if (topNums.isEmpty()) {
            topNums = listOfNotNull(
                searchNumberInStringAtPos(topRow, point.x, Direction.LEFT),
                searchNumberInStringAtPos(topRow, point.x, Direction.RIGHT),
            )
        }

        var bottomNums = listOfNotNull(searchNumberInStringAtPos(bottomRow, point.x, Direction.BOTH))
        // if nothing straight below, check diagonal
        if (bottomNums.isEmpty()) {
            bottomNums = listOfNotNull(
                searchNumberInStringAtPos(bottomRow, point.x, Direction.LEFT),
                searchNumberInStringAtPos(bottomRow, point.x, Direction.RIGHT),
            )
        }

        val inlineNums = listOfNotNull(
            // top row
            searchNumberInStringAtPos(currRow, point.x, Direction.LEFT),
            searchNumberInStringAtPos(currRow, point.x, Direction.RIGHT),
        )
        return topNums + inlineNums + bottomNums
    }

    private fun searchNumberInStringAtPos(string: String, idx: Int, direction: Direction): Long? {
        fun find(range: IntProgression, combination: (String, Char) -> String): Long? {
            var numberStr: String? = null
            for (x in range) {
                val char = string.getOrNull(x)
                if (char != null && char.isDigit()) {
                    numberStr = combination(numberStr.orEmpty(), char)
                } else {
                    break
                }
            }
            return numberStr?.toLong()
        }

        return when (direction) {
            Direction.LEFT -> find((0..<idx).reversed()) { s, c -> "$c$s" }
            Direction.RIGHT -> find(idx + 1..<string.length) { s, c -> "$s$c" }
            Direction.BOTH -> {
                val left = find((0..idx).reversed()) { s, c -> "$c$s" }
                // if we don't find anything at position idx, abort, diagonal covered differently
                if (left != null) {
                    val right = find(idx + 1..<string.length) { s, c -> "$s$c" }
                    "$left${right ?: ""}".toLongOrNull()
                } else {
                    null
                }
            }
        }
    }

    enum class Direction {
        LEFT, RIGHT, BOTH
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }

    class Schematic {
        val partNumbers: MutableList<PartNumber> = mutableListOf()
        val symbols: MutableList<Symbol> = mutableListOf()

        private var positions: List<Point> = emptyList()
        private var number: Int = 0

        fun trackSymbol(symbol: Char, pos: Point) {
            symbols.add(Symbol(symbol, pos))
        }

        fun trackNumber(digit: Int, pos: Point) {
            number = number * 10 + digit
            positions += pos
        }

        fun completeNumber() {
            if (positions.isNotEmpty()) {
                partNumbers.add(
                    PartNumber(number, positions)
                )
                number = 0
                positions = emptyList()
            }
        }
    }

    data class PartNumber(
        val number: Int,
        val positions: List<Point>,
    )

    data class Symbol(
        val char: Char,
        val position: Point,
    )
}