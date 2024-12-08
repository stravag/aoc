package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day03 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(4361, compute1(testInput))
        assertEquals(544664, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(467835, compute2(testInput))
        assertEquals(84495585, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val (partNumbers, symbols) = parseSchematics(input)
        val numbers = partNumbers.filter { (_, partPositions) ->
            symbols.any { (_, symbolPosition) ->
                partPositions.any { it.isAdjacentTo(symbolPosition) }
            }
        }.map { it.number }
        return numbers.sum()
    }

    private fun compute2(input: List<String>): Int {
        val (partNumbers, symbols) = parseSchematics(input)
        val partNumberLookup = partNumbers.flatMap { partNumber ->
            partNumber.positions.map { pos -> pos to partNumber }
        }.toMap()

        val ratios = symbols
            .filter { it.char == '*' }
            .mapNotNull { symbol ->
                val numbersForSymbol = symbol.position
                    .edges()
                    .mapNotNull { partNumberLookup[it] }
                    .distinct()
                if (numbersForSymbol.size == 2) {
                    numbersForSymbol.fold(1) { acc, partNumber -> acc * partNumber.number }
                } else {
                    null
                }
            }
        return ratios.sum()
    }

    private fun parseSchematics(input: List<String>): Pair<List<PartNumber>, List<Symbol>> {
        val partNumbers = mutableListOf<PartNumber>()
        val symbols = mutableListOf<Symbol>()
        input.forEachIndexed { y, line ->
            Regex("[0-9]+|[^0-9.]").findAll(line).forEach { r ->
                r.groups.filterNotNull().forEach { g ->
                    if (g.value.first().isDigit()) {
                        val positionsOfNum = g.range.map { x -> Point(x, y) }
                        partNumbers.add(PartNumber(g.value.toInt(), positionsOfNum))
                    } else {
                        val positionOfSymbol = Point(g.range.first, y)
                        symbols.add(Symbol(g.value.single(), positionOfSymbol))
                    }
                }
            }
        }
        return partNumbers to symbols
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