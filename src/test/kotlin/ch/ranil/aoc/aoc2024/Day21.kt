package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.*
import ch.ranil.aoc.common.types.Direction
import ch.ranil.aoc.common.types.Direction.*
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.reflect.KMutableProperty0
import kotlin.test.assertEquals

class Day21 : AbstractDay() {

    @Test
    fun part1Dummy1() {
        val starship = Starship()
        // 👇 this seems fine ¯\_(ツ)_/¯
        assertEquals("<A", starship.reset().press("0", emptyList()))
        assertEquals("<v<A>>^A", starship.reset().press("0", listOf(starship::dPad1)))
        assertEquals("<v<A>A<A>>^AvAA<^A>A", starship.reset().press("0", listOf(starship::dPad1, starship::dPad2)))
    }

    @Test
    fun part1DummyLong() {
        Debug.enable()
        val starship = Starship()
        //   <A^A>^^AvvvA 👈 this seems fine ¯\_(ツ)_/¯
        assertEquals(
            "<A^A>^^AvvvA",
            starship.reset().press("029A", emptyList())
        )
        //   v<<A>>^A<A>AvA<^AA>A<vAAA>^A 👈 this seems fine ¯\_(ツ)_/¯
        assertEquals(
            "<v<A>>^A<A>AvA<^AA>A<vAAA>^A",
            starship.reset().press("029A", listOf(starship::dPad1))
        )
        // TODO THIS IS SHORTER THAN MINE... HOW CAN I DETERMINE SHORTEST PATH??
        //   <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
        assertEquals(
            "<v<A>A<A>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A",
            starship.reset().press("029A", listOf(starship::dPad1, starship::dPad2))
        )
    }

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(126384, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
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
        val starship = Starship()
        return input.sumOf { sequence ->
            val dPadMoves = starship.press(sequence, listOf(starship::dPad1, starship::dPad2))
            val sequenceVal = sequence.dropLast(1).toLong()
            println("$sequence: $dPadMoves")
            println("${dPadMoves.length} * $sequenceVal")
            sequenceVal * dPadMoves.length
        }
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

    private class Starship {

        var numPadLoc: Button = Button('A')
        var dPad1: Button = Button('A')
        var dPad2: Button = Button('A')
        val moveCache: MutableMap<CacheKey, List<Button>> = mutableMapOf()

        private val colorMap = mapOf(
            ::dPad1 to PrintColor.YELLOW,
            ::dPad2 to PrintColor.BLUE,
        )

        fun reset(): Starship {
            numPadLoc = Button('A')
            dPad1 = Button('A')
            dPad2 = Button('A')
            return this
        }

        fun press(sequence: String, involvedPads: List<KMutableProperty0<Button>>): String {
            Debug.debug { println("Pressing $sequence (via ${involvedPads.map { it.name }})}...") }
            val numButtons = sequence.map { Button(it) }
            val inputs = mutableListOf<Button>()
            for (numButton in numButtons) {
                val cacheKey = CacheKey(numPadLoc, numButton)
                for (dPadMove in moveCache.getValue(cacheKey)) {
                    val subMoves = getDPadSequenceForDPad(dPadMove, involvedPads)
                    inputs.addAll(subMoves)
                }
                numPadLoc = numButton
                Debug.debug { printlnColor("Pressed $numPadLoc on numPad", PrintColor.GREEN) }
            }
            Debug.debug { println("... done") }

            return inputs.joinToString("") { it.c.toString() }
        }

        private fun getDPadSequenceForDPad(
            dPad: Button,
            involvedPads: List<KMutableProperty0<Button>>
        ): List<Button> {
            if (involvedPads.isEmpty()) {
                return listOf(dPad)
            }

            val currentPad = involvedPads.first()
            val cacheKey = CacheKey(currentPad.get(), dPad)
            val nextDPadMoves = moveCache.getValue(cacheKey)
            val allDPad = nextDPadMoves.fold(mutableListOf<Button>()) { acc, nextDPadMove ->
                acc.addAll(getDPadSequenceForDPad(nextDPadMove, involvedPads.drop(1)))
                acc
            }
            currentPad.set(dPad)
            Debug.debug { printlnColor("Pressed $dPad on ${currentPad.name}", colorMap.getValue(currentPad)) }
            return allDPad
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
        ): Map<CacheKey, List<Button>> {
            val seen = mutableMapOf<Point, List<Direction>>()
            val queue = ArrayDeque<Pair<Point, List<Direction>>>()
            queue.add(from to emptyList())
            while (queue.isNotEmpty()) {
                val (next, moves) = queue.removeFirst()
                seen[next] = moves
                listOf(N, E, S, W)
                    .filter { next.move(it) !in seen }
                    .filter { next.move(it) in padPoints }
                    .forEach { direction ->
                        queue.add(next.move(direction) to moves + direction)
                    }
            }
            return seen
                .mapKeys { (point, _) ->
                    CacheKey(
                        robotState = listOf(Button(pad.charFor(from))),
                        targetButton = Button(pad.charFor(point))
                    )
                }
                .mapValues { (_, directions) ->
                    directions.map { Button(it.indicator) } + Button('A')
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
