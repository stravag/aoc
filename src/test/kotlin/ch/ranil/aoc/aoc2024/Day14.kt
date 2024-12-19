package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day14 : AbstractDay() {

    @Test
    fun part1Puzzle() {
        assertEquals(218965032, compute1(puzzleInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(7037, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val room = Room.parse(input)
        repeat(100) {
            room.moveRobots()
        }
        return room.score()
    }

    private fun compute2(input: List<String>): Int {
        val room = Room.parse(input)
        var i = 0
        while (!room.robotsShowTree()) {
            room.moveRobots()
            i++
        }
        room.print()
        return i
    }

    private class Room(private val robots: List<Robot>) {
        private val x: Int = 101
        private val y: Int = 103

        fun moveRobots() {
            robots.forEach { it.move(x, y) }
        }

        fun score(): Int {
            val mx = x / 2
            val my = y / 2
            val q1 = robots.count { it.p.col < mx && it.p.row < my }
            val q2 = robots.count { it.p.col < mx && it.p.row > my }
            val q3 = robots.count { it.p.col > mx && it.p.row < my }
            val q4 = robots.count { it.p.col > mx && it.p.row > my }
            return q1 * q2 * q3 * q4
        }

        fun robotsShowTree(): Boolean {
            // assume they show something when all of them are in distinct positions
            return robots.distinctBy { it.p }.size == robots.size
        }

        fun print() {
            for (y in 0..<y) {
                for (x in 0..<x) {
                    val p = Point(y, x)
                    val r = robots.count { it.p == p }
                    when (r) {
                        0 -> print(".")
                        else -> printColor(r, PrintColor.GREEN)
                    }
                }
                println()
            }
            println()
        }

        companion object {
            fun parse(input: List<String>): Room {
                val robots = input.map(Robot.Companion::parse)
                return Room(robots)
            }
        }
    }

    private data class Robot(var p: Point, val vX: Int, val vY: Int) {

        fun move(maxX: Int, maxY: Int) {
            val (y, x) = p.east(vX).south(vY)
            val wx = when {
                x < 0 -> maxX + x
                x >= maxX -> x - maxX
                else -> x
            }
            val wy = when {
                y < 0 -> maxY + y
                y >= maxY -> y - maxY
                else -> y
            }
            p = Point(wy, wx)
        }

        companion object {
            fun parse(s: String): Robot {
                val regex = """-?\d+""".toRegex()
                val (pX, pY, vX, vY) = regex.findAll(s).map { it.value.toInt() }.toList()

                return Robot(Point(pY, pX), vX, vY)
            }
        }
    }
}
