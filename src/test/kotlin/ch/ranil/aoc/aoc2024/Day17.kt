package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.*
import org.junit.jupiter.api.Test
import kotlin.math.pow
import kotlin.test.assertEquals

class Day17 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(
            "4,6,3,5,6,3,5,2,1,0", compute1(
                """
            Register A: 729
            Register B: 0
            Register C: 0

            Program: 0,1,5,4,3,0
        """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part1Puzzle() {
        assertEquals("7,4,2,0,5,0,5,3,7", compute1(puzzleInput))
    }

    @Test
    fun part2Puzzle() {
        Debug.enable()
        assertEquals(202991746427434, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): String {
        val output = runProgram(parse(input))
        return output.joinToString(",")
    }

    private fun compute2(input: List<String>): Long {
        val state = parse(input)

        fun findA(state: State, prgPos: Int): Long? {
            val (a, b, c, program) = state
            if (prgPos > program.size) return a

            for (i in 0..<8) {
                val nextState = state.copy(
                    regA = a * 8 + i,
                    regB = b,
                    regC = c
                )
                val output = runProgram(nextState)
                if (output.first() == program[program.size - prgPos]) {
                    val e = findA(nextState, prgPos + 1)
                    if (e != null) return e
                }
            }
            return null
        }

        val a = findA(state.copy(regA = 0), 1) ?: error("no solution found")

        return a
    }

    private fun runProgram(state: State): List<Int> {
        val output: MutableList<Int> = mutableListOf()
        val instructions = state.instructions
        var pointer = 0
        var regA = state.regA
        var regB = state.regB
        var regC = state.regC

        while (pointer < instructions.size) {
            val opCode = instructions[pointer]
            val operand = instructions[pointer + 1]
            val comboOperand = when {
                operand <= 3 -> operand.toLong()
                operand == 4 -> regA
                operand == 5 -> regB
                operand == 6 -> regC
                else -> error("invalid combo operand: $operand")
            }

            pointer += 2
            when (opCode) {
                0 -> regA = (regA / 2.0.pow(comboOperand.toDouble())).toLong()
                1 -> regB = regB xor operand.toLong()
                2 -> regB = comboOperand % 8
                3 -> if (regA != 0L) pointer = operand
                4 -> regB = regB xor regC
                5 -> output.add((comboOperand % 8).toInt())
                6 -> regB = (regA / 2.0.pow(comboOperand.toDouble())).toLong()
                7 -> regC = (regA / 2.0.pow(comboOperand.toDouble())).toLong()
            }
        }
        return output
    }

    private fun parse(input: List<String>): State {
        val (regA, regB, regC) = input.take(3).map { it.split(" ").last().toLong() }
        val instructions = input.last().split(" ").last().split(",")
            .map(String::toInt)

        return State(regA, regB, regC, instructions)
    }

    private data class State(
        val regA: Long,
        val regB: Long,
        val regC: Long,
        val instructions: List<Int>,
    )
}
