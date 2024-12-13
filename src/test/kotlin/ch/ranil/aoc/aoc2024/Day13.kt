package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.Debug.debug
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day13 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(480, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(35574, compute1(puzzleInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(80882098756071, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val games = parse(input, prizeOffset = 0)
        return games.sumOf { it.solve() }
    }

    private fun compute2(input: List<String>): Long {
        val games = parse(input, prizeOffset = 10000000000000)
        return games.sumOf { it.solve() }
    }

    private fun parse(input: List<String>, prizeOffset: Long): List<Game> {
        return input
            .chunked(4)
            .map { (a, b, prize) -> Game.parse(a, b, prize, prizeOffset) }
    }

    private class Game(
        val buttonA: Button,
        val buttonB: Button,
        val prizeX: Long,
        val prizeY: Long,
    ) {
        fun solve(): Long {
            val bPresses = (prizeX * buttonA.dY - prizeY * buttonA.dX) /
                    (buttonB.dX * buttonA.dY - buttonB.dY * buttonA.dX)

            val aPresses = (prizeX - bPresses * buttonB.dX) / buttonA.dX

            val xOk = aPresses * buttonA.dX + bPresses * buttonB.dX == prizeX
            val yOk = aPresses * buttonA.dY + bPresses * buttonB.dY == prizeY
            return if (xOk && yOk) {
                val costA = aPresses * buttonA.cost
                val costB = bPresses * buttonB.cost
                val cost = costA + costB
                debug { println("A: $aPresses | B: $bPresses | Cost: $cost") }
                cost
            } else {
                0L
            }
        }

        companion object {
            val regex = "Prize: X=([0-9]+), Y=([0-9]+)".toRegex()
            fun parse(a: String, b: String, prize: String, prizeOffset: Long): Game {
                val (prizeX, prizeY) = regex.find(prize)!!.groupValues.drop(1)
                return Game(
                    buttonA = Button.parse(a),
                    buttonB = Button.parse(b),
                    prizeX = prizeOffset + prizeX.toLong(),
                    prizeY = prizeOffset + prizeY.toLong(),
                )
            }
        }
    }

    private class Button(
        val cost: Long,
        val dX: Long,
        val dY: Long,
    ) {
        companion object {
            val regex = "Button (.?): X\\+([0-9]+), Y\\+([0-9]+)".toRegex()
            fun parse(s: String): Button {
                val (type, dX, dY) = regex.find(s)!!.groupValues.drop(1)
                val cost = when (type) {
                    "A" -> 3L
                    "B" -> 1L
                    else -> error("Unexpected button type: $type")
                }
                return Button(cost, dX.toLong(), dY.toLong())
            }
        }
    }
}
