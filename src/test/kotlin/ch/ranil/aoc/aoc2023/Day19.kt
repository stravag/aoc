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
            isAccepted(it, "in", workflows)
        }.sumOf {
            it.x + it.m + it.a + it.s
        }
    }

    private fun isAccepted(
        rating: PartsRating,
        workflowName: WorkflowName,
        workflows: Map<WorkflowName, Workflow>,
    ): Boolean {
        val workflow = workflows[workflowName] ?: return workflowName == "A"

        val nextWorkflow = workflow.process(rating)
        return isAccepted(rating, nextWorkflow, workflows)
    }

    private fun compute2(input: List<String>): Long {
        val (workflows, _) = input.parse()
        return countPossibleCombinations(0L, "in", workflows)
    }

    private fun countPossibleCombinations(
        knownPossibleCombinations: Long,
        workflowName: WorkflowName,
        workflows: Map<WorkflowName, Workflow>,
    ): Long {
        val workflow = workflows[workflowName] ?: return if (workflowName == "A") knownPossibleCombinations else 0
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
        val name: String,
        private val ifCascade: List<String>,
        private val elseStmt: String,
    ) {
        fun process(rating: PartsRating): WorkflowName {
            for (stmt in ifCascade) {
                val nextWorkFlowName = processIf(stmt, rating)
                if (nextWorkFlowName != null) return nextWorkFlowName
            }
            return elseStmt
        }

        private fun processIf(stmt: String, rating: PartsRating): WorkflowName? {
            val (_, r, comparator, number, workflowName) = Regex("([a-z]+)([<>])([0-9]+):([a-zA-Z]+)").find(stmt)?.groupValues.orEmpty()
            val value = rating.get(r)
            return when (comparator) {
                "<" -> if (value < number.toInt()) workflowName else null
                ">" -> if (value > number.toInt()) workflowName else null
                else -> throw IllegalArgumentException("unexpected comparator $comparator in $this")
            }
        }

        companion object {
            fun parse(s: String): Workflow {
                val (_, name, operationsString) = Regex("([a-z]+)\\{(.*)}").find(s)?.groupValues.orEmpty()
                val operations = operationsString.split(",")
                return Workflow(
                    name = name,
                    ifCascade = operations.dropLast(1),
                    elseStmt = operations.last(),
                )
            }
        }
    }

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

private typealias WorkflowName = String
