package com.implemica.CalculatorProject.model.calculation;


import com.implemica.CalculatorProject.model.exception.CalculationException;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.implemica.CalculatorProject.model.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.model.validation.DataValidator.isZero;
import static java.lang.String.format;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;

/**
 * The {@code StandardCalculator} class is an implementation of {@link CalculationExecutor} interface. Performs some
 * Mathematical operations such as multiplying, dividing, changing number's sign to an opposite and etc.
 *
 * @author V. Kozina-Kravchenko
 */
public class StandardCalculationExecutor implements CalculationExecutor {

    /**
     * The error message about invalid count of numbers.
     */
    private static final String INVALID_ARGUMENTS_COUNT = "Invalid count of numbers for your operation. " +
            "Your operation is %s, count of numbers is %s";

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
     * The error message about such operation not found.
     */
    private static final String NO_SUCH_OPERATION_ERROR = "No such operation";

    /**
     * The error message about invalid input that means an input string contains not only number.
     */
    private static final String INVALID_INPUT_ERROR = "Invalid input";

    /**
     * The list of one or two numbers for calculations.
     */
    private BigDecimal[] numbers;

    /**
     * The value of a Mathematical operation type.
     */
    private MathOperation operation;

    /**
     * Scale of the {@code BigDecimal} quotient to be returned for result of the division.
     */
    private static final int SCALE = 10100;

    /**
     * The value of 100 represented as {@link BigDecimal} number.
     */
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    /**
     * Returns the result of calculations of an Mathematical operations with the specified numbers.
     *
     * @param operation a Mathematical operation to perform with the given numbers
     * @param numbers   a numbers to perform a Mathematical operation with
     * @return the result of calculations of an Mathematical operations with the specified numbers
     * @throws CalculationException in cases when of operation does not exist, division by zero or
     *                              specified invalid arguments
     */
    public BigDecimal calculate(MathOperation operation, BigDecimal... numbers) throws CalculationException {
        this.operation = operation;
        this.numbers = numbers;
        checkArgumentsAreValid();

        BigDecimal result;

        if (operation == ADD) {
            result = add(numbers[0], numbers[1]);

        } else if (operation == SUBTRACT) {
            result = subtract(numbers[0], numbers[1]);

        } else if (operation == MULTIPLY) {
            result = multiply(numbers[0], numbers[1]);

        } else if (operation == DIVIDE) {
            result = divide(numbers[0], numbers[1]);

        } else if (operation == PERCENT) {
            result = percent(numbers[0], numbers[1]);

            // Unary operations
        } else if (operation == NEGATE) {
            result = negate(numbers[0]);

        } else if (operation == SQUARE_ROOT) {
            result = sqrt(numbers[0]);

        } else if (operation == SQUARE) {
            result = square(numbers[0]);

        } else if (operation == REVERSE) {
            result = reverse(numbers[0]);

        } else {
            throw new CalculationException(NO_SUCH_OPERATION_ERROR);
        }
        return result;
    }

    /**
     * Checks are the given arguments valid.
     *
     * @throws CalculationException if the given arguments are null or invalid count of number for the given operation
     */
    private void checkArgumentsAreValid() throws CalculationException {
        if (operation == null) {
            throw new CalculationException(NO_SUCH_OPERATION_ERROR);
        }
        if (numbers == null || numbers.length == 0) {
            throw new CalculationException(format(INVALID_ARGUMENTS_COUNT, operation, 0));
        }
        if (operation.isBinary() && numbers.length != 2 ||
                !operation.isBinary() && numbers.length != 1) {
            throw new CalculationException(format(INVALID_ARGUMENTS_COUNT, operation, numbers.length));
        }
    }

    /**
     * Returns the sum of two specified numbers.
     *
     * @param firstNumber  a value of the first argument for sum
     * @param secondNumber a value of the second argument for sum
     * @return the sum of two specified numbers
     */
    private BigDecimal add(BigDecimal firstNumber, BigDecimal secondNumber) {
        return firstNumber.add(secondNumber);
    }

