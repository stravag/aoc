package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import org.junit.jupiter.api.Test
import java.util.Stack
import kotlin.test.assertEquals

class Day24 : AbstractDay() {

    @Test
    fun part1Dummy() {
        Debug.enable()
        val input = """
            x00: 1
            x01: 1
            x02: 1
            y00: 0
            y01: 1
            y02: 0

            x00 AND y00 -> z00
            x01 XOR y01 -> z01
            x02 OR y02 -> z02
        """.trimIndent().lines()
        assertEquals(4, compute1(input))
    }

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(2024, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(43559017878162, compute1(puzzleInput))
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
        val data = getInitialInputs(input).toMutableMap()
        val gates = getGates(input)
        val settledGates = mutableSetOf<Gate>()
        while (settledGates.size < gates.size) {
            val gatesToSettle = gates
                .filter { it !in settledGates }
                .filter { it.i1 in data && it.i2 in data }

            gatesToSettle.forEach { gateToSettle ->
                val i1 = data.getValue(gateToSettle.i1)
                val i2 = data.getValue(gateToSettle.i2)
                val (out, outVal) = gateToSettle.perform(i1, i2)
                data[out] = outVal
                settledGates.add(gateToSettle)
            }
        }

        return gates
            .map { it.out }
            .filter { it.startsWith("z") }
            .sumOf { out ->
                val shift = out.drop(1).toInt()
                val outVal = data.getValue(out)
                val shiftedVal = outVal.toLong() shl shift
                shiftedVal
            }
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }


    private fun getInitialInputs(input: List<String>): Map<String, Int> {
        return input
            .takeWhile { it.isNotEmpty() }
            .associate {
                val (i, value) = it.split(": ")
                i to value.toInt()
            }
    }

    private fun getGates(input: List<String>): List<Gate> {
        return input
            .dropWhile { it.contains(": ") }
            .drop(1)
            .map { line ->
                val (gateStr, out) = line.split(" -> ")
                val (i1, op, i2) = gateStr.split(" ")
                Gate(i1, i2, op, out)
            }
    }

    private fun List<Gate>.associateByInputs(): Map<String, List<Gate>> {
        val result = mutableMapOf<String, MutableList<Gate>>()
        this.forEach { gate ->
            result.compute(gate.i1) { _, acc -> (acc ?: mutableListOf()).apply { add(gate) } }
            result.compute(gate.i2) { _, acc -> (acc ?: mutableListOf()).apply { add(gate) } }
        }
        return result
    }

    data class Gate(
        val i1: String,
        val i2: String,
        private val op: String,
        val out: String,
    ) {
        fun perform(i1Val: Int, i2Val: Int): Pair<String, Int> {
            return out to getGateFunction(op).invoke(i1Val, i2Val)
        }

        private fun getGateFunction(op: String): (Int, Int) -> Int {
            return when (op) {
                "AND" -> { a, b -> a and b }
                "OR" -> { a, b -> a or b }
                "XOR" -> { a, b -> a xor b }
                else -> error("Invalid op: $op")
            }
        }
    }
}
