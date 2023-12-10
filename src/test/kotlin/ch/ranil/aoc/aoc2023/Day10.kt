package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day10 : AbstractDay() {

    private var inputMap = emptyList<String>()

    @Test
    fun part1Test() {
        assertEquals(8, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(6701, compute1(puzzleInput))
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
        inputMap = input

        var distance = 1
        val start = findStart()
        println("At position: ${getChar(start)}/${getChar(start)}")
        var prevA = start
        var prevB = start
        var (posA, posB) = requireNotNull(findConnectingPoints(start))
        do {
            println("At position: ${getChar(posA)}/${getChar(posB)}")
            val nextA = posA.next(prevA)
            val nextB = posB.next(prevB)
            prevA = posA
            prevB = posB
            posA = nextA
            posB = nextB
            distance++
        } while (posA != posB)

        return distance
    }

    private fun compute2(input: List<String>): Int {
        inputMap = input
        return input.size
    }

    private fun findStart(): Point {
        inputMap.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                if (c == 'S') return Point(x, y)
            }
        }
        throw IllegalArgumentException("no start found")
    }

    private fun Point.next(prev: Point): Point {
        val (a, b) = requireNotNull(findConnectingPoints(this))
        return if (a == prev) b else a
    }

    private fun findConnectingPoints(pos: Point): Connections? {
        return when (getChar(pos)) {
            '|' -> Connections(pos.north(), pos.south())
            '-' -> Connections(pos.east(), pos.west())
            'L' -> Connections(pos.north(), pos.east())
            'J' -> Connections(pos.north(), pos.west())
            '7' -> Connections(pos.south(), pos.west())
            'F' -> Connections(pos.south(), pos.east())
            'S' -> findStartConnectors(pos)
            else -> null
        }
    }

    private fun findStartConnectors(start: Point): Connections {
        val startConnections = start
            .edges()
            .filter { edge -> edge.isContainedInMap() }
            .filter { edge ->
                findConnectingPoints(edge)?.anyIs(start) ?: false
            }

        require(startConnections.size == 2) {
            "Start pos $start should have 2 connections but found ${startConnections.size}"
        }

        return Connections(startConnections[0], startConnections[1])
    }

    private fun Point.isContainedInMap(): Boolean {
        return x >= 0 && x < inputMap.first().length && y >= 0 && y < inputMap.size
    }

    private fun getChar(pos: Point): Char {
        return inputMap[pos.y][pos.x]
    }

    private fun Point.north() = Point(x, y - 1)
    private fun Point.east() = Point(x + 1, y)
    private fun Point.south() = Point(x, y + 1)
    private fun Point.west() = Point(x - 1, y)

    private data class Connections(val a: Point, val b: Point) {
        fun anyIs(pos: Point) = a == pos || b == pos
    }
}
