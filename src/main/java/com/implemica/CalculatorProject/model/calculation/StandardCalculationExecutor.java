package com.implemica.CalculatorProject.model.calculation;


import com.implemica.CalculatorProject.model.exception.CalculationException;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.implemica.CalculatorProject.model.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.model.validation.DataValidator.isZero;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;

/**
 * The StandardCalculator class performs calculations of a {@link MathOperation}s for the specified
 * {@link BigDecimal} numbers.
 *
 * @author V. Kozina-Kravchenko
 */
public class StandardCalculationExecutor implements CalculationExecutor {

    /**
     * The error message about invalid arguments for the current {@link MathOperation}.
     */
    private static final String INVALID_ARGUMENTS_FOR_OPERATION = "Invalid count of numbers for operation %s, " +
            "first number is %s, second number is %s";

    /**
     * The error message about division by zero occurs.
     */
    private static final String DIVISION_BY_ZERO_ERROR = "Cannot divide by zero";

    /**
     * An error message about situation when result is undefined.
     * For example, division zero by zero.
     */
    private static final String RESULT_UNDEFINED_ERROR = "Result is undefined";

    /**
     * The error message about such {@link MathOperation} not found.
     */
    private static final String NO_SUCH_OPERATION_ERROR = "No such operation";

    /**
     * The error message about invalid input that means an input {@link BigDecimal} number is not allowed for the current
     * {@link MathOperation}. For example, negative number for {@link MathOperation#SQUARE_ROOT} operation.
     */
    private static final String INVALID_INPUT_ERROR = "Invalid input";

    /**
     * The value of first number used for calculations.
     */
    private BigDecimal firstNumber;

    /**
     * The value of second number used for calculations.
     */
    private BigDecimal secondNumber;

    /**
     * The value of a {@link MathOperation} instance that indicates what manipulations with given {@link BigDecimal} numbers
     * need to do.
     */
    private MathOperation operation;

    /**
     * Scale of the {@link BigDecimal} quotient to be returned for result of the division.
     */
    private static final int SCALE = 10100;

    /**
     * The value of 100 represented as {@link BigDecimal} number.
     */
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * Returns the result of calculations of an {@link MathOperation}s with the specified {@link BigDecimal} numbers.
     *
     * @param firstNumber  a number to perform a {@link MathOperation} with
     * @param operation    a Mathematical operation to perform with the given numbers
     * @param secondNumber a number to perform a binary {@link MathOperation} with or null if {@link MathOperation} is unary
     * @return the result of calculations of an {@link MathOperation}s with the specified {@link BigDecimal} numbers
     * @throws CalculationException in cases when of {@link MathOperation} does not exist, division by zero or
     *                              specified invalid arguments
     */
    public BigDecimal calculate(BigDecimal firstNumber, MathOperation operation, BigDecimal secondNumber) throws CalculationException {
        this.operation = operation;
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
        checkArgumentsAreValid();

        BigDecimal result;

        // Binary operations
        if (operation == ADD) {
            result = add();
        } else if (operation == SUBTRACT) {
            result = subtract();
        } else if (operation == MULTIPLY) {
            result = multiply();
        } else if (operation == DIVIDE) {
            result = divide();
        } else if (operation == PERCENT) {
            result = percent();

            // Unary operations
        } else if (operation == NEGATE) {
            result = negate();
        } else if (operation == SQUARE_ROOT) {
            result = sqrt();
        } else if (operation == SQUARE) {
            result = square();
        } else if (operation == REVERSE) {
            result = reverse();
        } else {
            throw new CalculationException(NO_SUCH_OPERATION_ERROR);
        }

        return result;
    }

    /**
     * Checks are the given arguments valid.
     *
     * @throws CalculationException if the given arguments are null or invalid count of {@link BigDecimal} numbers for
     *                              the given {@link MathOperation}
     */
    private void checkArgumentsAreValid() throws CalculationException {
        if (operation == null) {
            throw new CalculationException(NO_SUCH_OPERATION_ERROR);
        }

        boolean isBinaryOperation = operation.isBinary();
        boolean areArgumentsInvalid;

        if (isBinaryOperation) {
            areArgumentsInvalid = (firstNumber == null || secondNumber == null);
        } else {
            areArgumentsInvalid = (firstNumber == null || secondNumber != null);
        }

        if (areArgumentsInvalid) {
            throw new CalculationException(String.format(INVALID_ARGUMENTS_FOR_OPERATION, operation, firstNumber, secondNumber));
        }
    }

