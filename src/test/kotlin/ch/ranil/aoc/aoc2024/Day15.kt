package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.aoc2024.Day15.Warehouse.Thing.*
import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.Debug.debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.Direction
import ch.ranil.aoc.common.types.Direction.*
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day15 : AbstractDay() {

    @Test
    fun part1TestDummy() {
        assertEquals(
            2028, compute1(
                """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########

            <^^>>>vv<v>>v<<
        """.trimIndent().lines()
            )
        )
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
    fun part2TestDummy1() {
        compute2(
            """
            #######
            #...#.#
            #.....#
            #..O..#
            #.@O..#
            #..O..#
            #######

            >><^^>v
        """.trimIndent().lines()
        )
    }

    @Test
    fun part2TestDummy2() {
        compute2(
            """
            #######
            #OO@..#
            #######

            <
        """.trimIndent().lines()
        )
    }

    @Test
    fun part2TestDummy3() {
        assertEquals(
            105, compute2(
                """
            #######
            #@O...#
            #.....#
            #######

            >>v
        """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part2TestDummy4() {
        Debug.enable()
        compute2(
            """
            #########
            #.......#
            #.O.OOO.#
            #.OO.O..#
            #..OO...#
            #.@OO...#
            #...O...#
            #.......#
            #########

            >><^<<^>vvvv>>>>^^
        """.trimIndent().lines()
        )
    }

    @Test
    fun part2Test() {
        assertEquals(9021, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1397393, compute2(puzzleInput))
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

    private fun compute2(input: List<String>): Long {
        val warehouseInput = input.takeWhile { it.isNotBlank() }
        val warehouse = Warehouse.parseExtended(warehouseInput)
        val movements = input
            .drop(warehouseInput.size + 1)
            .flatMap { line -> line.map { char -> Direction.of(char) } }

        println("Initial state:")
        warehouse.printExtended()

        movements.forEachIndexed { i, direction ->
            debug { println("Move #$i ${direction.indicator}:") }
            warehouse.moveRobot(direction)
            debug { warehouse.printExtended() }
        }

        println("Final state:")
        warehouse.printExtended()

        return warehouse.coordinateSum()
    }

    private class Warehouse(
        private val maxX: Int,
        private val maxY: Int,
        private val wall: Set<Point>,
        private val boxes: MutableMap<Point, Box>,
        private var robot: Point
    ) {
        fun coordinateSum(): Long {
            return boxes.values.distinct().sumOf { box ->
                val pos = box.pos1
                100L * pos.y + pos.x
            }
        }

        fun moveRobot(direction: Direction) {
            val canMove = when (robotWouldHit(direction)) {
                BOX -> {
                    val allBoxesToMove = mutableListOf<Box>()
                    val canMove = moveBox(boxes.getValue(robot.move(direction)), direction, allBoxesToMove)
                    if (canMove) {
                        when (direction) {
                            N -> allBoxesToMove.sortedBy { it.pos1.y }.forEach { moveBox(it, direction) }
                            E -> allBoxesToMove.sortedByDescending { it.pos1.x }.forEach { moveBox(it, direction) }
                            W -> allBoxesToMove.sortedBy { it.pos1.x }.forEach { moveBox(it, direction) }
                            S -> allBoxesToMove.sortedByDescending { it.pos1.y }.forEach { moveBox(it, direction) }
                        }
                    }
                    canMove
                }

                WALL -> false
                SPACE -> true
                ROBOT -> error("this shouldn't happen")
            }

            if (canMove) {
                robot = robot.move(direction)
            }
        }

        fun moveBox(box: Box, direction: Direction) {
            boxes.remove(box.pos1)
            boxes.remove(box.pos2)
            val newBoxPosition = box.move(direction)
            boxes[newBoxPosition.pos1] = newBoxPosition
            boxes[newBoxPosition.pos2] = newBoxPosition
        }

        fun moveBox(box: Box, direction: Direction, allBoxesToMove: MutableList<Box>): Boolean {
            val canMove = when (box.wouldHit(direction)) {
                BOX -> {
                    val boxesToMove = when (direction) {
                        N, S -> setOfNotNull(
                            boxes[box.pos1.move(direction)],
                            boxes[box.pos2.move(direction)],
                        )

                        E -> setOf(boxes.getValue(box.pos2.move(direction)))
                        W -> setOf(boxes.getValue(box.pos1.move(direction)))
                    }
                    boxesToMove
                        .map { moveBox(it, direction, allBoxesToMove) }
                        .reduce { acc, can -> acc && can }
                }

                WALL -> false
                SPACE -> true
                ROBOT -> error("this shouldn't happen")
            }

            if (canMove) {
                allBoxesToMove.add(box)
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

        fun printExtended() {
            for (y in 0..<maxY) {
                for (x in 0..<maxX) {
                    val p = Point(x, y)
                    when (thingAt(p)) {
                        ROBOT -> printColor('@', PrintColor.GREEN)
                        BOX -> {
                            val box = boxes.getValue(p)
                            if (box.pos1 == p) printColor('[', PrintColor.YELLOW)
                            if (box.pos2 == p) printColor(']', PrintColor.YELLOW)
                        }

                        WALL -> printColor('#', PrintColor.RED)
                        SPACE -> print('.')
                    }
                }
                println()
            }
        }

        private fun robotWouldHit(direction: Direction): Thing {
            val position = robot.move(direction)
            if (boxes.contains(position)) return BOX
            if (wall.contains(position)) return WALL
            return SPACE
        }

        private fun Box.wouldHit(direction: Direction): Thing {
            val positionsToCheck = when (direction) {
                N, S -> setOf(pos1.move(direction), pos2.move(direction))
                E -> setOf(pos2.move(direction))
                W -> setOf(pos1.move(direction))
            }

            if (positionsToCheck.any { it in wall }) return WALL
            if (positionsToCheck.any { it in boxes }) return BOX
            return SPACE
        }

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

        data class Box(val pos1: Point, val pos2: Point = pos1) {
            fun move(direction: Direction) = Box(pos1.move(direction), pos2.move(direction))
        }

        companion object {
            fun parse(input: List<String>): Warehouse {
                val maxX = input.first().length
                val maxY = input.size
                val walls = mutableSetOf<Point>()
                val boxes = mutableMapOf<Point, Box>()
                var robot: Point? = null
                input.forEachIndexed { y, line ->
                    line.forEachIndexed { x, c ->
                        val point = Point(x, y)
                        when (c) {
                            '#' -> walls.add(point)
                            'O' -> boxes[point] = Box(point)
                            '@' -> robot = point
                        }
                    }
                }
                return Warehouse(maxX, maxY, walls, boxes, requireNotNull(robot))
            }

            fun parseExtended(input: List<String>): Warehouse {
                val maxX = input.first().length * 2
                val maxY = input.size
                val walls = mutableSetOf<Point>()
                val boxes = mutableMapOf<Point, Box>()
                var robot: Point? = null
                input.forEachIndexed { y, line ->
                    line.forEachIndexed { x, c ->
                        val point1 = Point(2 * x, y)
                        val point2 = Point(2 * x + 1, y)
                        when (c) {
                            '#' -> {
                                walls.add(point1)
                                walls.add(point2)
                            }

                            'O' -> {
                                val box = Box(point1, point2)
                                boxes[point1] = box
                                boxes[point2] = box
                            }

                            '@' -> robot = point1
                        }
                    }
                }
                return Warehouse(maxX, maxY, walls, boxes, requireNotNull(robot))
            }
        }
    }

}
