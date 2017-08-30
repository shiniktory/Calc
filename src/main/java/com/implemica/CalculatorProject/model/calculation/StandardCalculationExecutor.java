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
 * The StandardCalculator class performs calculations of a {@link MathOperation}s for the specified
 * {@link BigDecimal} numbers.
 *
 * @author V. Kozina-Kravchenko
 */
public class StandardCalculationExecutor implements CalculationExecutor {

    /**
     * The error message about invalid count of {@link BigDecimal} numbers for the current {@link MathOperation}.
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
     * The error message about such {@link MathOperation} not found.
     */
    private static final String NO_SUCH_OPERATION_ERROR = "No such operation";

    /**
     * The error message about invalid input that means an input {@link BigDecimal} number is not allowed for the current
     * {@link MathOperation}. For example, negative number for {@link MathOperation#SQUARE_ROOT} operation.
     */
    private static final String INVALID_INPUT_ERROR = "Invalid input";

    /**
     * The list of one or two {@link BigDecimal} numbers for calculations.
     */
    private BigDecimal[] numbers;

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
     * @param operation a {@link MathOperation} to perform with the given {@link BigDecimal} numbers
     * @param numbers   a {@link BigDecimal} numbers to perform a {@link MathOperation} with
     * @return the result of calculations of an {@link MathOperation}s with the specified {@link BigDecimal} numbers
     * @throws CalculationException in cases when of {@link MathOperation} does not exist, division by zero or
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
     * @throws CalculationException if the given arguments are null or invalid count of {@link BigDecimal} numbers for
     * the given {@link MathOperation}
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
     * Returns the sum of two specified {@link BigDecimal} numbers.
     *
     * @param firstNumber  a value of the first argument for sum
     * @param secondNumber a value of the second argument for sum
     * @return the sum of two specified {@link BigDecimal} numbers
     */
    private static BigDecimal add(BigDecimal firstNumber, BigDecimal secondNumber) {
        return firstNumber.add(secondNumber);
    }

    /**
     * Returns the subtraction of two specified {@link BigDecimal} numbers.
     *
     * @param firstNumber  a value of the first argument for subtraction
     * @param secondNumber a value of the second argument for subtraction
     * @return the subtraction of two specified {@link BigDecimal} numbers
     */
    private static BigDecimal subtract(BigDecimal firstNumber, BigDecimal secondNumber) {
        return firstNumber.subtract(secondNumber);
    }

    /**
     * Returns the multiplication of two specified {@link BigDecimal} numbers.
     *
     * @param firstNumber  a value of the first argument for multiplication
     * @param secondNumber a value of the second argument for multiplication
     * @return the multiplication of two specified numbers
     */
    private static BigDecimal multiply(BigDecimal firstNumber, BigDecimal secondNumber) {
        return firstNumber.multiply(secondNumber);
    }

    /**
     * Returns the division of two specified {@link BigDecimal} numbers.
     *
     * @param firstNumber  a value of the first argument for division
     * @param secondNumber a value of the second argument for division
     * @return the division of two specified {@link BigDecimal} numbers
     * @throws CalculationException if divisor or both arguments are equal to zero
     */
    private static BigDecimal divide(BigDecimal firstNumber, BigDecimal secondNumber) throws CalculationException {
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
     * @param base a {@link BigDecimal} number to calculate square for
     * @return the square for the specified {@link BigDecimal} number
     */
    private static BigDecimal square(BigDecimal base) {
        return base.abs().pow(2);
    }

    /**
     * Returns the negated given {@link BigDecimal} number.
     *
     * @param number a {@link BigDecimal} number to negate
     * @return the negated given {@link BigDecimal} number
     */
    private static BigDecimal negate(BigDecimal number) {
        return number.negate();
    }

    /**
     * Returns the square root calculated for the given {@link BigDecimal} number. Source of square root algorithm for {@link BigDecimal} is
     * <a href="https://www.java-forums.org/advanced-java/44345-square-rooting-bigdecimal.html">square rooting a BigDecimal</a>
     *
     * @param x a {@link BigDecimal} number to calculate square root for
     * @return the square root calculated for the given {@link BigDecimal} number
     * @throws CalculationException if the given {@link BigDecimal} number is negative
     */
    private static BigDecimal sqrt(BigDecimal x) throws CalculationException {
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
     * Returns the {@link BigDecimal} number that is a specified percentage calculated for the given {@link BigDecimal} number.
     *
     * @param base    a {@link BigDecimal} number to calculate percentage from
     * @param percent a count of percents to calculate
     * @return the {@link BigDecimal} number that is a specified percentage calculated for the given {@link BigDecimal} number
     */
    private static BigDecimal percent(BigDecimal base, BigDecimal percent) {
        if (isZero(base) || isZero(percent)) {
            return ZERO;
        }
        // Convert percentage to an absolute value
        BigDecimal absolutePercentageValue = percent.divide(ONE_HUNDRED, SCALE, ROUND_HALF_UP);
        return base.multiply(absolutePercentageValue);
    }

    /**
     * Returns the {@link BigDecimal} number calculated as a division of 1 by the given {@link BigDecimal} number.
     *
     * @param base a {@link BigDecimal} number to reverse
     * @return the {@link BigDecimal} number calculated as a division of 1 by the given {@link BigDecimal} number
     * @throws CalculationException if {@link BigDecimal} number is equal to zero
     */
    private static BigDecimal reverse(BigDecimal base) throws CalculationException {
        if (isZero(base)) {
            throw new CalculationException(DIVISION_BY_ZERO_ERROR);
        }
        return ONE.divide(base, SCALE, ROUND_HALF_UP);
    }
}