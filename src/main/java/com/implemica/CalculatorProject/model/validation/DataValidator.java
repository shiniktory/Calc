package com.implemica.CalculatorProject.model.validation;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.implemica.CalculatorProject.model.processing.InputValueProcessor.ZERO_VALUE;
import static com.implemica.CalculatorProject.model.util.OutputFormatter.POINT;
import static com.implemica.CalculatorProject.model.util.OutputFormatter.MINUS;

/**
 * The {@code DataValidator} class contains methods for validation of input data.
 *
 * @author V. Kozina-Kravchenko
 */
public class DataValidator {

    /**
     * The maximum length for number without minus sign and point.
     */
    public static final int MAX_NUMBER_LENGTH = 16;

    /**
     * The maximum length for number greater than one with point and without minus sign.
     */
    public static final int MAX_LENGTH_WITH_POINT = 17;

    /**
     * The maximum length for number with minus sign and without point.
     */
    public static final int MAX_LENGTH_WITH_MINUS = 17;

    /**
     * The maximum length for number less than one with point and without minus sign.
     */
    public static final int MAX_LENGTH_WITH_ZERO_AND_POINT = 18;

    /**
     * The maximum length for any number with point and minus.
     */
    public static final int MAX_LENGTH_WITH_POINT_AND_MINUS = 19;

    /**
     * The string value of pattern for any digit.
     */
    private static final String PATTERN_FOR_DIGIT = "\\d";

    /**
     * The string value of pattern for any number.
     */
    private static final String PATTERN_FOR_NUMBERS = "[-]?\\d+[.]?[\\d]*[Ee]?([-+]?\\d+)?";

    /**
     * The minimum number user can enter.
     */
    private static final BigDecimal MIN_VALUE = new BigDecimal(0.0000000000000001);

    /**
     * The maximum number user can enter.
     */
    private static final BigDecimal MAX_VALUE = new BigDecimal(9999999999999999.5);

    /**
     * The upper bound of number's value after what will be thrown an exception about overflow.
     */
    private static final BigDecimal MAX_NUMBER = new BigDecimal("1.e+10000");

    /**
     * The lower bound of number's value under what will be thrown an exception about overflow.
     */
    private static final BigDecimal MIN_NUMBER = new BigDecimal("1.e-10000");


    /**
     * Returns true if the specified string is null or empty.
     *
     * @param value a string to validate for emptiness
     * @return true if the specified string is null or empty
     */
    public static boolean isEmptyString(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Returns true if the specified string represents a digit.
     *
     * @param value a string to validate for digit
     * @return true if the specified string represents a digit
     */
    public static boolean isDigit(String value) {
        if (isEmptyString(value)) {
            return false;
        }

        Pattern pattern = Pattern.compile(PATTERN_FOR_DIGIT);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * Returns true if the specified string contains only number.
     *
     * @param value a string to validate for containing only number
     * @return true if the specified string contains only number
     */
    public static boolean isNumber(String value) {
        if (isEmptyString(value)) {
            return false;
        }
        Pattern pattern = Pattern.compile(PATTERN_FOR_NUMBERS);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * Returns true if the length of current number represented by string is valid.
     *
     * @param number a number to validate represented by string
     * @return true if the length of current number represented by string is valid
     */
    public static boolean isNumberLengthValid(String number) {
        if (isEmptyString(number)) {
            return false;
        }

        if (number.startsWith(ZERO_VALUE + POINT) &&
                number.length() <= MAX_LENGTH_WITH_ZERO_AND_POINT) {
            return true;
        }
        if (number.contains(POINT) &&
                !number.startsWith(MINUS) &&
                number.length() <= MAX_LENGTH_WITH_POINT) {
            return true;
        }
        if (!number.contains(POINT) &&
                number.startsWith(MINUS) &&
                number.length() <= MAX_LENGTH_WITH_MINUS) {
            return true;
        }
        if (number.startsWith(MINUS + ZERO_VALUE + POINT) &&
                number.length() <= MAX_LENGTH_WITH_POINT_AND_MINUS) {
            return true;
        }
        if (number.startsWith(MINUS) &&
                number.contains(POINT) &&
                number.length() < MAX_LENGTH_WITH_POINT_AND_MINUS) {
            return true;
        }
        if (!number.contains(POINT) &&
                !number.startsWith(MINUS) &&
                number.length() <= MAX_NUMBER_LENGTH) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the specified number needs exponential formatting. Validation based on different factors
     * such as number length and others.
     *
     * @param number the number to validate for an exponential formatting need
     * @return true if the specified number needs exponential formatting
     */
    public static boolean isExponentFormattingNeed(BigDecimal number) {
        if (isZero(number)) {
            return false;
        }

        if (number.abs().compareTo(MIN_VALUE) < 0 || number.abs().compareTo(MAX_VALUE) > 0) {
            return true;
        }

        String stringValue = number.toPlainString();
        if (number.abs().compareTo(new BigDecimal(0.001)) < 0 &&
                number.stripTrailingZeros().scale() > 16) {
            return true;
        }

        if (stringValue.startsWith(MINUS) &&
                !stringValue.contains(POINT) &&
                stringValue.length() > MAX_LENGTH_WITH_MINUS) {
            return true;
        }

        if (stringValue.startsWith(MINUS + ZERO_VALUE + POINT) && stringValue.length() > MAX_LENGTH_WITH_POINT_AND_MINUS + 2) {
            return true;
        }
        return !stringValue.startsWith(MINUS) && !stringValue.contains(POINT) && stringValue.length() > MAX_NUMBER_LENGTH;
    }

    /**
     * Returns true if the specified number is zero.
     *
     * @param number the number to validate for zero value
     * @return true if the specified number is zero
     */
    public static boolean isZero(BigDecimal number) {
        return BigDecimal.ZERO.compareTo(number) == 0;
    }

    public static boolean isResultOverflow(BigDecimal result) {
        boolean isResultOverflow = false;
        BigDecimal absResult = result.abs();
        if (MAX_NUMBER.compareTo(absResult) <= 0 ||
                absResult.compareTo(BigDecimal.ZERO) > 0 && MIN_NUMBER.compareTo(absResult) >= 0) {
            isResultOverflow = true;
        }
        return isResultOverflow;
    }
}
