package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
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
        return countPossibleCombinations(1L, WorkflowName("in"), workflows)
    }

    private fun countPossibleCombinations(
        knownPossibleCombinations: Long,
        workflowName: WorkflowName,
        workflows: Map<WorkflowName, Workflow>,
    ): Long {
        val workflow = workflows.getValue(workflowName)

        val (addedCombinationsForWorkflow, nextWorkflows) = workflow.possibleCombinations()

        return nextWorkflows.fold(
            initial = knownPossibleCombinations * addedCombinationsForWorkflow
        ) { combinations, nextWorkflow ->
            combinations * countPossibleCombinations(combinations, nextWorkflow, workflows)
        }
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

        fun possibleCombinations(): Pair<Long, List<WorkflowName>> {
            return statements
                .filter { it.response != Rejected }
                .map {
                    it.combinationsToFullFil() to it.response
                }
                .fold(0L to mutableListOf<WorkflowName>()) { (knownCombinations, responses), (combinations, response) ->
                    if (response is WorkflowName) responses.add(response)
                    (knownCombinations * combinations) to responses
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
        fun combinationsToFullFil(): Long
        fun process(rating: PartsRating): WorkFlowResponse?
    }

    private data class IfStatement(
        private val r: String,
        private val comparator: String,
        private val number: Int,
        override val response: WorkFlowResponse
    ) : Statement {
        override fun process(rating: PartsRating): WorkFlowResponse? {
            val value = rating.get(r)
            return when (comparator) {
                "<" -> if (value < number) response else null
                ">" -> if (value > number) response else null
                else -> throw IllegalArgumentException("unexpected comparator $comparator in $this")
            }
        }

        override fun combinationsToFullFil(): Long {
            return when (comparator) {
                "<" -> number - 1L
                ">" -> 4000L - number
                else -> throw IllegalArgumentException("unexpected comparator $comparator in $this")
            }
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
        override val response: WorkFlowResponse
    ) : Statement {
        override fun process(rating: PartsRating): WorkFlowResponse {
            return response
        }

        override fun combinationsToFullFil(): Long {
            return 1L
        }

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
}
