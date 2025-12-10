package ch.ranil.aoc.common.types

data class Line(val a: Point, val b: Point) {
    val direction: Direction = when {
        a.col == b.col -> Direction.N
        a.row == b.row -> Direction.E
        else -> error("$a, $b: not a vertical or horizontal line")
    }

    operator fun contains(p: Point): Boolean {
        return when (direction) {
            Direction.N -> p.col == a.col
                    && p.row >= minOf(a.row, b.row)
                    && p.row <= maxOf(a.row, b.row)

            Direction.E -> p.row == a.row
                    && p.col >= minOf(a.col, b.col)
                    && p.col <= maxOf(a.col, b.col)

            else -> error("unexpected direction")
        }
    }

    fun crosses(other: Line): Boolean {
        // Determine orientation of each line
        val thisIsHorizontal = a.row == b.row
        val otherIsHorizontal = other.a.row == other.b.row

        // Parallel lines don't cross
        if (thisIsHorizontal == otherIsHorizontal) {
            return false
        }

        // One line must be horizontal, the other vertical
        val (horizontal, vertical) = if (thisIsHorizontal) {
            this to other
        } else {
            other to this
        }

        // Get the bounds of each line
        val hRow = horizontal.a.row
        val hColMin = minOf(horizontal.a.col, horizontal.b.col)
        val hColMax = maxOf(horizontal.a.col, horizontal.b.col)

        val vCol = vertical.a.col
        val vRowMin = minOf(vertical.a.row, vertical.b.row)
        val vRowMax = maxOf(vertical.a.row, vertical.b.row)

        // Check if they intersect
        val intersects = vCol in hColMin..hColMax && hRow in vRowMin..vRowMax

        if (!intersects) {
            return false
        }

        // For a cross (not T or L), the intersection point must NOT be at the endpoints
        // The intersection point is (hRow, vCol)
        val isHorizontalEndpoint = (vCol == hColMin || vCol == hColMax)
        val isVerticalEndpoint = (hRow == vRowMin || hRow == vRowMax)

        // It's a cross only if the intersection is NOT at ANY endpoint
        return !isHorizontalEndpoint && !isVerticalEndpoint
    }
}
