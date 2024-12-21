package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day22 : AbstractDay() {

    @Test
    fun part1Dummy() {
        Debug.enable()
        val secret = Secret(123)
        assertEquals(15887950, secret.next())
        assertEquals(16495136, secret.next())
        assertEquals(527345, secret.next())
        assertEquals(704524, secret.next())
        assertEquals(1553684, secret.next())
        assertEquals(12683156, secret.next())
        assertEquals(11100544, secret.next())
        assertEquals(12249484, secret.next())
        assertEquals(7753432, secret.next())
        assertEquals(5908254, secret.next())
    }

    @Test
    fun part1Test() {
        Debug.enable()
        val input = "1\n10\n100\n2024".lines()
        assertEquals(37327623, compute1(input))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(20332089158, compute1(puzzleInput))
    }

    @Test
    fun part2Dummy() {
        fun Secret.nextPart2Dummy(): String {
            val (p, d) = nextPriceAndDiff()
            return "$secret".padStart(8) + ": $p ($d)"
        }

        Debug.enable()
        val secret = Secret(123)
        println("${secret.secret}".padStart(8) + ": ${secret.price()}")
        repeat(9) {
            println(secret.nextPart2Dummy())
        }
    }

    @Test
    fun part2Test() {
        Debug.enable()
        val input = "1\n10\n100\n2024".lines()
        assertEquals(0, compute2(input))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return input.sumOf {
            val secret = Secret(it.toLong())
            repeat(2000) { secret.next() }
            secret.secret
        }
    }

    private fun compute2(input: List<String>): Int {
        val sequencesPriceMap = mutableMapOf<Sequence, Int>()
        input.forEach {
            val seen = HashSet<Sequence>()
            val secret = Secret(it.toLong())

            val (_, d1) = secret.nextPriceAndDiff()
            val (_, d2) = secret.nextPriceAndDiff()
            val (_, d3) = secret.nextPriceAndDiff()
            val (p, d4) = secret.nextPriceAndDiff()
            var sequence = Sequence(d1, d2, d3, d4)
            sequencesPriceMap[sequence] = p
            repeat(2000 - 4) {
                val (price, diff) = secret.nextPriceAndDiff()
                sequence = sequence.next(diff)
                if (seen.add(sequence)) {
                    sequencesPriceMap.compute(sequence) { _, v -> (v ?: 0) + price }
                }
            }
        }
        val (sequence, maxPrice) = sequencesPriceMap.maxBy { it.value }
        println(sequence)
        return maxPrice
    }

    data class Sequence(
        val d1: Int,
        val d2: Int,
        val d3: Int,
        val d4: Int,
    ) {
        fun next(d: Int): Sequence {
            return Sequence(d2, d3, d4, d)
        }

        override fun toString(): String {
            return "$d1,$d2,$d3,$d4"
        }
    }

    class Secret(var secret: Long) {
        fun next(): Long {
            mix(secret * 64)
            prune()

            mix(secret / 32)
            prune()

            mix(secret * 2048)
            prune()

            return secret
        }

        fun nextPriceAndDiff(): Pair<Int, Int> {
            val oldPrice = price()
            next()
            val newPrice = price()
            return newPrice to (newPrice - oldPrice)
        }

        fun price(): Int {
            return (secret % 10).toInt()
        }

        private fun mix(result: Long) {
            this.secret = result xor secret
        }

        private fun prune() {
            this.secret %= 16777216
        }
    }
}
