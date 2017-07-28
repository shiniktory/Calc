package com.implemica.CalculatorProject.calculation;


import com.implemica.CalculatorProject.exception.CalculationException;
import com.implemica.CalculatorProject.validation.DataValidator;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.validation.DataValidator.isZero;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;

/**
 * The {@code StandardCalculator} class is an implementation of {@link Calculator} interface. Performs some
 * Mathematical operations such as multiplying, dividing, changing number's sign to an opposite and etc.
 *
 * @author V. Kozina-Kravchenko
 */
public class StandardCalculator implements Calculator {

    /**
     * The error message about invalid count of numbers.
     */
    private static final String INVALID_ARGUMENTS_COUNT = "Count of numbers must be 1 or 2. Your count is ";

    /**
     * The error message about division by zero occurs.
     */
    private static final String DIVISION_BY_ZERO_ERROR = "Cannot divide by zero";

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
    private static final int SCALE = 1000;

    /**
     * The value of 100 represented as {@link BigDecimal} number.
     */
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private static final String RESULT_UNDEFINED_ERROR = "Result is undefined";

    /**
     * Constructs a new {@code StandardCalculator} instance with the given numbers and Mathematical operation
     * to perform with these numbers.
     *
     * @param operation a Mathematical operation to perform with the given numbers
     * @param numbers   a numbers to perform a Mathematical operation with
     * @throws CalculationException if some of arguments is null or if count of numbers for the
     *                              given operation is wrong
     */
    public StandardCalculator(MathOperation operation, BigDecimal... numbers) throws CalculationException {
        this.operation = operation;
        this.numbers = numbers;

        if (operation == null) {
            throw new CalculationException(NO_SUCH_OPERATION_ERROR);
        }

        if (numbers == null || numbers.length == 0) {
            throw new CalculationException(INVALID_ARGUMENTS_COUNT);
        }
        if (operation.isBinary() && numbers.length != 2 ||
                !operation.isBinary() && numbers.length != 1) {
            throw new CalculationException(INVALID_ARGUMENTS_COUNT);
        }

    }

    /**
     * Returns the result of calculations of an Mathematical operations with the specified numbers.
     *
     * @return the result of calculations of an Mathematical operations with the specified numbers
     * @throws CalculationException in case of operation does not exist, division by zero or
     *                              invalid argument for an operation
     */
    public BigDecimal calculate() throws CalculationException {
        switch (operation) {
            // Binary operations
            case ADD:
                return add(numbers[0], numbers[1]);
            case SUBTRACT:
                return subtract(numbers[0], numbers[1]);
            case MULTIPLY:
                return multiply(numbers[0], numbers[1]);
            case DIVIDE:
                return divide(numbers[0], numbers[1]);
            case PERCENT:
                return percent(numbers[0], numbers[1]);

            // Unary operations
            case NEGATE:
                return negate(numbers[0]);
            case SQUARE_ROOT:
                return sqrt(numbers[0]);
            case SQUARE:
                return square(numbers[0]);
            case REVERSE:
                return reverse(numbers[0]);
            default:
                throw new CalculationException(NO_SUCH_OPERATION_ERROR);
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
     * Returns the square root calculated for the given number.
     *
     * @param number a number to calculate square root for
     * @return the square root calculated for the given number
     * @throws CalculationException if the given number is negative
     */
    private BigDecimal sqrt(BigDecimal number) throws CalculationException {
        if (number.compareTo(ZERO) < 0) {
            throw new CalculationException(INVALID_INPUT_ERROR);
        }
        if (isZero(number)) {
            return ZERO.setScale(0);
        }

        // Using Karp's algorithm for searching square root. Has 32 digits precision
        BigDecimal rootForDoublePart = new BigDecimal(Math.sqrt(number.doubleValue()));
        BigDecimal squareForRoot = rootForDoublePart.multiply(rootForDoublePart);
        double difference = number.subtract(squareForRoot).doubleValue();
        BigDecimal correlation = new BigDecimal(difference / (rootForDoublePart.doubleValue() * 2.0));
        return rootForDoublePart.add(correlation);
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
            return ZERO.setScale(0);
        }
        // Convert percentage to an absolute value
        BigDecimal absolutePercentage = percent.divide(ONE_HUNDRED, SCALE, ROUND_HALF_UP).stripTrailingZeros();
        return base.multiply(absolutePercentage).stripTrailingZeros();
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
        return BigDecimal.ONE.divide(base, SCALE, ROUND_HALF_UP).stripTrailingZeros();
    }
}