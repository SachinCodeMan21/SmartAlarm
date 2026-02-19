package com.example.smartalarm.feature.alarm.utility

import com.example.smartalarm.core.utility.extension.toLocalizedString
import kotlin.math.abs
import kotlin.random.Random

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
    val operand1 = Random.nextInt(1, 10)
    val operand2 = Random.nextInt(1, 10)
    val operator = listOf("+", "-", "*").random()

    // Localized values of operands
    val localizedOperand1 = operand1.toLong().toLocalizedString()
    val localizedOperand2 = operand2.toLong().toLocalizedString()

    val question = when (operator) {
        "+" -> "$localizedOperand1 + $localizedOperand2 = ?"
        "-" -> "$localizedOperand1 - $localizedOperand2 = ?"
        "*" -> "$localizedOperand1 * $localizedOperand2 = ?"
        else -> return "" to 0.0
    }

    val answer = when (operator) {
        "+" -> (operand1 + operand2).toDouble()
        "-" -> abs(operand1 - operand2).toDouble() // To ensure no negative result
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
    val operand1 = Random.nextInt(10, 50)
    val operand2 = Random.nextInt(10, 50)
    val operator = listOf("+", "-", "*", "/").random()

    // Ensure no division by zero and integer result
    val safeOperand2 = if (operand2 == 0) 1 else operand2

    // Localize operands
    val localizedOperand1 = operand1.toLong().toLocalizedString()
    val localizedOperand2 = operand2.toLong().toLocalizedString()

    val question = when (operator) {
        "+" -> "$localizedOperand1 + $localizedOperand2 = ?"
        "-" -> "$localizedOperand1 - $localizedOperand2 = ?"
        "*" -> "$localizedOperand1 * $localizedOperand2 = ?"
        "/" -> "$localizedOperand1 / $safeOperand2 = ?"
        else -> return "" to 0.0
    }

    val answer = when (operator) {
        "+" -> (operand1 + operand2).toDouble()
        "-" -> abs(operand1 - operand2).toDouble() // Ensure no negative result
        "*" -> (operand1 * operand2).toDouble()
        "/" -> (operand1 / safeOperand2).toDouble() // Perform division safely
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

    // Localize operands
    val localizedA = a.toLong().toLocalizedString()
    val localizedB = b.toLong().toLocalizedString()
    val localizedC = c.toLong().toLocalizedString()

    // First operation (addition or subtraction)
    val inner = if (op1 == "+") a + b else abs(a - b)  // Ensures no negative result

    return when (op2) {
        "*" -> "($localizedA $op1 $localizedB) * $localizedC = ?" to (inner * c).toDouble()
        "/" -> {
            val dividend = inner * c  // Make sure division produces a whole number (integer)
            "($dividend / $localizedC) = ?" to inner.toDouble()
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
    val localizedA = a.toLong().toLocalizedString()
    val localizedB = b.toLong().toLocalizedString()
    val localizedC = c.toLong().toLocalizedString()
    val localizedD = d.toLong().toLocalizedString()
    val localizedE = e.toLong().toLocalizedString()

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

