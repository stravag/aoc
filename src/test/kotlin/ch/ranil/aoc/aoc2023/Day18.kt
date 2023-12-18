package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.PrintColor
import ch.ranil.aoc.aoc2023.Direction.*
import ch.ranil.aoc.printColor
import org.junit.jupiter.api.Test
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
        val trenchMap = mutableSetOf(Point(0, 0))

        val area = digTrench(input, trenchMap)
        print(area, emptySet())

        val surroundingArea = area.getSurroundingArea()
        val size = (area.bottomRight.x - area.topLeft.x + 1) * (area.bottomRight.y - area.topLeft.y + 1)

        return size - surroundingArea.size
    }

    private fun digTrench(
        input: List<String>,
        trenchMap: MutableSet<Point>,
    ): Area {
        var topLeft = Point(0, 0)
        var bottomRight = Point(0, 0)
        val trenchPoints = input
            .map { it.parse() }
            .flatMap { (direction, length, _) ->
                val startPoint = trenchMap.last()
                val trenchPoints = (1..length).map { steps ->
                    startPoint.move(steps, direction)
                }
                topLeft = (trenchPoints + topLeft).min()
                bottomRight = (trenchPoints + bottomRight).max()
                trenchMap.addAll(trenchPoints)
                trenchPoints
            }.toSet()

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
    }

    private fun print(area: Area, surroundingArea: Set<Point>) {
        (area.topLeft.y..area.bottomRight.y).forEach { y ->
            (area.topLeft.x..area.bottomRight.x).forEach { x ->
                val p = Point(x, y)
                when (p) {
                    in area.trenchPoints -> printColor(PrintColor.GREEN, '#')
                    in surroundingArea -> print('.')
                    else -> print('#')
                }
            }
            println()
        }
    }
}
