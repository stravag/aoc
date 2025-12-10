package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.printlnColor
import ch.ranil.aoc.common.types.Direction
import ch.ranil.aoc.common.types.Point
import ch.ranil.aoc.common.types.Rect
import ch.ranil.aoc.common.uniquePairs
import org.junit.jupiter.api.Disabled
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
        assertEquals(24, compute2(testInput))
    }

    @Test
    @Disabled
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
            .flatMap { (a, b) -> a.allPointsTo(b) }
            .toSet()

        val maxArea = polygonCorners
            .uniquePairs()
            .map(::Rect)
            .filterIndexed { i, rect ->
                Debug.debug {
                    println("---------------------------------------------------------------")
                    println("Checking Rect #$i: $rect")
                }
                val rectInPolygon = when {
                    !rect.canShrink() -> true
                    polygonCorners.all { it in rect } -> false
                    else -> rect.shrink().corners.all { it.isInsidePolygon(polygonCorners) }
                }
                Debug.debug {
                    printlnColor(
                        "Rect in Polygon = $rectInPolygon",
                        if (rectInPolygon) PrintColor.GREEN else PrintColor.RED
                    )
                    println("Area = ${rect.area}")
                    print2(polygonCorners, polygonEdges, rect)
                }
                rectInPolygon
            }
            .maxOf { it.area }

        return maxArea
    }

    // GenAI
    fun Point.isInsidePolygon(polygon: List<Point>): Boolean {
        // Ray casting algorithm (cast a ray from the point to infinity)
        // Count how many times it crosses polygon edges
        // Odd count = inside, Even count = outside

        var crossings = 0
        val n = polygon.size

        for (i in polygon.indices) {
            val p1 = polygon[i]
            val p2 = polygon[(i + 1) % n]  // Next vertex (wraps around)

            // Check if the horizontal ray from our point crosses this edge
            // The ray goes from (row, col) to (row, +infinity)

            // Skip if edge doesn't span our row
            if ((p1.row <= row && p2.row <= row) || (p1.row > row && p2.row > row)) {
                continue
            }

            // Calculate the col (x) coordinate where the edge crosses our row
            val crossCol = p1.col + (row - p1.row).toDouble() * (p2.col - p1.col) / (p2.row - p1.row)

            // If the crossing is to the right of our point, count it
            if (crossCol > col) {
                crossings++
            }
        }

        // Odd number of crossings means we're inside
        return crossings % 2 == 1
    }

    private fun print2(points: List<Point>, polygonEdges: Set<Point>, rect: Rect) {
        val minRow = points.minOf { it.row } - 1
        val minCol = points.minOf { it.col } - 2
        val maxRow = points.maxOf { it.row } + 1
        val maxCol = points.maxOf { it.col } + 2
        for (row in minRow..maxRow) {
            for (col in minCol..maxCol) {
                val p = Point(row, col)
                when {
                    p in rect.corners -> printColor("O", PrintColor.BLUE)
                    p in rect.edgePoints -> printColor("O", PrintColor.YELLOW)
                    p in points -> printColor("#", PrintColor.RED)
                    p in polygonEdges -> printColor("X", PrintColor.GREEN)
                    else -> print(".")
                }
            }
            println()
        }
    }
}
