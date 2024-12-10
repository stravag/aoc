package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.Debug.debug
import ch.ranil.aoc.common.PrintColor.GREEN
import ch.ranil.aoc.common.isEven
import ch.ranil.aoc.common.printColor
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class Day09 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(1928, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(6432869891895, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(2858, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(6467290479134, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val blocks = input.single().flatMapIndexed { index, c ->
            val size = c.digitToInt()
            if (index.isEven()) {
                List(size) { Block.File(id = index / 2) }
            } else {
                List(size) { Block.Free() }
            }
        }

        debug { blocks.printBlocks() }

        var checksum = 0L
        var processedFileBlocks = 0
        val fileBlocks = LinkedList(blocks.filterIsInstance<Block.File>())

        for ((index, block) in blocks.withIndex()) {
            if (processedFileBlocks >= fileBlocks.size) break
            when (block) {
                is Block.File -> {
                    checksum += block.checksum(index)
                    processedFileBlocks++
                    debug { print(block.id) }
                }

                is Block.Free -> {
                    val blockToInsert = fileBlocks.removeLast()
                    checksum += blockToInsert.checksum(index)
                    debug { printColor(blockToInsert.id, GREEN) }
                }
            }
        }

        return checksum
    }

    private fun compute2(input: List<String>): Long {
        val blocks = mutableListOf<Block>()
        input.single().forEachIndexed { index, c ->
            val size = c.digitToInt()
            if (index.isEven()) {
                blocks.add(Block.File(id = index / 2, size = size))
            } else {
                blocks.add(Block.Free(size = size))
            }
        }

        debug { blocks.printBlocks() }
        blocks.deFragment()

        return blocks.checksum()
    }

    private fun List<Block>.checksum(): Long {
        var checksum = 0L
        var offset = 0
        forEachIndexed { i, block ->
            checksum += block.checksum(i + offset)
            offset += block.size - 1
        }
        return checksum
    }

    private fun MutableList<Block>.deFragment() {
        for (filePos in this.indices.reversed()) {
            val file = this[filePos]
            if (file is Block.File) {
                for (spacePos in 0..<filePos) {
                    val space = this[spacePos]
                    if (space is Block.Free && file.size <= space.size) {
                        this[filePos] = Block.Free(file.size)
                        this[spacePos] = file
                        if (space.size != file.size) {
                            add(spacePos + 1, Block.Free(space.size - file.size))
                        }
                        file.moved = true
                        debug { this.printBlocks() }
                        break
                    }
                }
            }
        }
    }

    private fun List<Block>.printBlocks() {
        forEach { block ->
            block.print()
        }
        println()
    }

    private sealed interface Block {
        val size: Int
        fun checksum(offset: Int): Long
        fun print()

        data class Free(
            override val size: Int = 1
        ) : Block {
            override fun checksum(offset: Int): Long = 0L
            override fun print() = repeat(size) { print('.') }
        }

        data class File(
            val id: Int,
            var moved: Boolean = false,
            override val size: Int = 1,
        ) : Block {

            override fun checksum(offset: Int): Long {
                var checksum = 0L
                for (i in 0..<size) {
                    checksum += (i + offset) * id
                }
                return checksum
            }

            override fun print() {
                if (moved) {
                    repeat(size) { printColor(id, GREEN) }
                } else {
                    repeat(size) { print(id) }
                }
            }
        }
    }
}
