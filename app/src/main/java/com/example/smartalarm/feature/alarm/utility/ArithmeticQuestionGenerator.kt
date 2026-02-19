package com.example.smartalarm.feature.alarm.utility

import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty
import javax.inject.Inject
import kotlin.math.abs
import kotlin.random.Random

class ArithmeticQuestionGenerator @Inject constructor(
    private val numberFormatter: NumberFormatter
) {

    /**
     * Generates an arithmetic question based on difficulty level.
     *
     * @param difficulty The difficulty level for the question.
     * @return A pair containing the formatted question as a string and the correct answer as a Double.
     */
    fun generateQuestion(difficulty: Difficulty): Pair<String, Double> {
        return when (difficulty) {
            Difficulty.EASY -> generateEasyQuestion()
            Difficulty.NORMAL -> generateNormalQuestion()
            Difficulty.HARD -> generateHardQuestion()
            Difficulty.EXPERT -> generateExpertQuestion()
        }
    }

    /**
     * Generates an easy-level arithmetic question using two random numbers between 1 and 9,
     * and randomly selects one of the basic arithmetic operators: addition, subtraction, or multiplication.
     *
     * If subtraction is selected, the operands are adjusted so that the result is always non-negative (i.e., no negative numbers).
     *
     * @return A pair containing the formatted question as a string and the correct answer as a Double.
     *         The answer will always be an integer value since all operands and results are integers.
     */
    fun generateEasyQuestion(): Pair<String, Double> {
        var operand1 = Random.nextInt(1, 10)
        var operand2 = Random.nextInt(1, 10)
        val operator = listOf("+", "-", "*").random()

        // Ensure operand1 is always greater than or equal to operand2 for subtraction
        if (operator == "-" && operand1 < operand2) {
            operand1 = operand2.also { operand2 = operand1 }
        }

        // Localize values of operands
        val localizedOperand1 = numberFormatter.formatLocalizedNumber(operand1.toLong(), false)
        val localizedOperand2 = numberFormatter.formatLocalizedNumber(operand2.toLong(), false)

        val question = when (operator) {
            "+" -> "$localizedOperand1 + $localizedOperand2 = ?"
            "-" -> "$localizedOperand1 - $localizedOperand2 = ?"
            "*" -> "$localizedOperand1 * $localizedOperand2 = ?"
            else -> return "" to 0.0
        }

        val answer = when (operator) {
            "+" -> (operand1 + operand2).toDouble()
            "-" -> (operand1 - operand2).toDouble()
            "*" -> (operand1 * operand2).toDouble()
            else -> 0.0
        }

        return question to answer
    }


    /**
     * Generates a normal-level arithmetic question using two random numbers between 10 and 50.
     * The question is generated with one of the following operations: addition, subtraction, multiplication, or division.
     *
     * If division is selected, it ensures that the result is an integer by using integer division.
     * Division by zero is prevented by setting the divisor to 1 if the randomly selected divisor is 0.
     * If subtraction is selected, the operands are adjusted to avoid negative results.
     *
     * @return A pair containing the formatted question as a string and the correct answer as a Double.
     *         The answer will always be an integer.
     */
    fun generateNormalQuestion(): Pair<String, Double> {
        var operand1 = Random.nextInt(10, 50)
        var operand2 = Random.nextInt(10, 50)
        val operator = listOf("+", "-", "*", "/").random()

        // Ensure no division by zero and integer result for division
        if (operator == "/" && operand2 == 0) {
            operand2 = 1 // Set operand2 to 1 if division by zero
        }

        // Ensure operand1 is always greater than or equal to operand2 for subtraction
        if (operator == "-" && operand1 < operand2) {
            operand1 = operand2.also { operand2 = operand1 } // Swap operands if necessary
        }

        // Localize operands
        val localizedOperand1 = numberFormatter.formatLocalizedNumber(operand1.toLong(), false)
        val localizedOperand2 = numberFormatter.formatLocalizedNumber(operand2.toLong(), false)

        val question = when (operator) {
            "+" -> "$localizedOperand1 + $localizedOperand2 = ?"
            "-" -> "$localizedOperand1 - $localizedOperand2 = ?"
            "*" -> "$localizedOperand1 * $localizedOperand2 = ?"
            "/" -> "$localizedOperand1 / $localizedOperand2 = ?"
            else -> return "" to 0.0
        }

        val answer = when (operator) {
            "+" -> (operand1 + operand2).toDouble()
            "-" -> (operand1 - operand2).toDouble() // No need for abs() now
            "*" -> (operand1 * operand2).toDouble()
            "/" -> (operand1 / operand2).toDouble() // Safe division
            else -> 0.0
        }

        return question to answer
    }


    /**
     * Generates a hard-level arithmetic question using three random numbers.
     * The question involves a combination of addition/subtraction followed by multiplication/division.
     * If division is selected, the dividend is ensured to be a multiple of the divisor to guarantee a whole number result.
     * If subtraction is selected, the operands are adjusted to avoid negative results.
     *
     * @return A pair containing the formatted question as a string and the correct answer as a Double.
     *         The answer will always be an integer.
     */
    fun generateHardQuestion(): Pair<String, Double> {
        val a = Random.nextInt(50, 150)
        val b = Random.nextInt(20, 100)
        val c = Random.nextInt(10, 50)
        val op1 = listOf("+", "-").random()
        val op2 = listOf("*", "/").random()

        // Ensure no division by zero (just in case for any operation where c might be zero in some future cases)
        val safeC = if (c == 0) 1 else c

        // Localize operands
        val localizedA = numberFormatter.formatLocalizedNumber(a.toLong(), false)
        val localizedB = numberFormatter.formatLocalizedNumber(b.toLong(), false)
        val localizedC = numberFormatter.formatLocalizedNumber(safeC.toLong(), false)

        // First operation (addition or subtraction)
        // Ensure that the result of subtraction is always non-negative
        val inner = if (op1 == "+") a + b else (a - b).takeIf { it >= 0 } ?: (b - a)

        // Handle the second operation (multiplication or division)
        return when (op2) {
            "*" -> "($localizedA $op1 $localizedB) * $localizedC = ?" to (inner * safeC).toDouble()
            "/" -> {
                // Ensure that division produces a clean integer (fallback to 1 if not clean)
                val dividend = inner * safeC
                if (dividend % safeC == 0) {
                    // Only divide if it results in a clean integer
                    "(${numberFormatter.formatLocalizedNumber(dividend.toLong(), false)} / $localizedC) = ?" to inner.toDouble()
                } else {
                    // If division isn't clean, return a fallback value instead of re-running the question
                    "(${numberFormatter.formatLocalizedNumber(inner.toLong(), false)} / $localizedC) = ?" to 1.0
                }
            }
            else -> "" to 0.0
        }
    }


    /**
     * Generates an expert-level arithmetic question using five random numbers.
     * The question involves multiple operations with nested expressions: addition, subtraction, multiplication,
     * and ensures all results are integers.
     * If subtraction is selected at any level, the operands are adjusted to avoid negative results.
     *
     * The question construction involves intermediate results that are combined and manipulated to create
     * a complex problem. The resulting question will always yield an integer answer.
     *
     * @return A pair containing the full formatted question string and its correct answer as a Double.
     *         The answer will always be an integer.
     */
    fun generateExpertQuestion(): Pair<String, Double> {
        val a = Random.nextInt(100, 1000)
        val b = Random.nextInt(50, 300)
        val c = Random.nextInt(20, 100)
        val d = Random.nextInt(10, 50)
        val e = Random.nextInt(1, 20)

        val op1 = listOf("+", "-", "*").random()
        val op2 = listOf("*", "+").random()
        val op3 = listOf("-", "+", "*").random()
        val op4 = listOf("+", "-").random()

        // Localize operands
        val localizedA = numberFormatter.formatLocalizedNumber(a.toLong(), false)
        val localizedB = numberFormatter.formatLocalizedNumber(b.toLong(), false)
        val localizedC = numberFormatter.formatLocalizedNumber(c.toLong(), false)
        val localizedD = numberFormatter.formatLocalizedNumber(d.toLong(), false)
        val localizedE = numberFormatter.formatLocalizedNumber(e.toLong(), false)

        val inner1 = when (op1) {
            "+" -> a + b
            "-" -> abs(a - b)  // Ensures no negative result
            "*" -> a * b
            else -> 0
        }

        val inner2 = when (op3) {
            "+" -> c + d
            "-" -> abs(c - d)  // Ensures no negative result
            "*" -> c * d
            else -> 0
        }

        val middle = when (op2) {
            "+" -> inner1 + inner2
            "*" -> inner1 * inner2
            else -> 0
        }

        val final = when (op4) {
            "+" -> middle + e
            "-" -> abs(middle - e)  // Ensures no negative result
            else -> 0
        }

        return "(($localizedA $op1 $localizedB) $op2 ($localizedC $op3 $localizedD)) $op4 $localizedE = ?" to final.toDouble()
    }
}

