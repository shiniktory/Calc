package com.implemica.CalculatorProject.validation;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.implemica.CalculatorProject.processing.InputValueProcessor.ZERO_VALUE;
import static com.implemica.CalculatorProject.util.OutputFormatter.POINT;
import static com.implemica.CalculatorProject.util.OutputFormatter.MINUS;

/**
 * The {@code DataValidator} class contains methods for validation of input data.
 *
 * @author V. Kozina-Kravchenko
 */
public class DataValidator {

    public static final int MAX_NUMBER_LENGTH = 16;
    public static final int MAX_LENGTH_WITH_POINT = 17;
    public static final int MAX_LENGTH_WITH_ZERO_AND_POINT = 18;
    public static final int MAX_LENGTH_WITH_MINUS = 17;
    public static final int MAX_LENGTH_WITH_POINT_AND_MINUS = 19;

    private static final String PATTERN_FOR_DIGIT = "\\d";
    private static final String PATTERN_FOR_NUMBERS = "[-]?\\d+[.]?[\\d]*[Ee]?[-+]?\\d*";

    private static final BigDecimal MIN_VALUE = new BigDecimal(0.0000000000000001);
    private static final BigDecimal MAX_VALUE = new BigDecimal(9999999999999999L);

    /**
     * Returns true if the specified string is null or empty.
     *
     * @param value a string to validate for emptiness
     * @return true if the specified string is null or empty
     */
    public static boolean isEmptyString(String value) {
        return value == null || value.isEmpty();
    }

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
        if (number.contains(POINT) &&
                number.startsWith(MINUS) &&
                number.length() <= MAX_LENGTH_WITH_POINT_AND_MINUS) {
            return true;
        }
        if (!number.contains(POINT) &&
                !number.startsWith(MINUS) &&
                number.length() <= MAX_NUMBER_LENGTH) {
            return true;
        }
        return false;
    }

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

        if (stringValue.startsWith(MINUS + ZERO_VALUE + POINT) && stringValue.length() > MAX_LENGTH_WITH_POINT_AND_MINUS) {
            return true;
        }
        return !stringValue.startsWith(MINUS) && !stringValue.contains(POINT) && stringValue.length() > MAX_NUMBER_LENGTH;
    }

    public static boolean isZero(BigDecimal number) {
        return BigDecimal.ZERO.compareTo(number) == 0;
    }
}
