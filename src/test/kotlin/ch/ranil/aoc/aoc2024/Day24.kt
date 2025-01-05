package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printlnColor
import org.junit.jupiter.api.Test
import java.lang.Long.toBinaryString
import kotlin.test.assertEquals

/**
 * Was only able to solve this by taking loads of inspiration by this good man 😇🙏🙈:
 * https://www.youtube.com/watch?v=pH5MRTC4MLY
 */
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
            val gatesInvolved = gates.filterDistinctInvolvedForZ(i)
            println("Gates for $i (${gatesInvolved.count()})")
            gatesInvolved.forEach { printlnColor(it, color) }
        }
    }

    @Test
    fun part2AnalyzeGates() {
        val gates = getGates(puzzleInput)

        listOf(0, 1, 2, 3).forEach { i ->
            val gatesInvolved = gates.filterInvolvedFor(i)
            println("Gates for $i (${gatesInvolved.count()})")
            gatesInvolved.forEach { println(it) }
            println()
        }
    }

    @Test
    fun part2Puzzle() {
        assertEquals("fhc,ggt,hqk,mwh,qhj,z06,z11,z35", compute2(puzzleInput))
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

    private fun List<Gate>.filterDistinctInvolvedForZ(zBit: Int): Set<Gate> {
        return filterInvolvedFor(zBit) - filterInvolvedFor(zBit - 1)
    }

    private fun List<Gate>.filterInvolvedFor(zBit: Int): Set<Gate> {
        val gates = this
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
                val data = initData()
                data[n.toWire('x')] = x.toInt()
                data[n.toWire('y')] = y.toInt()

                val z = gates.run(data)

                val zString = toBinaryString(z).padStart(46, '0')
                val bit = zString[45 - n]
                val carry = zString[45 - n - 1]

                val invalidZBits = mutableSetOf<Int>()
                val (expectedBit, expectedCarry) = when {
                    x == 0L && y == 0L -> '0' to '0'
                    x == 0L -> '1' to '0'
                    y == 0L -> '1' to '0'
                    else -> '0' to '1'
                }
                if (bit != expectedBit) invalidZBits.add(n)
                if (carry != expectedCarry) invalidZBits.add(n + 1)
                if (invalidZBits.isNotEmpty()) return invalidZBits
            }
        }
        return emptySet()
    }

    private fun List<Gate>.find(
        op: String? = null,
        i1: String? = null,
        i2: String? = null,
    ): Gate? {
        for (gate in this) {
            if (op != null && op != gate.op) continue
            if (i1 != null && i1 !in gate.i) continue
            if (i2 != null && i2 !in gate.i) continue
            return gate
        }
        return null
    }

    private fun compute2(input: List<String>): String {
        val gates = getGates(input)

        val brokenBits = (0..44)
            .flatMap { i -> gates.validate(i) }
            .distinct()
            .sorted()

        val swappedOutputs = mutableSetOf<String>()
        for (brokenBit in brokenBits) {
            val prevAND = gates.find(op = "AND", i1 = (brokenBit - 1).toWire('x'), i2 = (brokenBit - 1).toWire('y'))
            val prevXOR = gates.find(op = "XOR", i1 = (brokenBit - 1).toWire('x'), i2 = (brokenBit - 1).toWire('y'))
            val m2 = gates.find(op = "AND", i1 = prevXOR?.out)
            val m1 = gates.find(op = "OR", i1 = m2?.out, i2 = prevAND?.out)
            val nXOR = gates.find(op = "XOR", i1 = (brokenBit).toWire('x'), i2 = (brokenBit).toWire('y'))
            val zN = gates.find(op = "XOR", i1 = nXOR?.out, i2 = m1?.out)

            val zWire = brokenBit.toWire('z')
            if (zN == null) {
                val zToFix = gates.single { it.out == zWire }
                val set1 = zToFix.i
                val set2 = setOfNotNull(nXOR?.out, m1?.out)
                swappedOutputs.addAll((set1 - set2) + (set2 - set1))
            } else if (zN.out != zWire) {
                swappedOutputs.add(zWire)
                swappedOutputs.add(zN.out)
            }
        }

        return swappedOutputs.sorted().joinToString(",")
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
        val op: String,
        val out: String,
    ) {
        val i = setOf(i1, i2)

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
