package ch.ranil.aoc.common.types

typealias MovePointBySteps = (Point, Int) -> Point

enum class Direction(val indicator: Char) {
    N('^'), E('>'), S('v'), W('<');

    val opposite
        get() = when (this) {
            N -> S
            E -> W
            S -> N
            W -> E
        }

    fun turnRight(): Direction = when (this) {
        N -> E
        E -> S
        S -> W
        W -> N
    }

    fun turnLeft(): Direction = when (this) {
        N -> W
        E -> N
        S -> E
        W -> S
    }

    companion object {
        fun of(indicator: Char): Direction {
            return entries.single { it.indicator == indicator }
        }
    }
}

