package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.isEven
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day10 : AbstractDay() {

    private var inputMap = emptyList<String>()
    private var loopPositions = mutableSetOf<Point>()
    private var startPipe = 'S'

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
            18, compute2(
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
            """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part2Test2() {
        assertEquals(
            8, compute2(
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
            """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part2Test3() {
        assertEquals(
            10, compute2(
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
            """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part2Puzzle() {
        startPipe = '|'
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        inputMap = input
        loopPositions = mutableSetOf()

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
        loopPositions = mutableSetOf()

        traverseLoop { a, b -> loopPositions.addAll(listOf(a, b)) }

        val candidatePositions = inputMap.flatMapIndexed { y, s ->
            s.mapIndexed { x, _ ->
                Point(x, y)
            }
        }

        val pointsInside = (candidatePositions - loopPositions)
            .filterNot { p ->
                isConnectedToEdge(p)
            }

        printMap(pointsInside)
        return pointsInside.count()
    }

    private fun findPathToEdge(
        rootPoint: Point,
    ): Set<Point> {
        val seen = mutableSetOf(rootPoint)
        val queue = mutableListOf(rootPoint)
        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            if (p.isEdgeOfMap()) {
                return seen
            }

            val candidates = p.edges().flatMap { edge ->
                if (loopPositions.contains(edge)) {
                    // try ride along pipe
                    var riding = edge
                    if (edge.x == p.x && edge.y < p.y) { // north
                        //while ()
                    } else if (edge.x == p.x && edge.y > p.y) { // south

                    } else if (edge.x > p.x && edge.y == p.y) { // west

                    } else if (edge.x < p.x && edge.y == p.y) { // east

                    }
                    emptyList()
                } else {
                    listOf(edge)
                }
            }

            candidates
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
        val startConnectors = findStartConnectors(start)
        var (posA, posB) = startConnectors
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

    private fun isConnectedToEdge(pos: Point): Boolean {
        println("Counting loop crossings for $pos")
        var crossings = 0
        var p: Point
        var riding: Char?

        p = pos
        riding = null
        println("Riding north")
        while (!p.isEdgeOfMap()) {
            p = p.north()
            val char = getCharCleanedUp(p)
            if (!loopPositions.contains(p)) continue
            when (char) {
                '|' -> Unit
                '-' -> crossings++
                'L' -> riding = handleRiding('7', riding, char) { crossings++ }
                'J' -> riding = handleRiding('F', riding, char) { crossings++ }
                'F' -> riding = handleRiding('J', riding, char) { crossings++ }
                '7' -> riding = handleRiding('L', riding, char) { crossings++ }
            }
            println("Riding on $riding, crossings = $crossings")
        }

        if (crossings.isEven()) return true

        p = pos
        crossings = 0
        riding = null
        println("Riding south")
        while (!p.isEdgeOfMap()) {
            p = p.south()
            val char = getCharCleanedUp(p)
            if (!loopPositions.contains(p)) continue
            when (char) {
                '|' -> Unit
                '-' -> crossings++
                'L' -> riding = handleRiding('7', riding, char) { crossings++ }
                'J' -> riding = handleRiding('F', riding, char) { crossings++ }
                'F' -> riding = handleRiding('J', riding, char) { crossings++ }
                '7' -> riding = handleRiding('L', riding, char) { crossings++ }
            }
            println("Riding on $riding, crossings = $crossings")
        }

        if (crossings.isEven()) return true

        crossings = 0
        p = pos
        riding = null
        println("Riding east")
        while (!p.isEdgeOfMap()) {
            p = p.east()
            val char = getCharCleanedUp(p)
            if (!loopPositions.contains(p)) continue
            when (char) {
                '|' -> crossings++
                '-' -> Unit
                'L' -> riding = handleRiding('7', riding, char) { crossings++ }
                'F' -> riding = handleRiding('J', riding, char) { crossings++ }
                'J' -> riding = handleRiding('F', riding, char) { crossings++ }
                '7' -> riding = handleRiding('L', riding, char) { crossings++ }
            }
            println("Riding on $riding, crossings = $crossings")
        }

        if (crossings.isEven()) return true

        crossings = 0
        p = pos
        riding = null
        println("Riding west")
        while (!p.isEdgeOfMap()) {
            p = p.west()
            val char = getCharCleanedUp(p)
            if (!loopPositions.contains(p)) continue
            when (char) {
                '|' -> crossings++
                '-' -> Unit
                'L' -> riding = handleRiding('7', riding, char) { crossings++ }
                'F' -> riding = handleRiding('J', riding, char) { crossings++ }
                'J' -> riding = handleRiding('F', riding, char) { crossings++ }
                '7' -> riding = handleRiding('L', riding, char) { crossings++ }
            }
            println("Riding on $riding, crossings = $crossings")
        }

        return crossings.isEven()
    }

    private fun handleRiding(
        matchingCrossing: Char,
        riding: Char?,
        char: Char,
        increaseCrossings: () -> Unit
    ) = if (riding == matchingCrossing) {
        // rode up to a crossing
        increaseCrossings()
        null
    } else {
        // continue riding
        char
    }

    private fun getChar(pos: Point): Char {
        val c = inputMap[pos.y][pos.x]
        return if (c == 'S') startPipe else c
    }

    private fun getCharCleanedUp(pos: Point): Char {
        return if (loopPositions.contains(pos)) {
            val c = inputMap[pos.y][pos.x]
            if (c == 'S') startPipe else c
        } else {
            '.'
        }
    }

    private fun printMap(pointsInside: Collection<Point>) {
        inputMap.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                val p = Point(x, y)
                if (loopPositions.contains(p)) {
                    val red = "\u001b[31m"
                    val reset = "\u001b[0m"
                    print(red + c + reset)
                } else if (pointsInside.contains(p)) {
                    val yellow = "\u001b[33m"
                    val reset = "\u001b[0m"
                    print("${yellow}I$reset")
                } else {
                    print(".")
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
