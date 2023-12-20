package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.PrintColor
import ch.ranil.aoc.aoc2023.Direction.*
import ch.ranil.aoc.printColor
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

class Day18 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(62, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
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
        val area = digTrench(input)
        val filling = area.getFilling()

        print(area, filling)

        return filling.size + area.trenchPoints.size
    }

    private fun digTrench(
        input: List<String>,
    ): Area {
        var topLeft = Point(0, 0)
        var bottomRight = Point(0, 0)
        val trenchPoints = mutableSetOf(Point(0, 0))
        input
            .map { it.parse() }
            .forEach { (direction, length, _) ->
                val startPoint = trenchPoints.last()
                val nextTrenchPoints = (1..length).map { steps ->
                    val nextTrenchPoint = startPoint.move(steps, direction)
                    topLeft = Point(min(topLeft.x, nextTrenchPoint.x), min(topLeft.y, nextTrenchPoint.y))
                    bottomRight = Point(max(bottomRight.x, nextTrenchPoint.x), max(bottomRight.y, nextTrenchPoint.y))
                    nextTrenchPoint
                }
                trenchPoints.addAll(nextTrenchPoints)
            }

        return Area(topLeft, bottomRight, trenchPoints)
    }

    private fun String.parse(): DigInstruction {
        val (d, l, c) = this.split(" ")
        return DigInstruction(
            direction = d.toDirection(),
            length = l.toInt(),
            colour = c.drop(2).take(6),
        )
    }

    private fun compute2(input: List<String>): Int {
        TODO()
    }

    private data class DigInstruction(
        val direction: Direction,
        val length: Int,
        val colour: String,
    )

    private fun String.toDirection(): Direction {
        return when (this) {
            "R" -> E
            "L" -> W
            "U" -> N
            "D" -> S
            else -> throw IllegalArgumentException("Unknown direction: $this")
        }
    }

    private data class Area(
        val topLeft: Point,
        val bottomRight: Point,
        val trenchPoints: Set<Point>,
    ) {
        private fun holds(point: Point): Boolean {
            return topLeft.x <= point.x &&
                topLeft.y <= point.y &&
                point.x <= bottomRight.x &&
                point.y <= bottomRight.y
        }

        private fun edgePoints(): List<Point> {
            val leftEdge = (topLeft.y..bottomRight.y).map { y -> Point(topLeft.x, y) }
            val topEdge = (topLeft.x..bottomRight.x).map { x -> Point(x, topLeft.y) }
            val rightEdge = (topLeft.y..bottomRight.y).map { y -> Point(bottomRight.x, y) }
            val bottomEdge = (topLeft.x..bottomRight.x).map { x -> Point(x, bottomRight.y) }
            return leftEdge + topEdge + rightEdge + bottomEdge
        }

        fun getSurroundingArea(): Set<Point> {
            val surroundingArea = mutableSetOf<Point>()
            edgePoints()
                .filter { it !in trenchPoints }
                .forEach { areaEdgePoint ->
                    val neighborsOfEdgePointToCheck = mutableListOf(areaEdgePoint)
                    while (neighborsOfEdgePointToCheck.isNotEmpty()) {
                        val current = neighborsOfEdgePointToCheck.removeFirst()
                        surroundingArea.add(current)
                        val candidates = current.edges()
                            .filter { it !in trenchPoints }
                            .filter { it !in surroundingArea }
                            .filter { this.holds(it) }
                        neighborsOfEdgePointToCheck.addAll(candidates)
                    }
                }
            return surroundingArea
        }

        fun getFilling(): Set<Point> {
            val horizontal = trenchPoints
                .groupBy { it.y }
                .values
                .flatMap { row ->
                    row
                        .sortedBy { it.x }
                        .getFilling { p, i -> p.move(i, E) }
                }

            val vertical = trenchPoints
                .groupBy { it.x }
                .values
                .flatMap { row ->
                    row
                        .sortedBy { it.y }
                        .getFilling { p, i -> p.move(i, S) }
                }
            return horizontal.intersect(vertical.toSet())
        }

        private fun List<Point>.getFilling(shiftPoint: (Point, Int) -> Point): Set<Point> {
            val pointsInLine = this
            val filling = mutableSetOf<Point>()
            var i = 0
            while (i < pointsInLine.size - 1) {
                val p1 = pointsInLine[i]
                val p2 = pointsInLine[i + 1]
                val delta = p1.distanceTo(p2) - 1
                val pointsBetweenP1P2 = List(delta) { shiftPoint(p1, it + 1) }
                filling.addAll(pointsBetweenP1P2)
                i++
            }
            return filling
        }
    }

    private fun print(area: Area, filling: Set<Point>) {
        (area.topLeft.y..area.bottomRight.y).forEach { y ->
            (area.topLeft.x..area.bottomRight.x).forEach { x ->
                val p = Point(x, y)
                when (p) {
                    in filling -> printColor(PrintColor.YELLOW, '#')
                    in area.trenchPoints -> printColor(PrintColor.GREEN, '#')
                    else -> print('#')
                }
            }
            println()
        }
    }
}
