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

    fun turn90(): Direction = when (this) {
        N -> E
        E -> S
        S -> W
        W -> N
    }
}