    /**
     * Returns the subtraction of two specified numbers.
     *
     * @param firstNumber  a value of the first argument for subtraction
     * @param secondNumber a value of the second argument for subtraction
     * @return the subtraction of two specified numbers
     */
    private BigDecimal subtract(BigDecimal firstNumber, BigDecimal secondNumber) {
        return firstNumber.subtract(secondNumber);
    }

    /**
     * Returns the multiplication of two specified numbers.
     *
     * @param firstNumber  a value of the first argument for multiplication
     * @param secondNumber a value of the second argument for multiplication
     * @return the multiplication of two specified numbers
     */
    private BigDecimal multiply(BigDecimal firstNumber, BigDecimal secondNumber) {
        return firstNumber.multiply(secondNumber);
    }

    /**
     * Returns the division of two specified numbers.
     *
     * @param firstNumber  a value of the first argument for division
     * @param secondNumber a value of the second argument for division
     * @return the division of two specified numbers
     * @throws CalculationException if divisor or both arguments are equal to zero
     */
    private BigDecimal divide(BigDecimal firstNumber, BigDecimal secondNumber) throws CalculationException {
        if (isZero(firstNumber) && isZero(secondNumber)) {
            throw new CalculationException(RESULT_UNDEFINED_ERROR);
        }
        if (isZero(secondNumber)) {
            throw new CalculationException(DIVISION_BY_ZERO_ERROR);
        }
        return firstNumber.divide(secondNumber, SCALE, ROUND_HALF_UP);
    }

    /**
     * Returns the square calculated for the specified number.
     *
     * @param base a number to calculate square for
     * @return the square for the specified number
     */
    private BigDecimal square(BigDecimal base) {
        return base.abs().pow(2);
    }

    /**
     * Returns the negated given number.
     *
     * @param number a number to negate
     * @return the negated given number
     */
    private BigDecimal negate(BigDecimal number) {
        return number.negate();
    }

    /**
     * Returns the square root calculated for the given number. Source of square root for {@link BigDecimal} is
     * <a href="https://www.java-forums.org/advanced-java/44345-square-rooting-bigdecimal.html">square rooting a BigDecimal</a>
     *
     * @param x a number to calculate square root for
     * @return the square root calculated for the given number
     * @throws CalculationException if the given number is negative
     */
    private BigDecimal sqrt(BigDecimal x) throws CalculationException {
        if (x.compareTo(ZERO) < 0) {
            throw new CalculationException(INVALID_INPUT_ERROR);
        }
        if (isZero(x)) {
            return ZERO;
        }

        // n = x*(10^(2*scale))
        BigInteger n = x.movePointRight(SCALE << 1).toBigInteger();

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
     * Returns the number that is a specified percentage calculated for the given number.
     *
     * @param base    a number to calculate percentage from
     * @param percent a count of percents to calculate
     * @return the number that is a specified percentage calculated for the given number
     */
    private BigDecimal percent(BigDecimal base, BigDecimal percent) {
        if (isZero(base) || isZero(percent)) {
            return ZERO;
        }
        // Convert percentage to an absolute value
        BigDecimal absolutePercentageValue = percent.divide(ONE_HUNDRED, SCALE, ROUND_HALF_UP);
        return base.multiply(absolutePercentageValue);
    }

    /**
     * Returns the number calculated as a division of 1 by the given number.
     *
     * @param base a number to reverse
     * @return the number calculated as a division of 1 by the given number
     * @throws CalculationException if number is equal to zero
     */
    private BigDecimal reverse(BigDecimal base) throws CalculationException {
        if (isZero(base)) {
            throw new CalculationException(DIVISION_BY_ZERO_ERROR);
        }
        return ONE.divide(base, SCALE, ROUND_HALF_UP);
    }
}