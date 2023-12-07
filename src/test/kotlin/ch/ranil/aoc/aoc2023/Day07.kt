package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.aoc2023.Day07.Hand.Companion.toHand
import ch.ranil.aoc.aoc2023.Day07.HandType.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day07 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(6440, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(248559379, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    @Test
    fun sortTests() {
        assertTrue(Card('T') < Card('K'))
        val twoPairTen = "KTJJT 0".toHand()
        val twoPairKing = "KK677 0".toHand()
        assertTrue(twoPairTen < twoPairKing)
    }

    private fun compute1(input: List<String>): Long {
        val hands = input
            .map { it.toHand() }
            .sorted()
        return hands
            .foldIndexed(0L) { index, acc, hand ->
                acc + (index + 1) * hand.bid
            }
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }

    data class Hand(
        val cards: List<Card>,
        val type: HandType,
        val bid: Int,
    ) : Comparable<Hand> {

        override fun compareTo(other: Hand): Int {
            val handComp = type.compareTo(other.type)
            if (handComp != 0) return handComp

            for ((thisCard, otherCard) in cards.zip(other.cards)) {
                val cardComp = thisCard.compareTo(otherCard)
                if (cardComp != 0) return cardComp
            }

            return 0
        }

        override fun toString(): String {
            val cardsString = cards.joinToString("") { it.v.toString() }
            return "Hand(cards=$cardsString, type=$type, bid=$bid)"
        }

        companion object {
            fun String.toHand(): Hand {
                val (cardsPart, bidPart) = this.split(" ")
                val cards = cardsPart.map { Card(it) }
                val bid = bidPart.toInt()
                val type = cards.handType()
                return Hand(cards, type, bid)
            }

            private fun List<Card>.handType(): HandType {
                val map = mutableMapOf<Char, Int>()
                this.forEach {
                    val c = map[it.v] ?: 0
                    map[it.v] = c + 1
                }
                return when (map.size) {
                    1 -> FIVE_OF_A_KIND
                    2 -> {
                        if (map.values.any { cardCount -> cardCount == 4 }) {
                            FOUR_OF_A_KIND
                        } else {
                            FULL_HOUSE
                        }
                    }

                    else -> {
                        val hasThreePairs = map.values.any { cardCount -> cardCount == 3 }
                        val pairCount = map.values.filter { cardCount -> cardCount == 2 }.size
                        when {
                            hasThreePairs -> THREE_OF_A_KIND
                            pairCount == 2 -> TWO_PAIR
                            pairCount == 1 -> ONE_PAIR
                            else -> HIGH_CARD
                        }
                    }
                }
            }
        }
    }

    enum class HandType {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND,
    }

    data class Card(val v: Char) : Comparable<Card> {
        private val cardVal = cardMap.getValue(v)

        override fun compareTo(other: Card): Int {
            return cardVal.compareTo(other.cardVal)
        }

        companion object {
            val cardMap = mapOf(
                'A' to 14,
                'K' to 13,
                'Q' to 12,
                'J' to 11,
                'T' to 10,
                '9' to 9,
                '8' to 8,
                '7' to 7,
                '6' to 6,
                '5' to 5,
                '4' to 4,
                '3' to 3,
                '2' to 2,
            )
        }
    }
}
