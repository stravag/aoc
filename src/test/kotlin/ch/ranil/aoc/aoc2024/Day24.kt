package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printlnColor
import org.junit.jupiter.api.Test
import java.lang.Long.toBinaryString
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
    fun part2FindFaultyBits() {
        val gates = getGates(puzzleInput)
        val invalidBits = (0..44)
            .flatMap { i -> gates.validate(i) }
            .distinct()
            .sorted()
        for (i in 0..44) {
            val color = if (i in invalidBits) PrintColor.RED else PrintColor.GREEN
            val gatesInvolved = gates.filterInvolvedForZ(i)
            println("Gates for $i (${gatesInvolved.count()})")
            gatesInvolved.forEach { printlnColor(it, color) }
        }
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput, Long::plus))
    }

    private fun compute1(input: List<String>): Long {
        val data = getInitialInputs(input).toMutableMap()
        val gates = getGates(input)
        return gates.run(data)
    }

    private fun List<Gate>.run(data: MutableMap<String, Int>): Long {
        val gates = this
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
            .filter { it.out.startsWith("z") }
            .map { it.out }
            .toNumber(data)
    }

    private fun List<Gate>.filterInvolvedForZ(zBit: Int): Set<Gate> {
        val gates = this
        fun getInvolvedFor(zBit: Int): Set<Gate> {
            val result = mutableSetOf<Gate>()
            val nextOutputs = mutableListOf(zBit.toWire('z'))
            while (nextOutputs.isNotEmpty()) {
                val out = nextOutputs.removeFirst()
                val gateOfOut = gates.singleOrNull { it.out == out } ?: continue

                result.add(gateOfOut)
                if (gateOfOut.i1.first() !in listOf('x', 'y')) {
                    nextOutputs.add(gateOfOut.i1)
                }
                if (gateOfOut.i2.first() !in listOf('x', 'y')) {
                    nextOutputs.add(gateOfOut.i2)
                }
            }
            return result
        }
        return getInvolvedFor(zBit) - getInvolvedFor(zBit - 1)
    }

    private fun List<Gate>.filterInvolvedForXY(bit: Int): Set<Gate> {
        val gates = this
        fun getInvolvedFor(bit: Int): Set<Gate> {
            val result = mutableSetOf<Gate>()
            val nextInputs = mutableListOf(bit.toWire('x'), bit.toWire('y'))
            while (nextInputs.isNotEmpty()) {
                val input = nextInputs.removeFirst()
                val gatesOfIn = gates.filter { it.i1 == input || it.i2 == input }
                result.addAll(gatesOfIn)
                nextInputs.addAll(gatesOfIn.map { it.out })
            }
            return result
        }
        return getInvolvedFor(bit) - getInvolvedFor(bit - 1)
    }

    private fun List<Gate>.validate(n: Int): Set<Int> {
        fun initData(): MutableMap<String, Int> {
            val data = mutableMapOf<String, Int>()
            repeat(45) { i ->
                data[i.toWire('x')] = 0
                data[i.toWire('y')] = 0
            }
            return data
        }

        val gates = this
        for (x in 0..1L) {
            for (y in 0..1L) {
                // Create init_x and init_y
                val data = initData()
                data[n.toWire('x')] = x.toInt()
                data[n.toWire('y')] = y.toInt()

                // Call the run_wire2 function
                val z = gates.run(data)

                // Validate the result
                val zString = toBinaryString(z).padStart(46, '0')
                val bit = zString[45 - n]
                val carry = zString[45 - n - 1]

                val invalidZBits = mutableSetOf<Int>()
                val (expectedBit, expectedCarry) = when {
                    x == 0L && y == 0L -> '0' to '0'
                    x == 0L && y == 1L -> '1' to '0'
                    x == 1L && y == 0L -> '1' to '0'
                    x == 1L && y == 1L -> '0' to '1'
                    else -> error("Invalid case")
                }
                if (bit != expectedBit) invalidZBits.add(n)
                if (carry != expectedCarry) invalidZBits.add(n + 1)
                if (invalidZBits.isNotEmpty()) return invalidZBits
            }
        }
        return emptySet()
    }

    private fun compute2(input: List<String>, op: (Long, Long) -> Long): Long {
        val data = getInitialInputs(input).toMutableMap()
        val gates = getGates(input)
        val resultSize = gates.count { it.out.startsWith("z") }

        val (xInputs, yInputs) = data.keys.partition { it.startsWith("x") }
        val x = xInputs.toNumber(data)
        val y = yInputs.toNumber(data)
        val z = op(x, y)
        val actual = compute1(input)

        println("Desired: ${toBinaryString(z).padStart(resultSize, '0')}")
        println("Actual : ${toBinaryString(actual).padStart(resultSize, '0')}")
        println("Diff   : ${toBinaryString(z xor actual).padStart(resultSize, '0')}")

        TODO()
    }

    private fun List<String>.toNumber(data: MutableMap<String, Int>): Long {
        return this.sumOf { out ->
            val shift = out.drop(1).toInt()
            val outVal = data.getValue(out)
            val shiftedVal = outVal.toLong() shl shift
            shiftedVal
        }
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

    private data class Gate(
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

        override fun toString(): String {
            return "$i1 $op $i2 -> $out"
        }
    }

    private fun Int.toWire(type: Char): String = "$type${this.toString().padStart(2, '0')}"
}
