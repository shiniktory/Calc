package com.implemica.CalculatorProject.model.util;

import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.exception.CalculationException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.implemica.CalculatorProject.model.processing.InputValueProcessor.ZERO_VALUE;
import static com.implemica.CalculatorProject.model.validation.DataValidator.*;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ROUND_UP;
import static java.math.RoundingMode.HALF_UP;

/**
 * The class contains an instruments for formatting number views or expressions for history.
 *
 * @author V. Kozina-Kravchenko
 */
public class OutputFormatter {

    /**
     * The string value contains an exponent symbol.
     */
    private static final String EXPONENT = "E";

    /**
     * The string value contains minus sign.
     */
    public static final String MINUS = "-";

    /**
     * The value of an empty string.
     */
    public static final String EMPTY_VALUE = "";

    /**
     * The string value contains a pattern for decimal numbers with exponent.
     */
    private static final String NUMBER_FORMAT_PATTERN = "0.###############E0";

    /**
     * The string value contains exponent symbol and plus sign.
     */
    private static final String EXPONENT_REPLACEMENT = "e+";

    /**
     * The string contains a point for double numbers.
     */
    public static final String POINT = ".";

    /**
     * The value of character used as decimal separator.
     */
    private static final char DECIMAL_SEPARATOR = '.';

    /**
     * The value of character used as group separator.
     */
    private static final char GROUP_SEPARATOR = ',';

    /**
     * The value of pattern for exponent with positive degree.
     */
    private static final String EXPONENT_PATTERN = "[Ee]\\d+";

    /**
     * The value of pattern for any number with exponent.
     */
    private static final String NUMBER_WITH_EXPONENT_PATTERN = "([-]?\\d+[.]?\\d*[1-9]*)([0+]*e)(.*)";

    /**
     * The value of pattern for any number less than one and contains tail of 9 in period.
     */
    private static final String NUMBER_LESS_ONE_WITH_TRAILING_9_PATTERN = "([-]?\\d+[.]\\d*[0-8]*)(9{2,})(e.*)?";

    /**
     * The value of pattern for any number greater than one and contains tail of 9 in period.
     */
    private static final String NUMBER_GREATER_ONE_WITH_TRAILING_9_PATTERN = "([-]?9+[.])(9+)(e.*)?";

    /**
     * The value of pattern for formatting the history expression for square root operation.
     */
    private static final String SQUARE_ROOT_PATTERN = "√(%s)";

    /**
     * The value of pattern for formatting the history expression for square operation.
     */
    private static final String SQUARE_PATTERN = "sqr(%s)";

    /**
     * The value of pattern for formatting the history expression for reverse operation.
     */
    private static final String REVERSE_PATTERN = "1/(%s)";

    /**
     * The value of pattern for formatting the history expression for negate operation.
     */
    private static final String NEGATE_PATTERN = "negate(%s)";

    /**
     * The value of an error message about invalid input.
     */
    private static final String INVALID_INPUT_ERROR = "Invalid input";

    /**
     * Formats the specified number represented by string to Mathematical view. Removes group delimiters
     * from the number and trailing zeroes in fractional part of the number and converts to exponential view
     * if needed.
     *
     * @param numberStr the string with number to format
     * @return the formatted string containing number for the next calculations
     */
    public static String formatToMathView(String numberStr) throws CalculationException {
        String formattedNumberStr = removeGroupDelimiters(numberStr);
        if (!isNumber(formattedNumberStr)) {
            throw new CalculationException(INVALID_INPUT_ERROR);
        }
        BigDecimal number = new BigDecimal(formattedNumberStr);
        int scale = 0;
        if (formattedNumberStr.contains(POINT)) {
            scale = getScale(formattedNumberStr);
        }
        return formatToMathView(number.setScale(scale, HALF_UP));
    }

    private static int getScale(String number) {
        int scale;
        if (number.toUpperCase().contains(EXPONENT)) {
            scale = new BigDecimal(number).scale();
        } else {
            scale = number.length() - number.indexOf(POINT) - 1;
        }
        return scale;
    }

    /**
     * Removes group delimiters from the specified number represented by string and returns formatted string.
     *
     * @param number the string with number to format
     * @return the formatted string containing number without group delimiters
     */
    public static String removeGroupDelimiters(String number) {
        return number.replaceAll(String.valueOf(GROUP_SEPARATOR), EMPTY_VALUE).toLowerCase();
    }

