package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.min
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day22 : AbstractDay() {

    @Test
    @Disabled
    fun brickTests() {
        val horizontalBrick = Brick.parse("0,0,2~0,2,2")
        val verticalBrick = Brick.parse("1,1,8~1,1,9")

        assertTrue(Brick.parse("1,0,1~1,2,1").isOnGround)
        assertEquals(listOf(Point(1, 1, 10)), verticalBrick.pointsAbove)
        assertEquals(listOf(Point(1, 1, 7)), verticalBrick.pointsBelow)
        assertEquals(listOf(Point(0, 0, 3), Point(0, 1, 3), Point(0, 2, 3)), horizontalBrick.pointsAbove)
        assertEquals(listOf(Point(0, 0, 1), Point(0, 1, 1), Point(0, 2, 1)), horizontalBrick.pointsBelow)

        horizontalBrick.fall()
        assertTrue(horizontalBrick.isOnGround)
        assertEquals(listOf(Point(0, 0, 2), Point(0, 1, 2), Point(0, 2, 2)), horizontalBrick.pointsAbove)

        val brick0 = Brick.parse("0,0,0~0,2,0")
        val brick1 = Brick.parse("0,0,1~0,3,1")
        val brick2 = Brick.parse("0,3,2~2,3,9")
        val brickN = Brick.parse("1,0,0~2,0,0")
        val lookup = setOf(brick0, brick1, brick2, brickN).buildLookup()
        assertEquals(listOf(brick1, brick2), brick0.allBricksRestingOnTop(lookup))
    }

    @Test
    fun part1Test() {
        assertEquals(5, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(530, compute1(puzzleInput))
    }

    @Test
    @Disabled
    fun part2Test() {
        assertEquals(7, compute2(testInput))
    }

    @Test
    @Disabled
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val fallingBricks = FallingBricks(
            input
                .mapIndexed { i, s -> Brick.parse(s, i) }
                .sortedBy { it.lowZ }.toMutableSet()
        )

        fallingBricks.settleBricks()
        return fallingBricks.countRemovableBricks()
    }

    private fun compute2(input: List<String>): Int {
        val fallingBricks = FallingBricks(
            input
                .mapIndexed { i, s -> Brick.parse(s, i) }
                .sortedBy { it.lowZ }.toMutableSet()
        )

        fallingBricks.settleBricks()
        return fallingBricks.countFallingBricks()
    }

    private class FallingBricks(
        private val bricks: MutableSet<Brick>,
    ) {
        val settledBricks: MutableSet<Brick> = mutableSetOf()

        fun settleBricks() {
            do {
                markSettledBricksSettled()
                val unsettledBricks = bricks.filterNot { it in settledBricks }
                unsettledBricks.forEach { it.fall() }
            } while (bricks.size != settledBricks.size)
        }

        fun countRemovableBricks(): Int {
            val bricksLookup = bricks.buildLookup()
            val filter = settledBricks.filter { brick ->
                val supporting = brick.supporting(bricksLookup)
                supporting.all { it.supportedBy(bricksLookup).size > 1 }
            }
            return filter.count()
        }

        fun countFallingBricks(): Int {
            val bricksLookup = bricks.buildLookup()
            return settledBricks.sumOf {
                var count = 0
                val queue = it.supporting(bricksLookup).toMutableList()
                while (queue.isNotEmpty()) {
                    val current = queue.removeFirst()
                    val supportedBy = current.supportedBy(bricksLookup)
                    if (supportedBy.size == 1) {
                        count++
                        queue.addAll(supportedBy)
                    }
                }
                count
            }
        }

        private fun markSettledBricksSettled() {
            val bricksLookup = bricks.buildLookup()
            val unsettledBricks = bricks.filterNot { it in settledBricks }
            for (unsettledBrick in unsettledBricks) {
                if (unsettledBrick.lowZ == 1 || unsettledBrick.supportedBy(bricksLookup).any { it in settledBricks }) {
                    println("Marking $unsettledBrick as settled")
                    val restingOnTop = unsettledBrick.allBricksRestingOnTop(bricksLookup)
                    settledBricks.addAll(restingOnTop)
                    settledBricks.add(unsettledBrick)
                }
            }
        }
    }

    private data class Brick(
        val id: Char,
        var points: Set<Point>,
    ) {
        val pointsAbove: List<Point>
            get() {
                return if (points.first().z != points.last().z) {
                    listOf(points.last().let { it.copy(z = it.z + 1) })
                } else {
                    points.map { it.copy(z = it.z + 1) }
                }
            }

        val pointsBelow: List<Point>
            get() {
                return if (points.first().z != points.last().z) {
                    listOf(points.first().let { it.copy(z = it.z - 1) })
                } else {
                    points.map { it.copy(z = it.z - 1) }
                }
            }

        val lowZ: Int get() = points.minOf { it.z }
        val isOnGround: Boolean get() = lowZ == 1

        fun fall() {
            points = points
                .map { it.copy(z = it.z - 1) }
                .toSet()
        }

        fun allBricksRestingOnTop(bricksLookup: Map<Point, Brick>): Collection<Brick> {
            val brick = this
            val restingOnTop = brick.supporting(bricksLookup)
            val above = restingOnTop.flatMap { it.allBricksRestingOnTop(bricksLookup) }
            return restingOnTop + above
        }

        fun supportedBy(bricksLookup: Map<Point, Brick>): Set<Brick> {
            return pointsBelow.mapNotNull { bricksLookup[it] }.toSet()
        }

        fun supporting(bricksLookup: Map<Point, Brick>): Set<Brick> {
            val pointsAbove1 = pointsAbove
            return pointsAbove1.mapNotNull { bricksLookup[it] }.toSet()
        }

        companion object {
            private fun spread(i: Int, j: Int, map: (Int) -> Point): Set<Point> {
                val start = min(i, j)
                val size = abs(i - j) + 1
                return List(size) { map(start + it) }.toSet()
            }

            fun parse(s: String, i: Int = -1): Brick {
                val p = s
                    .split("~")
                    .flatMap { it.split(",") }
                    .map { it.toInt() }

                val points = when {
                    p[0] != p[3] -> spread(p[0], p[3]) { Point(it, p[1], p[2]) }
                    p[1] != p[4] -> spread(p[1], p[4]) { Point(p[0], it, p[2]) }
                    p[2] != p[5] -> spread(p[2], p[5]) { Point(p[0], p[1], it) }
                    else -> setOf(Point(p[0], p[1], p[2]))
                }

                return Brick('A'.plus(i), points)
            }
        }
    }


    private data class Point(val x: Int, val y: Int, val z: Int) {
        override fun toString(): String {
            return "($x,$y,$z)"
        }
    }

    companion object {
        private fun Set<Brick>.buildLookup(): Map<Point, Brick> {
            return this
                .flatMap { brick -> brick.points.map { it to brick } }
                .associate { it }
        }
    }
}
