package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.*
import ch.ranil.aoc.common.types.Direction
import ch.ranil.aoc.common.types.Direction.*
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Could've not solved part 1 without some inspiration from 😇🙏
 * https://www.reddit.com/r/adventofcode/comments/1hj2odw/comment/m370d1h
 */
class Day21 : AbstractDay() {

    @Test
    fun part1Dummy() {
        val starship = Starship()
        // 👇 this seems fine ¯\_(ツ)_/¯
        assertEquals("<A", starship.press("0", 0))
        assertEquals("v<<A>^>A", starship.press("0", 1))
        // 👇 this seems wrong ¯\_(ツ)_/¯
        assertEquals("v<A<AA>^>AvAA^<A>A", starship.press("0", 2))
    }

    @Test
    fun part1DummyLong() {
        Debug.enable()
        val starship = Starship()
        //   <A^A>^^AvvvA 👈 this seems fine ¯\_(ツ)_/¯
        assertEquals(
            "<A^A^^>AvvvA",
            starship.press("029A", 0)
        )
        //   v<<A>>^A<A>AvA<^AA>A<vAAA>^A 👈 this seems fine ¯\_(ツ)_/¯
        assertEquals(
            "v<<A>^>A<A>A<AA>vA^Av<AAA^>A",
            starship.press("029A", 1)
        )
        //   <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A  👈 this seems fine ¯\_(ツ)_/¯
        assertEquals(
            "v<A<AA>^>AvAA^<A>Av<<A>^>AvA^Av<<A>^>AAvA<A^>A<A>Av<A<A>^>AAA<A>vA^A",
            starship.press("029A", 2)
        )
    }

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(126384, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(215374, compute1(puzzleInput))
    }

    @Test
    fun part2Dummy() {
        val starship = Starship()
        assertEquals("<A", starship.press("0", 25))
    }

    @Test
    @Disabled
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(testInput))
    }

    @Test
    @Disabled
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val starship = Starship()
        return input.sumOf { sequence ->
            val dPadMoves = starship.press(sequence, depth = 2)
            val sequenceVal = sequence.dropLast(1).toLong()
            println("$sequence: $dPadMoves")
            println("${dPadMoves.length} * $sequenceVal")
            sequenceVal * dPadMoves.length
        }
    }

    private fun compute2(input: List<String>): Long {
        val starship = Starship()
        return input.sumOf { sequence ->
            val dPadMoves = starship.press(sequence, depth = 3)
            val sequenceVal = sequence.dropLast(1).toLong()
            println("$sequence: $dPadMoves")
            println("${dPadMoves.length} * $sequenceVal")
            sequenceVal * dPadMoves.length
        }
    }

    private class Starship {

        val moveCache: MutableMap<CacheKey, List<List<Button>>> = mutableMapOf()

        fun press(sequence: String, depth: Int): String {
            val inputsForNumPad = "A$sequence"
                .map { Button(it) }
                .windowed(2)
                .fold(listOf<List<Button>>()) { list, (src, dst) ->
                    val cacheKey = CacheKey(src, dst)
                    val nextMoves = moveCache.getValue(cacheKey)
                    if (list.isEmpty()) {
                        nextMoves
                    } else {
                        list.flatMap { path -> nextMoves.map { path + it } }
                    }
                }

            Debug.debug { println("Possible inputs for numPad: $inputsForNumPad") }
            var inputsForDPad = inputsForNumPad
            repeat(depth) {
                inputsForDPad = getDPadSequenceForDPad(inputsForDPad)
            }

            return inputsForDPad.first().joinToString("") { it.c.toString() }
        }

        private fun getDPadSequenceForDPad(
            possibleDPadInputs: List<List<Button>>,
        ): List<List<Button>> {
            val possibleInputs = possibleDPadInputs
                .flatMap { inputs -> (listOf(Button('A')) + inputs).getControls() }
            val bestSolutionCount = possibleInputs.minOf { it.count() }
            val bestSolutions = possibleInputs.filter { it.count() == bestSolutionCount }
            return bestSolutions
        }

        private fun List<Button>.getControls(): List<List<Button>> = this
            .windowed(2)
            .fold(mutableListOf(mutableListOf())) { list, (src, dst) ->
                val cacheKey = CacheKey(src, dst)
                val nextMoves = moveCache.getValue(cacheKey)
                if (list.isEmpty()) {
                    nextMoves
                } else {
                    list.flatMap { path -> nextMoves.map { path + it } }
                }
            }

        init {
            val dPad = " ^A\n<v>".lines()
            val nPad = "789\n456\n123\n 0A".lines()
            val dPadPoints = dPad.allPoints()
                .filter { dPad.charFor(it) != ' ' }
                .toSet()

            val nPadPoints = nPad.allPoints()
                .filter { nPad.charFor(it) != ' ' }
                .toSet()

            dPadPoints.forEach { dPadPoint ->
                val shortestPaths = findShortestPaths(dPadPoint, dPad, dPadPoints)
                moveCache.putAll(shortestPaths)
            }

            nPadPoints.forEach { nPadPoint ->
                val shortestPaths = findShortestPaths(nPadPoint, nPad, nPadPoints)
                moveCache.putAll(shortestPaths)
            }
        }

        private fun findShortestPaths(
            from: Point,
            pad: List<String>,
            padPoints: Set<Point>,
        ): Map<CacheKey, List<List<Button>>> {
            val seen = mutableMapOf<Point, MutableSet<List<Direction>>>()
            val queue = ArrayDeque<Pair<Point, List<Direction>>>()
            queue.add(from to emptyList())
            while (queue.isNotEmpty()) {
                val (point, movesToPoint) = queue.removeFirst()
                seen.compute(point) { _, shortestMoves ->
                    (shortestMoves ?: mutableSetOf()).apply { add(movesToPoint) }
                }
                for (direction in listOf(N, E, S, W)) {
                    val nextPoint = point.move(direction)
                    if (nextPoint !in padPoints) continue // stay within pad

                    val movesToNextPoint = movesToPoint + direction
                    val bestKnownMovesToPoint = seen[nextPoint]?.minOf { it.size } ?: Int.MAX_VALUE
                    if (movesToNextPoint.size > bestKnownMovesToPoint) continue // already worse off

                    queue.add(nextPoint to movesToNextPoint)
                }
            }
            return seen
                .mapKeys { (point, _) ->
                    CacheKey(
                        robotState = listOf(Button(pad.charFor(from))),
                        targetButton = Button(pad.charFor(point))
                    )
                }
                .mapValues { (_, moves) ->
                    moves.map { directions -> directions.map { Button(it.indicator) } + Button('A') }
                }
        }

        data class Button(val c: Char) {
            override fun toString(): String = "$c"
        }

        data class CacheKey(
            val robotState: List<Button>,
            val targetButton: Button
        ) {
            constructor(robotState: Button, targetButton: Button) : this(listOf(robotState), targetButton)
        }
    }
}