    /**
     * Formats the specified number to a mathematical view without group delimiters, rounded or transformed to en
     * exponential view. Returns this formatted number represented by string.
     *
     * @param number the number to format
     * @return a string representation of formatted given number
     * @throws CalculationException
     */
    public static String formatToMathView(BigDecimal number) throws CalculationException {
        //Without trailing zeroes after point and with groups delimiters
        String numberString = formatNumberForDisplaying(number.toString());
        numberString = removeGroupDelimiters(numberString);
        return numberString;
    }

    /**
     * Returns the formatted specified number represented by string for displaying in
     * calculator's text field. Removes trailing zeroes, round or adds an exponent if
     * necessary and adds group delimiters to number.
     *
     * @param numberStr a number represented by string to format
     * @return the formatted specified number represented by string for displaying
     */
    public static String formatNumberForDisplaying(String numberStr) throws CalculationException {
        if (!isNumber(numberStr)) {
            throw new CalculationException(INVALID_INPUT_ERROR);
        }
        if (numberStr.endsWith(POINT)) {
            numberStr = numberStr.substring(0, numberStr.length() - 1);
            return formatNumberForDisplaying(numberStr) + POINT;
        }

        BigDecimal number = new BigDecimal(numberStr);
        if (isZero(number)) {
            return ZERO_VALUE;
        }

        if (!numberStr.toUpperCase().contains(EXPONENT)) { // Round if there are trailing "9"
            if (number.abs().compareTo(BigDecimal.ONE) > 0) {
                Pattern pattern2 = Pattern.compile(NUMBER_GREATER_ONE_WITH_TRAILING_9_PATTERN);
                Matcher matcher2 = pattern2.matcher(numberStr);
                if (matcher2.matches() && numberStr.length() >= MAX_LENGTH_WITH_POINT) {
                    // If after point is all 9s up to max length
                    int scale = 0;
                    String roundedNumber = number.setScale(scale, ROUND_UP).toString();
                    return addGroupDelimiters(roundedNumber);
                }
            } else {
                Pattern pattern = Pattern.compile(NUMBER_LESS_ONE_WITH_TRAILING_9_PATTERN);
                Matcher matcher = pattern.matcher(numberStr);
                if (matcher.matches() && numberStr.length() > MAX_LENGTH_WITH_POINT) {
                    int scale = numberStr.indexOf(matcher.group(2)) - numberStr.indexOf(POINT) - 1;
                    return number.setScale(scale, ROUND_UP).toString();
                }
            }
        }

        number = roundMeasurementError(numberStr);
        return formatNumberView(number);
    }

    private static BigDecimal roundMeasurementError(String numberStr) {
        BigDecimal number = new BigDecimal(numberStr);
        // remove rounding delta
        if (numberStr.contains(POINT) &&
                !numberStr.toUpperCase().contains(EXPONENT) &&
                numberStr.length() == MAX_LENGTH_WITH_POINT) { // for positive double

            int fractionDigitsCount = number.scale();
            BigDecimal fractionalPart = number.remainder(BigDecimal.ONE);

            BigDecimal correlation = new BigDecimal("1.e-" + fractionDigitsCount).multiply(new BigDecimal(5));
            if (fractionalPart.compareTo(correlation) < 0) {
                number = number.setScale(fractionDigitsCount - 1, ROUND_HALF_UP);
            }
        }
        return number;
    }

    private static String formatNumberView(BigDecimal number) {
        String stringValue;
        if (isExponentFormattingNeed(number)) {
            stringValue = formatToExponentialView(number);
        } else {
            stringValue = formatWithRounding(number);
        }
        return stringValue;
    }

    /**
     * Formats the given number to an exponential view and returns this formatted number represented by string.
     *
     * @param number a number to format to an exponential view
     * @return formatted given number represented by string
     */
    private static String formatToExponentialView(BigDecimal number) {
        DecimalFormat format = new DecimalFormat(NUMBER_FORMAT_PATTERN);
        format.setRoundingMode(HALF_UP);
        format.setDecimalFormatSymbols(getDelimiters());
        String formattedNumber = format.format(number).toLowerCase();
        return adjustExponentialView(formattedNumber);
    }

