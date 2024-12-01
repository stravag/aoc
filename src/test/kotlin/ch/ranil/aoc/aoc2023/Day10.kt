package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.Point
import ch.ranil.aoc.isEven
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

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
        assertEquals(
            18,
            compute2(
                """
                ............
                .S--------7.
                .|F------7|.
                .||......||.
                .||......||.
                .||......||.
                .|L--7F--J|.
                .|...||...|.
                .|...||...|.
                .|...||...|.
                .L---JL---J.
                ............
                """.trimIndent().lines(),
            ),
        )
    }

    @Test
    fun part2Test2() {
        /*

             F----7F7F7F7F-7
             |F--7||||||||FJ
             || FJ||||||||L7
            FJL7L7LJLJ||LJIL-7
            L--J L7   LJF7F-7L7
                F-JIIF7FJ|L7L7L7
                L7IF7||L7|IL7L7|
                 |FJLJ|FJ|F7| LJ
                FJL-7 || ||||
                L---J LJ LJLJ

         */
        assertEquals(
            8,
            compute2(
                """
                .F----7F7F7F7F-7....
                .|F--7||||||||FJ....
                .||.FJ||||||||L7....
                FJL7L7LJLJ||LJ.L-7..
                L--J.L7...LJS7F-7L7.
                ....F-J..F7FJ|L7L7L7
                ....L7.F7||L7|.L7L7|
                .....|FJLJ|FJ|F7|.LJ
                ....FJL-7.||.||||...
                ....L---J.LJ.LJLJ...
                """.trimIndent().lines(),
            ),
        )
    }

    @Test
    fun part2Test3() {
        assertEquals(
            10,
            compute2(
                """
                FF7FSF7F7F7F7F7F---7
                L|LJ||||||||||||F--J
                FL-7LJLJ||||||LJL-77
                F--JF--7||LJLJ7F7FJ-
                L---JF-JLJ.||-FJLJJ7
                |F|F-JF---7F7-L7L|7|
                |FFJF7L7F-JF7|JL---7
                7-L-JL7||F7|L7F-7F7|
                L.L7LFJ|||||FJL7||LJ
                L7JLJL-JLJLJL--JLJ.L
                """.trimIndent().lines(),
            ),
        )
    }

    @Test
    fun part2Test4() {
        inputMap = """
F-7
| |               
L-J
        """.trimIndent().lines()

        val loopPoints = inputMap.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                if (c != ' ') Point(x, y) to c else null
            }
        }.toMap()

        assertFalse(isConnectedToEdge(Point(1, 1), loopPoints))
    }

    @Test
    fun part2Test5() {
        inputMap = """
---------------------------------------------------------------------------------------------------------------------
L--7|L----7LJF--7FJ||||LJL-7L---JLJ F7 |L-----JLJ|LJFJ|||LJ|||FJLJ  LJF---J|F7FJL------JLJF7L-JF-------7F7F7|L----7LJ               
---------------------------------------------------------------------------------------------------------------------
        """.trimIndent().lines()

        val loopPoints = inputMap.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                if (c != ' ') Point(x, y) to c else null
            }
        }.toMap()

        assertFalse(isConnectedToEdge(Point(66, 1), loopPoints))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(303, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        inputMap = input

        var distance = 1
        val start = findStart()
        var prevA = start
        var prevB = start
        val startConnectors = findStartConnectors(start)
        var (posA, posB) = startConnectors
        do {
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
        // if pipe cross count even times: outside
        // if pipe cross count odd times: inside
        // if pipe ends face same directions: no crossing (L--J / F--7)
        // if pipe ends face opposite directions: no crossing (F--J / L--7)
        // replace S with actual pipe piece (hope note necessary)
        inputMap = input
        val loop = traverseLoop()

        val candidatePositions = inputMap.flatMapIndexed { y, s ->
            s.mapIndexed { x, _ ->
                Point(x, y)
            }
        }

        val pointsInside = (candidatePositions - loop.keys)
            .filterNot { p ->
                isConnectedToEdge(p, loop)
            }

        println("------------------------------------------------------------------------------------------")
        printMap(pointsInside, loop)
        println("------------------------------------------------------------------------------------------")
        return pointsInside.count()
    }

    private fun traverseLoop(): Map<Point, Char> {
        val loopPositions = mutableMapOf<Point, Char>()
        val start = findStart()

        var prevA = start
        var prevB = start
        val startConnectors = findStartConnectors(start)
        val startPipe = resolveStartPipe(start, startConnectors)
        loopPositions[start] = startPipe
        var (posA, posB) = startConnectors
        do {
            loopPositions[posA] = getChar(posA)
            loopPositions[posB] = getChar(posB)
            val nextA = posA.next(prevA)
            val nextB = posB.next(prevB)
            prevA = posA
            prevB = posB
            posA = nextA
            posB = nextB
        } while (posA != posB)
        loopPositions[posA] = getChar(posA)
        loopPositions[posB] = getChar(posB)
        return loopPositions
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

    private fun findConnectingPoints(pos: Point, charResolver: (Point) -> Char = ::getChar): Connections? {
        return when (charResolver(pos)) {
            '|' -> Connections(pos.north(), pos.south())
            '-' -> Connections(pos.east(), pos.west())
            'L' -> Connections(pos.north(), pos.east())
            'J' -> Connections(pos.north(), pos.west())
            '7' -> Connections(pos.south(), pos.west())
            'F' -> Connections(pos.south(), pos.east())
            else -> null
        }
    }

    private fun resolveStartPipe(start: Point, startConnections: Connections): Char {
        return listOf('|', '-', 'L', 'J', '7', 'F').single { c ->
            val points = requireNotNull(findConnectingPoints(start) { c })
            startConnections.anyIs(points.a) && startConnections.anyIs(points.b)
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

    private fun isConnectedToEdge(pos: Point, loop: Map<Point, Char>): Boolean {
        fun isConnectedToEdge(wall: Char, slip: Char, step: (Point) -> Point): Unit? {
            var crossings = 0
            var p = pos
            var riding: Char? = null
            while (!p.isEdgeOfMap()) {
                p = step(p)
                val char = getCharCleanedUp(p, loop)
                if (!loop.contains(p)) continue
                val (newCrossings, newRiding) = when (char) {
                    slip -> crossings to riding
                    wall -> crossings + 1 to null
                    else -> {
                        val crossedLoop = when (char) {
                            'L' -> riding == '7'
                            'F' -> riding == 'J'
                            'J' -> riding == 'F'
                            '7' -> riding == 'L'
                            else -> throw IllegalArgumentException("something went wrong")
                        }
                        if (crossedLoop) {
                            crossings + 1 to null
                        } else {
                            if (riding == null) {
                                crossings to char
                            } else {
                                crossings to null
                            }
                        }
                    }
                }
                crossings = newCrossings
                riding = newRiding
            }
            return if (crossings.isEven()) null else Unit
        }

        isConnectedToEdge(wall = '-', slip = '|') { it.north() } ?: return true
        isConnectedToEdge(wall = '-', slip = '|') { it.south() } ?: return true
        isConnectedToEdge(wall = '|', slip = '-') { it.east() } ?: return true
        isConnectedToEdge(wall = '|', slip = '-') { it.west() } ?: return true
        return false
    }

    private fun getChar(pos: Point): Char {
        return inputMap[pos.y][pos.x]
    }

    private fun getCharCleanedUp(pos: Point, loop: Map<Point, Char>): Char {
        return loop[pos] ?: '.'
    }

    private fun printMap(pointsInside: Collection<Point>, loopPositions: Map<Point, Char>) {
        inputMap.forEachIndexed { y, s ->
            s.forEachIndexed { x, _ ->
                val p = Point(x, y)
                val pipe = loopPositions[p]
                if (pipe != null) {
                    val red = "\u001b[31m"
                    val reset = "\u001b[0m"
                    print(red + pipe + reset)
                } else if (pointsInside.contains(p)) {
                    val yellow = "\u001b[33m"
                    val reset = "\u001b[0m"
                    print("${yellow}I$reset")
                } else {
                    print(" ")
                }
            }
            println()
        }
    }

    private data class Connections(val a: Point, val b: Point) {
        fun anyIs(pos: Point) = a == pos || b == pos
    }
}
