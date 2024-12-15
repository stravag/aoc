package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.aoc2024.Day15.Warehouse.Thing.*
import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.Debug.debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.Direction
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day15 : AbstractDay() {

    @Test
    fun part1TestDummy() {
        assertEquals(2028, compute1(test2Input))
    }

    @Test
    fun part1Test() {
        assertEquals(10092, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(1398947, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val warehouseInput = input.takeWhile { it.isNotBlank() }
        val warehouse = Warehouse.parse(warehouseInput)
        val movements = input
            .drop(warehouseInput.size + 1)
            .flatMap { line -> line.map { char -> Direction.of(char) } }

        println("Initial state:")
        warehouse.print()

        movements.forEach { direction ->
            warehouse.moveRobot(direction)
        }

        println("Final state:")
        warehouse.print()

        return warehouse.coordinateSum()
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }

    private class Warehouse(
        private val maxX: Int,
        private val maxY: Int,
        private val wall: Set<Point>,
        private val boxes: MutableSet<Point>,
        private var robot: Point
    ) {
        fun coordinateSum(): Long {
            return boxes.sumOf { box ->
                100L * box.y + box.x
            }
        }

        fun moveRobot(direction: Direction) {
            val newPosition = robot.peek(direction)
            val canMove = when (thingAt(newPosition)) {
                BOX -> moveBox(newPosition, direction)
                WALL -> false
                SPACE -> true
                ROBOT -> error("this shouldn't happen")
            }

            if (canMove) {
                robot = newPosition
            }
        }

        fun moveBox(box: Point, direction: Direction): Boolean {
            val newPosition = box.move(direction)
            val canMove = when (thingAt(newPosition)) {
                BOX -> moveBox(newPosition, direction)
                WALL -> false
                SPACE -> true
                ROBOT -> error("this shouldn't happen")
            }

            if (canMove) {
                boxes.remove(box)
                boxes.add(newPosition)
            }

            return canMove
        }

        fun print() {
            for (y in 0..<maxY) {
                for (x in 0..<maxX) {
                    val p = Point(x, y)
                    when (thingAt(p)) {
                        ROBOT -> printColor('@', PrintColor.GREEN)
                        BOX -> printColor('O', PrintColor.YELLOW)
                        WALL -> printColor('#', PrintColor.RED)
                        SPACE -> print('.')
                    }
                }
                println()
            }
        }

        private fun Point.peek(direction: Direction): Point = move(direction)

        private fun thingAt(position: Point): Thing {
            if (robot == position) return ROBOT
            if (boxes.contains(position)) return BOX
            if (wall.contains(position)) return WALL
            return SPACE
        }

        enum class Thing {
            ROBOT,
            BOX,
            WALL,
            SPACE,
        }

        companion object {
            fun parse(input: List<String>): Warehouse {
                val maxX = input.first().length
                val maxY = input.size
                val walls = mutableSetOf<Point>()
                val boxes = mutableSetOf<Point>()
                var robot: Point? = null
                input.forEachIndexed { y, line ->
                    line.forEachIndexed { x, c ->
                        val point = Point(x, y)
                        when (c) {
                            '#' -> walls.add(point)
                            'O' -> boxes.add(point)
                            '@' -> robot = point
                        }
                    }
                }
                return Warehouse(maxX, maxY, walls, boxes, requireNotNull(robot))
            }
        }
    }

}