    /**
     * Rounds the given number and returns it's string representation.
     *
     * @param number a number to round
     * @return formatted given number represented by string
     */
    private static String formatWithRounding(BigDecimal number) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(getCountFractionDigits(number.toPlainString()));
        format.setRoundingMode(HALF_UP);
        format.setDecimalFormatSymbols(getDelimiters());
        return format.format(number).toLowerCase();
    }

    /**
     * Returns a {@link DecimalFormatSymbols} instance with configured decimal and group separators.
     *
     * @return a {@link DecimalFormatSymbols} instance with configured decimal and group separators
     */
    private static DecimalFormatSymbols getDelimiters() {
        DecimalFormatSymbols delimiters = new DecimalFormatSymbols();
        delimiters.setDecimalSeparator(DECIMAL_SEPARATOR);
        delimiters.setGroupingSeparator(GROUP_SEPARATOR);
        return delimiters;
    }

    /**
     * Adjusts the given string contains number formatted to an exponential view. For example, removes trailing
     * zeroes between point and exponent, adds a plus sign to an exponent positive degree and etc.
     *
     * @param number a string representation of a number formatted to an exponential view
     * @return formatted number represented by string
     */
    private static String adjustExponentialView(String number) {
        String formattedNumber = number;
        Pattern pattern = Pattern.compile(EXPONENT_PATTERN);
        Matcher matcher = pattern.matcher(number);
        if (matcher.find()) { // If exponent has no sign after add plus
            formattedNumber = formattedNumber.toUpperCase().replace(EXPONENT, EXPONENT_REPLACEMENT);
        }

        pattern = Pattern.compile(NUMBER_WITH_EXPONENT_PATTERN);
        matcher = pattern.matcher(formattedNumber);
        if (matcher.find()) {
            if (matcher.group(1).contains(POINT)) {
                formattedNumber = matcher.replaceAll("$1e$3"); // Remove trailing zeroes before exponent by removing group 2
            } else {
                formattedNumber = matcher.replaceAll("$1.e$3"); // Remove trailing zeroes before exponent and add point
            }
        }
        return formattedNumber;
    }

    /**
     * Returns a count of fraction digits to the specified number represented by string.
     *
     * @param number a string representation of a number to count fraction digits
     * @return a count of fraction digits to the specified number represented by string
     */
    private static int getCountFractionDigits(String number) {
        if (!number.contains(POINT)) {
            return 0;
        }
        if (number.startsWith(MINUS + ZERO_VALUE + POINT)) {
            return MAX_LENGTH_WITH_POINT_AND_MINUS - number.indexOf(POINT) - 1;
        }
        if (number.startsWith(MINUS)) {
            return MAX_LENGTH_WITH_POINT_AND_MINUS - number.indexOf(POINT) - 2;
        }
        if (number.startsWith(ZERO_VALUE + POINT)) {
            return MAX_LENGTH_WITH_POINT - number.indexOf(POINT);
        }
        return MAX_LENGTH_WITH_POINT - number.indexOf(POINT) - 1;
    }

    /**
     * Adds group delimiters to the specified number represented by string.
     *
     * @param number a string representation of a number to add group delimiters
     * @return a string representation of the given number with group delimiters
     * @throws CalculationException if the input string is not a number
     */
    public static String addGroupDelimiters(String number) throws CalculationException {
        if (!isNumber(number)) {
            throw new CalculationException(INVALID_INPUT_ERROR);
        }
        if (isZero(new BigDecimal(number))) {
            return number;
        }

        String intPart;
        String fractionPart;
        int pointIndex = number.indexOf(POINT);
        if (pointIndex == -1) {
            intPart = number;
            fractionPart = EMPTY_VALUE;
        } else {
            intPart = number.substring(0, pointIndex);
            fractionPart = number.substring(pointIndex);
        }

        return getFormattedNumber(intPart, fractionPart);
    }

    /**
     * Returns number constructed from the given integer and fraction parts. Integer part formats with group delimiters.
     *
     * @param intPart a string value of an integer part of number
     * @param fractionPart a string value of a fraction part of number
     * @return number constructed from the given integer and fraction parts
     */
    private static String getFormattedNumber(String intPart, String fractionPart) {
        String formattedIntPart = formatNumberView(new BigDecimal(intPart));
        String formattedNumber = formattedIntPart + fractionPart;

        if (intPart.startsWith(MINUS + ZERO_VALUE)) {
            formattedNumber = MINUS + formattedNumber;
        }
        return formattedNumber;
    }

    /**
     * Returns a string contains formatted expression for the specified argument and mathematical operation.
     * For example, square root: √(x); square: sqr(x); reverse: 1/(x).
     *
     * @param operation a mathematical operation to use for formatting
     * @param argument  a string contains number or previous formatted expression to use for formatting
     * @return a string contains formatted expression for the specified argument and mathematical operation
     */
    public static String formatUnaryOperation(MathOperation operation, String argument) {
        switch (operation) {
            case SQUARE_ROOT:
                return String.format(SQUARE_ROOT_PATTERN, argument);
            case SQUARE:
                return String.format(SQUARE_PATTERN, argument);
            case REVERSE:
                return String.format(REVERSE_PATTERN, argument);
            case NEGATE:
                return String.format(NEGATE_PATTERN, argument);
        }
        return EMPTY_VALUE;
    }
}
