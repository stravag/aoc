package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.*
import org.junit.jupiter.api.Test
import kotlin.math.pow
import kotlin.test.assertEquals

class Day17 : AbstractDay() {

    @Test
    fun part1DummyTest() {
        Debug.enable()
        with(
            Computer(
                regC = 9,
                instructions = listOf(2, 6)
            ).also { it.runProgram() }
        ) {
            assertEquals(1, regB)
        }
        with(
            Computer(
                regA = 10,
                instructions = listOf(5, 0, 5, 1, 5, 4)
            ).also { it.runProgram() }
        ) {
            assertEquals("0,1,2", output)
        }
        with(
            Computer(
                regA = 2024,
                instructions = listOf(0, 1, 5, 4, 3, 0)
            ).also { it.runProgram() }
        ) {
            assertEquals("4,2,5,6,7,7,7,7,3,1,0", output)
            assertEquals(0, regA)
        }
        with(
            Computer(
                regB = 29,
                instructions = listOf(1, 7)
            ).also { it.runProgram() }
        ) {
            assertEquals(26, regB)
        }
        with(
            Computer(
                regB = 2024,
                regC = 43690,
                instructions = listOf(4, 0)
            ).also { it.runProgram() }
        ) {
            assertEquals(44354, regB)
        }
    }

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
    fun part2DummyTest() {
        Debug.enable()
        assertEquals(
            "0,3,5,4,3,0", compute1(
                """
            Register A: 117440
            Register B: 0
            Register C: 0

            Program: 0,3,5,4,3,0
        """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part2Puzzle() {
        Debug.enable()
        assertEquals(99, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): String {
        val computer = Computer.parse(input)
        computer.runProgram()
        return computer.output
    }

    private fun compute2(input: List<String>): Int {
        val computer = Computer.parse(input)
        computer.runProgram()
        Debug.debug { println(computer.output) }
        return 0
    }

    private data class Computer(
        var regA: Int,
        var regB: Int,
        var regC: Int,
        val instructions: List<Instruction>,
        var pointer: Int = 0,
    ) {
        constructor(regA: Int = 0, regB: Int = 0, regC: Int = 0, instructions: List<Int>) : this(
            regA, regB, regC, instructions.chunked(2) { (opCode, operand) -> Instruction(opCode, operand) }, 0
        )

        private var outputs: MutableList<String> = mutableListOf()
        val output: String
            get() = outputs.joinToString(",")

        fun runProgram() {
            while (pointer < instructions.size) {
                perform(instructions[pointer])
            }
        }

        fun perform(instruction: Instruction) {
            println()
            printlnColor("regA=$regA, regB=$regB, regC=$regC", PrintColor.GREEN)
            when (instruction.opCode) {
                0 -> adv(instruction.operand)
                1 -> bxl(instruction.operand)
                2 -> bst(instruction.operand)
                3 -> jnz(instruction.operand)
                4 -> bxc(instruction.operand)
                5 -> out(instruction.operand)
                6 -> bdv(instruction.operand)
                7 -> cdv(instruction.operand)
            }
        }

        fun adv(operand: Int) {
            val comboOperand = combo(operand)
            val newRegA = (regA / 2.0.pow(comboOperand.toDouble())).toInt()
            regA = newRegA
            println("[$pointer] adv: $newRegA ($regA 2^$comboOperand)")
            pointer++
        }

        fun bxl(operand: Int) {
            val newRegB = regB xor operand
            regB = newRegB
            println("[$pointer] bxl: $newRegB ($regB xor $operand)")
            pointer++
        }

        fun bst(operand: Int) {
            val comboOperand = combo(operand)
            regB = comboOperand % 8
            println("[$pointer] bst: $regB ($comboOperand % 8)")
            pointer++
        }

        fun jnz(operand: Int) {
            if (regA == 0) {
                pointer++
            } else {
                pointer = operand
                println("[$pointer] jnz: $operand")
            }
        }

        fun bxc(operand: Int) {
            val newRegB = regB xor regC
            regB = newRegB
            println("[$pointer] bxc: $newRegB ($regB xor $regC)")
            pointer++
        }

        fun out(operand: Int) {
            val comboOperand = combo(operand)
            val result = comboOperand % 8
            outputs.add(result.toString())
            printlnColor("[$pointer] out: $result (operand $operand, comboOperand $comboOperand)", PrintColor.YELLOW)
            pointer++
        }

        fun bdv(operand: Int) {
            val comboOperand = combo(operand)
            val newRegB = (regA / 2.0.pow(comboOperand.toDouble())).toInt()
            regB = newRegB
            println("[$pointer] bdv: $newRegB ($regA 2^$comboOperand)")
            pointer++
        }

        fun cdv(operand: Int) {
            val comboOperand = combo(operand)
            val newRegC = (regA / 2.0.pow(comboOperand.toDouble())).toInt()
            regC = newRegC
            println("[$pointer] cdv: $newRegC ($regA 2^$comboOperand)")
            pointer++
        }

        fun combo(operand: Int): Int {
            return when {
                operand <= 3 -> operand
                operand == 4 -> regA
                operand == 5 -> regB
                operand == 6 -> regC
                else -> error("invalid combo operand: $operand")
            }
        }

        companion object {
            fun parse(input: List<String>): Computer {
                val (regA, regB, regC) = input.take(3).map { it.split(" ").last().toInt() }
                val instructions = input.last().split(" ").last().split(",")
                    .map(String::toInt)
                    .chunked(2) { (opCode, operand) -> Instruction(opCode, operand) }
                return Computer(regA, regB, regC, instructions)
            }
        }
    }

    private data class Instruction(
        val opCode: Int,
        val operand: Int,
    )
}
