package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day10 : AbstractDay() {

    private var inputMap = emptyList<String>()
    private var startPipe = "S"

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
        assertEquals(0, compute2(test2Input))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        inputMap = input

        var distance = -1
        traverseLoop { _, _ ->
            distance++
        }

        return distance
    }


    private fun compute2(input: List<String>): Int {
        // if pipe cross count even times: outside
        // if pipe cross count odd times: inside
        // if pipe ends face same directions: no crossing (L--J / F--7)
        // if pipe ends face opposite directions: no crossing (F--J / L--7)
        // replace S with actual pipe piece (hope note necessary)
        inputMap = input

        val loopPositions = mutableSetOf<Point>()
        traverseLoop { a, b -> loopPositions.addAll(listOf(a, b)) }

        val candidatePositions = inputMap.flatMapIndexed { y, s ->
            s.mapIndexed { x, _ ->
                Point(x, y)
            }
        }

        val connectedToEdge = mutableSetOf<Point>()
        candidatePositions
            .filterNot { loopPositions.contains(it) }
            .forEach { pointToCheck ->
                if (!connectedToEdge.contains(pointToCheck)) {
                    val pathToEdge = findPathToEdge(pointToCheck, loopPositions)
                    connectedToEdge.addAll(pathToEdge)
                }
            }

        (candidatePositions - loopPositions - connectedToEdge)

        printMap(loopPositions, connectedToEdge)
        return input.size
    }

    private fun findPathToEdge(
        rootPoint: Point,
        loopPositions: Set<Point>,
    ): Set<Point> {
        val seen = mutableSetOf(rootPoint)
        val queue = mutableListOf(rootPoint)
        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            if (p.isEdgeOfMap()) {
                return seen
            }
            p.edges()
                .filterNot { loopPositions.contains(it) }
                .filterNot { seen.contains(it) }
                .forEach {
                    seen.add(it)
                    queue.add(it)
                }
        }
        return emptySet()
    }

    private fun traverseLoop(actionOnStep: (Point, Point) -> Unit) {
        val start = findStart()
        actionOnStep(start, start)

        var prevA = start
        var prevB = start
        var (posA, posB) = findStartConnectors(start)
        do {
            actionOnStep(posA, posB)
            val nextA = posA.next(prevA)
            val nextB = posB.next(prevB)
            prevA = posA
            prevB = posB
            posA = nextA
            posB = nextB
        } while (posA != posB)
        actionOnStep(posA, posB)
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

    private fun Point.isEdgeOfMap(): Boolean {
        return edges().any { !it.isContainedInMap() }
    }

    private fun loopCrossings(pos: Point): List<Int> {
        return emptyList()
    }

    private fun getChar(pos: Point): Char {
        return inputMap[pos.y][pos.x]
    }

    private fun printMap(loopPositions: Set<Point>, connectedToEdge: MutableSet<Point>) {
        inputMap.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                val p = Point(x, y)
                if (loopPositions.contains(p)) {
                    val red = "\u001b[31m"
                    val reset = "\u001b[0m"
                    print(red + c + reset)
                } else if (connectedToEdge.contains(p)) {
                    val red = "\u001b[33m"
                    val reset = "\u001b[0m"
                    print(red + c + reset)
                } else {
                    print(c)
                }
            }
            println()
        }
    }

    private fun Point.north() = Point(x, y - 1)
    private fun Point.east() = Point(x + 1, y)
    private fun Point.south() = Point(x, y + 1)
    private fun Point.west() = Point(x - 1, y)

    private data class Connections(val a: Point, val b: Point) {
        fun anyIs(pos: Point) = a == pos || b == pos
    }
}
