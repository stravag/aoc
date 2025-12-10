package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.Direction
import ch.ranil.aoc.common.types.Point
import ch.ranil.aoc.common.types.Rect
import ch.ranil.aoc.common.uniquePairs
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day09 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(50, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(4750297200, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val points = input.map(::toPoint)
        val largestArea = points
            .uniquePairs()
            .map { (a, b) -> Rect(a, b) }
            .maxOf {
                Debug.debug {
                    print(points, it)
                }
                it.area
            }

        return largestArea
    }

    private fun toPoint(string: String): Point {
        val (col, row) = string.split(",").map { it.toInt() }
        return Point(row, col)
    }

    private fun print(points: List<Point>, rect: Rect) {
        val minRow = points.minOf { it.row } - 1
        val minCol = points.minOf { it.col } - 2
        val maxRow = points.maxOf { it.row } + 1
        val maxCol = points.maxOf { it.col } + 2
        println("${rect.p1} -> ${rect.p2} = ${rect.area}")
        for (row in minRow..maxRow) {
            for (col in minCol..maxCol) {
                when (Point(row, col)) {
                    in rect.corners -> printColor("O", PrintColor.RED)
                    in rect -> printColor("O", PrintColor.YELLOW)
                    in points -> printColor("#", PrintColor.GREEN)
                    else -> print(".")
                }
            }
            println()
        }
    }

    private fun compute2(input: List<String>): Long {
        val polygonCorners = input.map(::toPoint)
        val polygonEdges = polygonCorners
            .plus(polygonCorners.first())
            .zipWithNext()
            .map { (a, b) -> Line(a, b) }
        polygonCorners
            .uniquePairs()
            .map(::Rect)
            .filterIndexed { i, rect ->
                val rectEdges = setOf(
                    Line(rect.topLeft, rect.topRight),
                    Line(rect.topRight, rect.bottomRight),
                    Line(rect.bottomRight, rect.bottomLeft),
                    Line(rect.bottomLeft, rect.topLeft),
                )
                val overlaps = rectEdges
                    .any { rectEdge -> polygonEdges.any { polygonEdge -> polygonEdge.crosses(rectEdge) } }
                Debug.debug {
                    println("$rect overlaps = $overlaps")
                    print2(polygonCorners, polygonEdges, rectEdges)
                    println("Area = ${rect.area}")
                    println("---------------------------------------------------------------")
                }
                !overlaps
            }
            .maxOf { it.area }
        return input.size.toLong()
    }

    private fun print2(points: List<Point>, polygonEdges: List<Line>, rectEdges: Set<Line>) {
        val rectCorners = rectEdges.flatMap { setOf(it.a, it.b) }
        val minRow = points.minOf { it.row } - 1
        val minCol = points.minOf { it.col } - 2
        val maxRow = points.maxOf { it.row } + 1
        val maxCol = points.maxOf { it.col } + 2
        for (row in minRow..maxRow) {
            for (col in minCol..maxCol) {
                val p = Point(row, col)
                when {
                    p in rectCorners -> printColor("O", PrintColor.BLUE)
                    rectEdges.any { p in it } -> printColor("O", PrintColor.YELLOW)
                    p in points -> printColor("#", PrintColor.RED)
                    polygonEdges.any { p in it } -> printColor("X", PrintColor.GREEN)
                    else -> print(".")
                }
            }
            println()
        }
    }

    @Test
    fun line() {
        val line = Line(Point(5, 2), Point(3, 2))
        assertEquals(false, Point(0, 2) in line)
        val horizontalLine = Line(Point(1, 1), Point(1, 9))
        val verticalLine = Line(Point(1, 1), Point(9, 1))
        assertEquals(false, Point(0, 0) in horizontalLine)
        assertEquals(true, Point(1, 1) in horizontalLine)
        assertEquals(true, Point(1, 2) in horizontalLine)
        assertEquals(true, Point(1, 9) in horizontalLine)
        assertEquals(false, Point(0, 0) in verticalLine)
        assertEquals(true, Point(1, 1) in verticalLine)
        assertEquals(true, Point(2, 1) in verticalLine)
        assertEquals(true, Point(9, 1) in verticalLine)
    }

    @Test
    fun horizontalLineCrosses() {
        // horizontal
        val line = Line(Point(10, 1), Point(10, 10))
        val parallel = Line(Point(11, 1), Point(11, 10))
        val lShape = Line(Point(0, 1), Point(10, 1))
        val tShape1 = Line(Point(0, 5), Point(10, 5))
        val cross = Line(Point(5, 5), Point(15, 5))
        assertEquals(false, line.crosses(line))
        assertEquals(false, line.crosses(lShape))
        assertEquals(false, lShape.crosses(line))
        assertEquals(false, line.crosses(tShape1))
        assertEquals(false, tShape1.crosses(line))
        assertEquals(false, line.crosses(parallel))
        assertEquals(false, parallel.crosses(line))
        assertEquals(true, line.crosses(cross))
        assertEquals(true, cross.crosses(line))
    }

    @Test
    fun verticalLineCrosses() {
        // vertical
        val line = Line(Point(1, 10), Point(10, 10))
        val parallel = Line(Point(1, 11), Point(10, 11))
        val lShape = Line(Point(10, 10), Point(10, 15))
        val tShape = Line(Point(5, 10), Point(5, 15))
        val cross = Line(Point(5, 5), Point(5, 15))
        assertEquals(false, line.crosses(line))
        assertEquals(false, line.crosses(lShape))
        assertEquals(false, lShape.crosses(line))
        assertEquals(false, line.crosses(tShape))
        assertEquals(false, tShape.crosses(line))
        assertEquals(false, line.crosses(parallel))
        assertEquals(false, parallel.crosses(line))
        assertEquals(true, line.crosses(cross))
        assertEquals(true, cross.crosses(line))
    }

    private data class Line(val a: Point, val b: Point) {
        val direction: Direction = when {
            a.col == b.col -> Direction.N
            a.row == b.row -> Direction.E
            else -> error("$a, $b: not a vertical or horizontal line")
        }

        operator fun contains(p: Point): Boolean {
            return when (direction) {
                Direction.N -> p.col == a.col
                        && p.row >= minOf(a.row, b.row)
                        && p.row <= maxOf(a.row, b.row)

                Direction.E -> p.row == a.row
                        && p.col >= minOf(a.col, b.col)
                        && p.col <= maxOf(a.col, b.col)

                else -> error("unexpected direction")
            }
        }

        fun crosses(other: Line): Boolean {
            // Determine orientation of each line
            val thisIsHorizontal = a.row == b.row
            val otherIsHorizontal = other.a.row == other.b.row

            // Parallel lines don't cross
            if (thisIsHorizontal == otherIsHorizontal) {
                return false
            }

            // One line must be horizontal, the other vertical
            val (horizontal, vertical) = if (thisIsHorizontal) {
                this to other
            } else {
                other to this
            }

            // Get the bounds of each line
            val hRow = horizontal.a.row
            val hColMin = minOf(horizontal.a.col, horizontal.b.col)
            val hColMax = maxOf(horizontal.a.col, horizontal.b.col)

            val vCol = vertical.a.col
            val vRowMin = minOf(vertical.a.row, vertical.b.row)
            val vRowMax = maxOf(vertical.a.row, vertical.b.row)

            // Check if they intersect
            val intersects = vCol in hColMin..hColMax && hRow in vRowMin..vRowMax

            if (!intersects) {
                return false
            }

            // For a cross (not T or L), the intersection point must NOT be at the endpoints
            // The intersection point is (hRow, vCol)
            val isHorizontalEndpoint = (vCol == hColMin || vCol == hColMax)
            val isVerticalEndpoint = (hRow == vRowMin || hRow == vRowMax)

            // It's a cross only if the intersection is NOT at ANY endpoint
            return !isHorizontalEndpoint && !isVerticalEndpoint
        }
    }
}
