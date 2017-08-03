package com.implemica.CalculatorProject.util;

import com.implemica.CalculatorProject.exception.CalculationException;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.util.OutputFormatter.removeGroupDelimiters;
import static com.implemica.CalculatorProject.validation.DataValidator.isNumber;
import static java.lang.String.format;

/**
 * The class grants a functionality to transform specified values into other types. Such as get
 * array of {@link BigDecimal} numbers from array with number's string representations.
 *
 * @author V. Kozina-Kravchenko
 */
public class ValueTransformerUtil {

    /**
     * An error message about a given string is not a number.
     */
    private static final String CONVERTING_ERROR = "Some of arguments is not valid. Argument value = %s";

    /**
     * Returns an array of {@link BigDecimal} numbers created based on the specified array with number's string
     * representations. Returns null if the given array is null.
     *
     * @param stringValues an array of string representations of numbers
     * @return an array of {@link BigDecimal} numbers created based on the specified array with number's string
     * representations
     * @throws CalculationException if some of string representations are not a number
     */
    public static BigDecimal[] getBigDecimalValues(String... stringValues) throws CalculationException {
        if (stringValues == null) {
            return null;
        }

        BigDecimal[] numbers = new BigDecimal[stringValues.length];
        for (int i = 0; i < stringValues.length; i++) {
            numbers[i] = convertToNumber(stringValues[i]);
        }
        return numbers;
    }

    /**
     * Returns a {@link BigDecimal} value created based on given string representation of a number.
     *
     * @param numberString a string representation of a number
     * @return a {@link BigDecimal} value created based on given string representation of a number
     * @throws CalculationException if the given string is not a number
     */
    private static BigDecimal convertToNumber(String numberString) throws CalculationException {
        if (!isNumber(numberString)) {
            throw new CalculationException(format(CONVERTING_ERROR, numberString));
        }
        return new BigDecimal(removeGroupDelimiters(numberString));
    }
}
