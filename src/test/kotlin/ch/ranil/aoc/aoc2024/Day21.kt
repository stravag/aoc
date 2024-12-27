package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.*
import ch.ranil.aoc.common.types.Direction
import ch.ranil.aoc.common.types.Direction.*
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Could've not solved this without some inspiration from these two good people 😇🙏
 * https://www.reddit.com/r/adventofcode/comments/1hj2odw/comment/m370d1h
 * https://github.com/lukaslebo/AdventOfCode/blob/main/year2024/src/day21/Day21.kt
 */
class Day21 : AbstractDay() {

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
    fun part2Puzzle() {
        assertEquals(260586897262600, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val starship = Starship()
        return input.sumOf { sequence ->
            starship.complexity(sequence, depth = 2)
        }
    }

    private fun compute2(input: List<String>): Long {
        val starship = Starship()
        return input.sumOf { sequence ->
            starship.complexity(sequence, depth = 25)
        }
    }

    private class Starship {
        val possibleMoves: MutableMap<Pair<Button, Button>, List<List<Button>>> = mutableMapOf()

        fun complexity(sequence: String, depth: Int): Long {
            val dPadMovesLength = bestLength(sequence, depth)
            val sequenceVal = sequence.dropLast(1).toLong()
            println("$dPadMovesLength * $sequenceVal")
            return dPadMovesLength * sequenceVal
        }

        private fun bestLength(code: String, depth: Int): Long {
            val directionsForNumPad = "A$code"
                .map { Button(it) }
                .windowed(2)
                .fold(listOf<List<Button>>()) { list, (src, dst) ->
                    val cacheKey = src to dst
                    val nextMoves = possibleMoves.getValue(cacheKey)
                    if (list.isEmpty()) {
                        nextMoves
                    } else {
                        list.flatMap { path -> nextMoves.map { path + it } }
                    }
                }

            Debug.debug {
                println("Possible inputs for numPad: $code")
                directionsForNumPad.forEach { buttons -> println(buttons.joinToString("") { "${it.c}" }) }
            }


            val bestLength = directionsForNumPad.minOf { directions ->
                bestDPadSequenceLengthForDPad(directions, depth)
            }

            return bestLength
        }

        private fun bestDPadSequenceLengthForDPad(
            dPadInputs: List<Button>,
            depth: Int,
            cache: MutableMap<CacheKey, Long> = mutableMapOf(),
        ): Long {
            var total = 0L
            var current = Button('A')
            for (dPadInput in dPadInputs) {
                total += bestDPadSequenceLengthForDPad(current, dPadInput, depth, cache)
                current = dPadInput
            }
            return total
        }

        private fun bestDPadSequenceLengthForDPad(
            src: Button,
            dst: Button,
            depth: Int,
            cache: MutableMap<CacheKey, Long>,
        ): Long {
            if (depth == 1) {
                return possibleMoves.getValue(src to dst).minOf { it.size.toLong() }
            }
            val key = CacheKey(
                src = src,
                dst = dst,
                depth = depth,
            )
            return cache.getOrPut(key) {
                possibleMoves.getValue(src to dst).minOf { directions ->
                    bestDPadSequenceLengthForDPad(directions, depth - 1, cache)
                }
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
                possibleMoves.putAll(shortestPaths)
            }

            nPadPoints.forEach { nPadPoint ->
                val shortestPaths = findShortestPaths(nPadPoint, nPad, nPadPoints)
                possibleMoves.putAll(shortestPaths)
            }
        }

        private fun findShortestPaths(
            from: Point,
            pad: List<String>,
            padPoints: Set<Point>,
        ): Map<Pair<Button, Button>, List<List<Button>>> {
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
                    Button(pad.charFor(from)) to Button(pad.charFor(point))
                }
                .mapValues { (_, moves) ->
                    moves.map { directions -> directions.map { Button(it.indicator) } + Button('A') }
                }
        }

        data class Button(val c: Char) {
            override fun toString(): String = "$c"
        }

        data class CacheKey(
            val src: Button,
            val dst: Button,
            val depth: Int,
        )
    }
}
