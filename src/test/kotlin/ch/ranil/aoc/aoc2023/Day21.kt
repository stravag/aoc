package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.PrintColor
import ch.ranil.aoc.printColor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day21 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(16, compute1(testInput, 6))
    }

    @Test
    fun part1TestSimple() {
        assertEquals(4, compute1(testInput, 2))
        assertEquals(2, compute1(testInput, 1))
        assertEquals(1, compute1(testInput, 0))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(3632, compute1(puzzleInput, 64))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>, steps: Int): Int {
        val (garden, start) = Garden.parse(input)

        val result = mutableSetOf<Point>()
        val seen = mutableSetOf(start)
        val queue = mutableListOf(start to 0)
        while (queue.isNotEmpty()) {
            val (current, step) = queue.removeFirst()
            // the reachable points always toggle
            if (step % 2 == steps % 2) result.add(current)
            if (step < steps) {
                val nextTiles = garden.nextTilesFor(current).filter { it !in seen }
                for (next in nextTiles) {
                    seen.add(next)
                    queue.add(next to step + 1)
                }
            }
        }

        print(garden, seen, result)

        return result.size
    }

    private fun compute2(input: List<String>): Int {
        TODO()
    }

    private data class Garden(
        val width: Int,
        val height: Int,
        val rocks: Set<Point>,
        val tiles: Set<Point>,
    ) {

        private fun contains(p: Point): Boolean {
            return p in rocks || p in tiles
        }

        fun nextTilesFor(p: Point): Collection<Point> {
            return setOfNotNull(
                p.move(1, Direction.N),
                p.move(1, Direction.E),
                p.move(1, Direction.S),
                p.move(1, Direction.W),
            )
                .filter { this.contains(it) }
                .filter { it !in rocks }
        }

        companion object {
            fun parse(input: List<String>): Pair<Garden, Point> {
                val rocks = mutableSetOf<Point>()
                val tiles = mutableSetOf<Point>()
                var start: Point? = null
                input.forEachIndexed { y, s ->
                    s.forEachIndexed { x, c ->
                        val p = Point(x, y)
                        when (c) {
                            '#' -> rocks.add(p)
                            '.' -> tiles.add(p)
                            'S' -> {
                                tiles.add(p)
                                start = p
                            }
                        }
                    }
                }
                val garden = Garden(
                    width = input.first().length,
                    height = input.size,
                    rocks = rocks,
                    tiles = tiles,
                )
                return garden to requireNotNull(start)
            }
        }
    }

    private fun print(garden: Garden, reachable: Collection<Point>, nxt: Collection<Point>) {
        for (y in 0..<garden.height) {
            for (x in 0..<garden.width) {
                val p = Point(x, y)
                when (p) {
                    in nxt -> printColor(PrintColor.YELLOW, 'O')
                    in reachable -> printColor(PrintColor.GREEN, 'O')
                    in garden.rocks -> printColor(PrintColor.RED, '#')
                    in garden.tiles -> print('.')
                }
            }
            println()
        }
    }
}
