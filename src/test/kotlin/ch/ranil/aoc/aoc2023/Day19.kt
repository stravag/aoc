package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

class Day19 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(19114, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(472630, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(167409079868000, compute2(testInput))
    }

    @Test
    fun part2Tests() {
        Workflow.parse("qqz{s>2770:qs,m<1801:hdj,R}").possibleCombinations()

        """
            px{a<2006:qkq,m>2090:A,rfg}
            pv{a>1716:R,A}
            lnx{m>1548:A,A}
            rfg{s<537:gd,x>2440:R,A}
            qs{s>3448:A,lnx}
            qkq{x<1416:A,crn}
            crn{x>2662:A,R}
            in{s<1351:px,qqz}
            qqz{s>2770:qs,m<1801:hdj,R}
            gd{a>3333:R,R}
            hdj{m>838:A,pv}
        """.trimIndent()
            .lines()
            .map { Workflow.parse(it) }
            .map { it.name to it.possibleCombinations() }
            .forEach { (name, comb) -> println("${name.name} = ${comb.first}, ${comb.second.map { "${it.key.name} = ${it.value}" }}") }
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val (workflows, ratings) = input.parse()

        return ratings.filter {
            isAccepted(it, WorkflowName("in"), workflows)
        }.sumOf {
            it.x + it.m + it.a + it.s
        }
    }

    private fun isAccepted(
        rating: PartsRating,
        workflowName: WorkflowName,
        workflows: Map<WorkflowName, Workflow>,
    ): Boolean {
        val workflow = workflows.getValue(workflowName)
        return when (val nextWorkflow = workflow.process(rating)) {
            Accepted -> true
            Rejected -> false
            is WorkflowName -> isAccepted(rating, nextWorkflow, workflows)
        }
    }

    private fun compute2(input: List<String>): Long {
        val (workflows, _) = input.parse()
        return countPossibleCombinations(PartsRatingRanges(), WorkflowName("in"), workflows)
    }

    private fun countPossibleCombinations(
        knownCombinations: PartsRatingRanges,
        workflowName: WorkflowName,
        workflows: Map<WorkflowName, Workflow>,
    ): Long {
        val workflow = workflows.getValue(workflowName)

        val (combinationsForWorkflow, nextWorkflows) = workflow.possibleCombinations()

        TODO()
    }

    private fun List<String>.parse(): Pair<Map<WorkflowName, Workflow>, List<PartsRating>> {
        val workflows = this
            .takeWhile { it.isNotBlank() }
            .map { Workflow.parse(it) }
            .associateBy { it.name }

        val ratings = this.drop(workflows.size + 1)
            .map { ratingsString ->
                val (x, m, a, s) = Regex("[0-9]+").findAll(ratingsString).map { it.value }.toList()
                PartsRating(
                    x = x.toInt(),
                    m = m.toInt(),
                    a = a.toInt(),
                    s = s.toInt(),
                )
            }

        return workflows to ratings
    }

    private data class Workflow(
        val name: WorkflowName,
        val statements: List<Statement>,
    ) {
        fun process(rating: PartsRating): WorkFlowResponse {
            for (statement in statements) {
                val workFlowResponse = statement.process(rating)
                if (workFlowResponse != null) return workFlowResponse
            }
            throw IllegalStateException("Workflow $this didn't yield response for $rating")
        }

        fun possibleCombinations(): Pair<PartsRatingRanges, Map<WorkflowName, PartsRatingRanges>> {
            return statements.windowed(size = 2, step = 1) { (ifStmt, elseStmt) ->
                var ranges = PartsRatingRanges()
                val nextWorkflowCombinations = mutableMapOf<WorkflowName, PartsRatingRanges>()
                if (ifStmt.response is Accepted) {
                    ranges = ranges.intersect(ifStmt.rangeForTrue())
                }
                if (ifStmt.response is WorkflowName) {
                    nextWorkflowCombinations.compute(ifStmt.response as WorkflowName) { _, c ->
                        (c ?: PartsRatingRanges()).intersect(ifStmt.rangeForTrue())
                    }
                }
                if (elseStmt.response is Accepted) {
                    ranges = ranges.intersect(ifStmt.rangeForTrue())
                }
                if (elseStmt.response is WorkflowName) {
                    nextWorkflowCombinations.compute(elseStmt.response as WorkflowName) { _, c ->
                        (c ?: PartsRatingRanges()).intersect(ifStmt.rangeForFalse())
                    }
                }
                ranges to nextWorkflowCombinations
            }.reduce { acc, map ->
                acc.second.putAll(map.second)
                (acc.first.intersect(map.first)) to acc.second
            }
        }

        companion object {
            fun parse(s: String): Workflow {
                val (_, name, operationsString) = Regex("([a-z]+)\\{(.*)}").find(s)?.groupValues.orEmpty()
                val operations = operationsString
                    .split(",")
                    .map { stmt ->
                        if (stmt.contains(":")) {
                            IfStatement.of(stmt)
                        } else {
                            ElseStatement.of(stmt)
                        }
                    }
                return Workflow(
                    name = WorkflowName(name),
                    statements = operations,
                )
            }
        }
    }

    private sealed interface Statement {
        val response: WorkFlowResponse
        fun rangeForTrue(): PartsRatingRanges
        fun rangeForFalse(): PartsRatingRanges
        fun process(rating: PartsRating): WorkFlowResponse?
    }

    private data class IfStatement(
        private val r: String,
        private val comparator: String,
        private val number: Int,
        override val response: WorkFlowResponse,
    ) : Statement {
        override fun process(rating: PartsRating): WorkFlowResponse? {
            val value = rating.get(r)
            return when (comparator) {
                "<" -> if (value < number) response else null
                ">" -> if (value > number) response else null
                else -> throw IllegalArgumentException("unexpected comparator $comparator in $this")
            }
        }

        override fun rangeForTrue(): PartsRatingRanges {
            val range = when (comparator) {
                "<" -> 1..<number
                ">" -> number..4000
                else -> throw IllegalArgumentException("unexpected comparator $comparator in $this")
            }
            return PartsRatingRanges.build(r, range)
        }

        override fun rangeForFalse(): PartsRatingRanges {
            val range = when (comparator) {
                ">" -> 1..number
                "<" -> number..4000
                else -> throw IllegalArgumentException("unexpected comparator $comparator in $this")
            }
            return PartsRatingRanges.build(r, range)
        }

        companion object {
            fun of(s: String): IfStatement {
                val (_, r, comparator, number, workflowName) = Regex("([a-z]+)([<>])([0-9]+):([a-zA-Z]+)")
                    .find(s)?.groupValues.orEmpty()

                return IfStatement(r, comparator, number.toInt(), WorkFlowResponse.of(workflowName))
            }
        }
    }

    private data class ElseStatement(
        override val response: WorkFlowResponse,
    ) : Statement {
        override fun process(rating: PartsRating): WorkFlowResponse {
            return response
        }

        override fun rangeForTrue(): PartsRatingRanges = PartsRatingRanges.empty()
        override fun rangeForFalse(): PartsRatingRanges = PartsRatingRanges.empty()

        companion object {
            fun of(s: String): ElseStatement {
                return ElseStatement(WorkFlowResponse.of(s))
            }
        }
    }

    private sealed interface WorkFlowResponse {
        companion object {
            fun of(s: String) = when (s) {
                "A" -> Accepted
                "R" -> Rejected
                else -> WorkflowName(s)
            }
        }
    }

    private data object Accepted : WorkFlowResponse
    private data object Rejected : WorkFlowResponse
    private data class WorkflowName(val name: String) : WorkFlowResponse

    private data class PartsRating(
        val x: Int,
        val m: Int,
        val a: Int,
        val s: Int,
    ) {
        fun get(i: String): Int {
            return when (i) {
                "x" -> x
                "m" -> m
                "a" -> a
                "s" -> s
                else -> throw IllegalArgumentException("unexpected ratings value: $i")
            }
        }
    }

    private data class PartsRatingRanges(
        val x: IntRange = 1..4000,
        val m: IntRange = 1..4000,
        val a: IntRange = 1..4000,
        val s: IntRange = 1..4000,
    ) {
        fun intersect(other: PartsRatingRanges): PartsRatingRanges {
            return PartsRatingRanges(
                x = IntRange(max(x.first, other.x.first), min(x.last, other.x.last)),
                m = IntRange(max(m.first, other.m.first), min(m.last, other.m.last)),
                a = IntRange(max(a.first, other.a.first), min(a.last, other.a.last)),
                s = IntRange(max(s.first, other.s.first), min(s.last, other.s.last)),
            )
        }

        companion object {
            fun build(r: String, range: IntRange): PartsRatingRanges {
                return when (r) {
                    "x" -> PartsRatingRanges(x = range)
                    "m" -> PartsRatingRanges(m = range)
                    "a" -> PartsRatingRanges(a = range)
                    "s" -> PartsRatingRanges(s = range)
                    else -> throw IllegalArgumentException("unexpected ratings value: $r")
                }
            }

            fun empty(): PartsRatingRanges {
                return PartsRatingRanges(IntRange.EMPTY, IntRange.EMPTY, IntRange.EMPTY, IntRange.EMPTY)
            }
        }
    }
}
