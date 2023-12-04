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
            it.points
        }
    }

    private fun compute2(input: List<String>): Int {
        val cards = parseScratchCards(input)

        val totalWonCardsCount = cards.sumOf { card: Card ->
            processCard(card, cards)
        }
        return cards.size + totalWonCardsCount
    }

    private fun processCard(card: Card, cards: List<Card>): Int {
        val cardsWon = cards
            .subList(card.num, card.num + card.matchingCount)

        val cardsWonOfCardsCount = cardsWon.sumOf {
            processCard(it, cards)
        }
        return cardsWon.size + cardsWonOfCardsCount
    }

    private fun parseScratchCards(input: List<String>) = input.map { line ->
        val (gamePart, numbersPart) = line.split(":")
        val (winningPart, availablePart) = numbersPart.split("|")
        Card(
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

    data class Card(
        val num: Int,
        val winning: Set<Int>,
        val avail: Set<Int>,
    ) {
        private val matchingNumbers = winning.filter {
            avail.contains(it)
        }

        val points: Int
            get() = if (matchingNumbers.isEmpty()) {
                0
            } else {
                2.0.pow(matchingNumbers.size.toDouble() - 1).toInt()
            }

        val matchingCount: Int = matchingNumbers.size
    }
}
