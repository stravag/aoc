package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.math.pow
import kotlin.test.assertEquals

class Day04 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(13, compute1(testInput))
        assertEquals(26218, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(30, compute2(testInput))
        assertEquals(9997537, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return parseScratchCards(input).sumOf {
            it.getPoints()
        }
    }

    private fun compute2(input: List<String>): Int {
        val scratchCards = parseScratchCards(input)

        return scratchCards.sumOf { scratchCard: ScratchCard ->
            processCard(scratchCard, scratchCards)
        }
    }

    private fun processCard(card: ScratchCard, pileOfCards: List<ScratchCard>): Int {
        if (card.num == pileOfCards.size) return 1

        return pileOfCards
            .subList(card.num, card.num + card.matchingCount())
            .sumOf { processCard(it, pileOfCards) } + 1
    }

    private fun parseScratchCards(input: List<String>) = input.map { line ->
        val (gamePart, numbersPart) = line.split(":")
        val (winningPart, availablePart) = numbersPart.split("|")
        ScratchCard(
            num = gamePart
                .split(" ")
                .filter { it.isNotBlank() }[1]
                .toInt(),
            winning = winningPart
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.toInt() }
                .toSet(),
            avail = availablePart
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.toInt() }
                .toSet(),
        )
    }

    data class ScratchCard(
        val num: Int,
        val winning: Set<Int>,
        val avail: Set<Int>,
    ) {
        fun getPoints(): Int {
            val matchingNumbers = winning.intersect(avail)
            return if (matchingNumbers.isEmpty()) {
                0
            } else {
                2.0.pow(matchingNumbers.size.toDouble() - 1).toInt()
            }
        }

        fun matchingCount(): Int {
            return winning.intersect(avail.toSet()).count()
        }
    }

    @Test
    fun pointTest() {
        assertEquals(0, ScratchCard(0, setOf(0), setOf(1)).getPoints())
        assertEquals(1, ScratchCard(0, setOf(1), setOf(1)).getPoints())
        assertEquals(2, ScratchCard(0, setOf(1, 2), setOf(1, 2)).getPoints())
        assertEquals(4, ScratchCard(0, setOf(1, 2, 3), setOf(1, 2, 3)).getPoints())
        assertEquals(8, ScratchCard(0, setOf(1, 2, 3, 4), setOf(1, 2, 3, 4)).getPoints())
    }
}
