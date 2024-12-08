package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.aoc2023.Day07.Card.Companion.toCard1
import ch.ranil.aoc.aoc2023.Day07.Card.Companion.toCard2
import ch.ranil.aoc.aoc2023.Day07.Hand.Companion.toHand
import ch.ranil.aoc.aoc2023.Day07.Hand.Companion.toHand2
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
        assertEquals(5905, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(249631254, compute2(puzzleInput))
    }

    @Test
    fun sortTests() {
        assertTrue('J'.toCard1() > 'T'.toCard1())
        assertTrue('J'.toCard2() < 'T'.toCard2())
        val twoPairTen = "KTJJT 0".toHand()
        val twoPairKing = "KK677 0".toHand()
        assertTrue(twoPairTen < twoPairKing)
    }

    @Test
    fun comparisionTests() {
        assertTrue("JJJJJ 0".toHand2() < "JJJJ2 0".toHand2())
        assertTrue("JKKK2 0".toHand2() < "QQQQ2 0".toHand2())
    }

    @Test
    fun elevationTests() {
        fun assertElevation(type: HandType, s: String) = assertEquals(type, s.toHand2().type)

        assertElevation(HIGH_CARD, "23456 0")
        assertElevation(ONE_PAIR, "2345J 0")

        assertElevation(ONE_PAIR, "23455 0")
        assertElevation(THREE_OF_A_KIND, "234JJ 0")
        assertElevation(THREE_OF_A_KIND, "2245J 0")

        assertElevation(TWO_PAIR, "22455 0")
        assertElevation(FOUR_OF_A_KIND, "224JJ 0")
        assertElevation(FULL_HOUSE, "22J44 0")

        assertElevation(THREE_OF_A_KIND, "22234 0")
        assertElevation(FOUR_OF_A_KIND, "222J4 0")
        assertElevation(FOUR_OF_A_KIND, "JJJ23 0")
        assertElevation(FIVE_OF_A_KIND, "222JJ 0")

        assertElevation(FULL_HOUSE, "22233 0")
        assertElevation(FIVE_OF_A_KIND, "222JJ 0")
        assertElevation(FIVE_OF_A_KIND, "JJJ22 0")

        assertElevation(FOUR_OF_A_KIND, "22223 0")
        assertElevation(FIVE_OF_A_KIND, "2222J 0")
        assertElevation(FIVE_OF_A_KIND, "JJJJ2 0")

        assertElevation(FIVE_OF_A_KIND, "JJJJJ 0")
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

    private fun compute2(input: List<String>): Long {
        val hands = input
            .map { it.toHand2() }
            .sorted()
        return hands
            .foldIndexed(0L) { index, acc, hand ->
                acc + (index + 1) * hand.bid
            }
    }

    data class Hand(
        val cards: List<Card>,
        val type: HandType,
        val bid: Int,
        val charMap: Map<Char, Int>,
    ) : Comparable<Hand> {

        fun elevate(): Hand {
            val jokerCount = charMap.getOrDefault('J', 0)
            val elevatedType = when (type) {
                HIGH_CARD -> when (jokerCount) {
                    1 -> ONE_PAIR
                    else -> type
                }

                ONE_PAIR -> when (jokerCount) {
                    1, 2 -> THREE_OF_A_KIND
                    else -> type
                }

                TWO_PAIR -> when (jokerCount) {
                    2 -> FOUR_OF_A_KIND
                    1 -> FULL_HOUSE
                    else -> type
                }

                THREE_OF_A_KIND -> when (jokerCount) {
                    2 -> FIVE_OF_A_KIND
                    1, 3 -> FOUR_OF_A_KIND
                    else -> type
                }

                FULL_HOUSE -> when (jokerCount) {
                    2, 3 -> FIVE_OF_A_KIND
                    else -> type
                }

                FOUR_OF_A_KIND -> when {
                    jokerCount > 0 -> FIVE_OF_A_KIND
                    else -> type
                }

                FIVE_OF_A_KIND -> type
            }
            return copy(type = elevatedType)
        }

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
                val cards = cardsPart.map { it.toCard1() }
                val bid = bidPart.toInt()
                val (type, charMap) = cards.handType()
                return Hand(cards, type, bid, charMap)
            }

            fun String.toHand2(): Hand {
                val (cardsPart, bidPart) = this.split(" ")
                val cards = cardsPart.map { it.toCard2() }
                val bid = bidPart.toInt()
                val (type, charMap) = cards.handType()
                val hand = Hand(cards, type, bid, charMap)
                return hand.elevate()
            }

            private fun List<Card>.handType(): Pair<HandType, Map<Char, Int>> {
                val map = this.groupBy { it.v }.mapValues { it.value.size }
                val type = when (map.size) {
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
                return type to map
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

    data class Card(val v: Char, private val cardVal: Int) : Comparable<Card> {
        override fun compareTo(other: Card): Int {
            return cardVal.compareTo(other.cardVal)
        }

        companion object {
            fun Char.toCard1(): Card = Card(this, cardMap1.getValue(this))
            fun Char.toCard2(): Card = Card(this, cardMap2.getValue(this))
            private val cardMap1 = "23456789TJQKA".mapIndexed { i, c -> c to i }.toMap()
            private val cardMap2 = "J23456789TQKA".mapIndexed { i, c -> c to i }.toMap()
        }
    }
}
