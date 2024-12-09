package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.PrintColor.GREEN
import ch.ranil.aoc.common.isEven
import ch.ranil.aoc.common.printColor
import org.junit.jupiter.api.Test
import java.util.LinkedList
import kotlin.test.assertEquals

class Day09 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(1928, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(6432869891895, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val blocks = input.single().flatMapIndexed { index, c ->
            val size = c.digitToInt()
            if (index.isEven()) {
                List(size) { Block.File(id = index / 2) }
            } else {
                List(size) { Block.Free }
            }
        }

        blocks.printBlocks()
        println()

        var checksum = 0L
        var processedFileBlocks = 0
        val fileBlocks = LinkedList(blocks.filterIsInstance<Block.File>())

        for ((index, block) in blocks.withIndex()) {
            if (processedFileBlocks >= fileBlocks.size) break
            when (block) {
                is Block.File -> {
                    checksum += index * block.id
                    processedFileBlocks++
                    print(block.id)
                }

                is Block.Free -> {
                    val blockToInsert = fileBlocks.removeLast()
                    checksum += index * blockToInsert.id
                    printColor(blockToInsert.id, GREEN)
                }
            }
        }

        return checksum
    }

    private fun compute2(input: List<String>): Long {
        TODO()
    }

    private fun List<Block>.printBlocks() {
        forEach { block ->
            when (block) {
                is Block.File -> print(block.id)
                is Block.Free -> print(".")
            }
        }
    }

    sealed interface Block {
        data object Free : Block
        data class File(val id: Int) : Block
    }
}