    /**
     * Returns the sum of two specified {@link BigDecimal} numbers.
     *
     * @return the sum of two specified {@link BigDecimal} numbers
     */
    private BigDecimal add() {
        return firstNumber.add(secondNumber);
    }

    /**
     * Returns the subtraction of two specified {@link BigDecimal} numbers.
     *
     * @return the subtraction of two specified {@link BigDecimal} numbers
     */
    private BigDecimal subtract() {
        return firstNumber.subtract(secondNumber);
    }

    /**
     * Returns the multiplication of two specified {@link BigDecimal} numbers.
     *
     * @return the multiplication of two specified numbers
     */
    private BigDecimal multiply() {
        return firstNumber.multiply(secondNumber);
    }

    /**
     * Returns the division of two specified {@link BigDecimal} numbers.
     *
     * @return the division of two specified {@link BigDecimal} numbers
     * @throws CalculationException if divisor or both arguments are equal to zero
     */
    private BigDecimal divide() throws CalculationException {
        if (isZero(firstNumber) && isZero(secondNumber)) {
            throw new CalculationException(RESULT_UNDEFINED_ERROR);
        }

        if (isZero(secondNumber)) {
            throw new CalculationException(DIVISION_BY_ZERO_ERROR);
        }

        return firstNumber.divide(secondNumber, SCALE, ROUND_HALF_UP);
    }

    /**
     * Returns the square calculated for the specified {@link BigDecimal} number.
     *
     * @return the square for the specified {@link BigDecimal} number
     */
    private BigDecimal square() {
        return firstNumber.pow(2);
    }

    /**
     * Returns the negated given {@link BigDecimal} number.
     *
     * @return the negated given {@link BigDecimal} number
     */
    private BigDecimal negate() {
        return firstNumber.negate();
    }

    /**
     * Returns the square root calculated for the given {@link BigDecimal} number. Source of square root algorithm for {@link BigDecimal} is
     * <a href="https://www.java-forums.org/advanced-java/44345-square-rooting-bigdecimal.html">square rooting a BigDecimal</a>
     *
     * @return the square root calculated for the given {@link BigDecimal} number
     * @throws CalculationException if the given {@link BigDecimal} number is negative
     */
    private BigDecimal sqrt() throws CalculationException {
        if (firstNumber.compareTo(ZERO) < 0) {
            throw new CalculationException(INVALID_INPUT_ERROR);
        }

        if (isZero(firstNumber)) {
            return ZERO;
        }

        // n = x*(10^(2*scale))
        BigInteger n = firstNumber.movePointRight(SCALE << 1).toBigInteger();

        // The first approximation is the upper half of n.
        int bits = (n.bitLength() + 1) >> 1;
        BigInteger ix = n.shiftRight(bits);
        BigInteger ixPrev;

        // Loop until the approximations converge
        // (two successive approximations are equal after rounding).
        do {
            ixPrev = ix;

            // x = (x + n/x)/2
            ix = ix.add(n.divide(ix)).shiftRight(1);

            Thread.yield();
        } while (ix.compareTo(ixPrev) != 0);

        return new BigDecimal(ix, SCALE);
    }

    /**
     * Returns the {@link BigDecimal} number that is a specified percentage calculated for the given {@link BigDecimal} number.
     *
     * @return the {@link BigDecimal} number that is a specified percentage calculated for the given {@link BigDecimal} number
     */
    private BigDecimal percent() {
        if (isZero(firstNumber) || isZero(secondNumber)) {
            return ZERO;
        }
        // Convert percentage to an absolute value
        BigDecimal absolutePercentageValue = secondNumber.divide(ONE_HUNDRED, SCALE, ROUND_HALF_UP);

        return firstNumber.multiply(absolutePercentageValue);
    }

    /**
     * Returns the {@link BigDecimal} number calculated as a division of 1 by the given {@link BigDecimal} number.
     *
     * @return the {@link BigDecimal} number calculated as a division of 1 by the given {@link BigDecimal} number
     * @throws CalculationException if {@link BigDecimal} number is equal to zero
     */
    private BigDecimal reverse() throws CalculationException {
        if (isZero(firstNumber)) {
            throw new CalculationException(DIVISION_BY_ZERO_ERROR);
        }

        return ONE.divide(firstNumber, SCALE, ROUND_HALF_UP);
    }
}