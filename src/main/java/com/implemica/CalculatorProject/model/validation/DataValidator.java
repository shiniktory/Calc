package com.implemica.CalculatorProject.model.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.implemica.CalculatorProject.view.formatting.OutputFormatter.POINT;
import static com.implemica.CalculatorProject.view.formatting.OutputFormatter.MINUS;
import static com.implemica.CalculatorProject.view.formatting.OutputFormatter.ZERO_VALUE;
import static java.math.BigDecimal.ZERO;

/**
 * The {@code DataValidator} class contains methods for validation of input data.
 *
 * @author V. Kozina-Kravchenko
 */
public class DataValidator {

    /**
     * The maximum length for number without minus sign and point.
     */
    private static final int MAX_NUMBER_LENGTH = 16;

    /**
     * The maximum length for number greater than one with point and without minus sign.
     */
    private static final int MAX_LENGTH_WITH_POINT = 17;

    /**
     * The maximum length for number less than one with point and without minus sign.
     */
    private static final int MAX_LENGTH_WITH_ZERO_AND_POINT = 18;

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
    private static final BigDecimal MIN_VALUE = new BigDecimal("0.0000000000000001");

    /**
     * The maximum number without converting to exponential view.
     */
    private static final BigDecimal MAX_VALUE = new BigDecimal("9999999999999999.5");

    /**
     * The upper bound of number's value after what will be thrown an exception about overflow.
     */
    private static final BigDecimal MAX_NUMBER = new BigDecimal("1.e+10000");

    /**
     * The lower bound of number's value under what will be thrown an exception about overflow.
     */
    private static final BigDecimal MIN_NUMBER = new BigDecimal("1.e-10000");

    /**
     * The value of scale for numbers with fraction part longer than valid to check is exponential converting need/
     */
    private static final int SCALE_FOR_FRACTION_PART_CHECK = 21;

    /**
     * The value greater than {@link #MIN_VALUE} but number less of it must be converted to exponential view
     * on condition when this number's length is bigger than valid.
     */
    private static final BigDecimal NUMBER_FOR_EXPONENTIAL_CHECK = BigDecimal.valueOf(0.001);

    /**
     * The minimum value of number's fraction part after 16-th digit after point greater what number must be converted
     * to an exponential view.
     */
    private static final BigDecimal MIN_TAIL_VALUE_FOR_EXPONENT = BigDecimal.valueOf(0.000000000000000000001);

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

        boolean isLengthValid = false;

        if (number.startsWith(MINUS)) { // get absolute value of number
            number = number.substring(1);
        }

        if (!number.contains(POINT) && number.length() <= MAX_NUMBER_LENGTH) {
            isLengthValid = true;
        }

        if (number.contains(POINT) && number.length() <= MAX_LENGTH_WITH_POINT) {
            isLengthValid = true;
        }

        if (number.startsWith(ZERO_VALUE + POINT) && number.length() <= MAX_LENGTH_WITH_ZERO_AND_POINT) {
            isLengthValid = true;
        }

        return isLengthValid;
    }

    /**
     * Returns true if the specified number needs exponential formatting. Validation based on different factors
     * such as number length and others.
     *
     * @param number the number to validate for an exponential formatting need
     * @return true if the specified number needs exponential formatting
     */
    public static boolean isExponentFormattingNeed(BigDecimal number) {

        number = number.abs();
        boolean isExponentNeed = false;

        if (number.compareTo(MIN_VALUE) < 0 || number.compareTo(MAX_VALUE) >= 0) {
            isExponentNeed = true;
        }

        BigDecimal tailLessMinValue = number.remainder(MIN_VALUE).setScale(SCALE_FOR_FRACTION_PART_CHECK, RoundingMode.HALF_UP);

        if (number.compareTo(NUMBER_FOR_EXPONENTIAL_CHECK) < 0 &&
                tailLessMinValue.compareTo(MIN_TAIL_VALUE_FOR_EXPONENT) >= 0) {
            isExponentNeed = true;
        }
        if (isZero(number)) {
            isExponentNeed = false;
        }
        return isExponentNeed;
    }

    /**
     * Returns true if the specified number is zero.
     *
     * @param number the number to validate for zero value
     * @return true if the specified number is zero
     */
    public static boolean isZero(BigDecimal number) {
        return ZERO.compareTo(number) == 0;
    }

    /**
     * Returns true if the given number as result of an operations is out of valid bounds.
     *
     * @param result the number to check for overflow
     * @return true if the given number as result of an operations is out of valid bounds
     */
    public static boolean isResultOverflow(BigDecimal result) {
        boolean isResultOverflow = false;
        BigDecimal absResult = result.abs();
        if (MAX_NUMBER.compareTo(absResult) <= 0 ||
                absResult.compareTo(ZERO) > 0 && MIN_NUMBER.compareTo(absResult) >= 0) {
            isResultOverflow = true;
        }
        return isResultOverflow;
    }
}
