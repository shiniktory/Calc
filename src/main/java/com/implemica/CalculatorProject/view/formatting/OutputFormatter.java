package com.implemica.CalculatorProject.view.formatting;

import com.implemica.CalculatorProject.model.calculation.MathOperation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.implemica.CalculatorProject.model.validation.DataValidator.*;
import static java.lang.String.format;
import static java.math.BigDecimal.*;
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
    private static final String EXPONENT = "e";

    /**
     * The string value contains minus sign.
     */
    public static final String MINUS = "-";

    /**
     * The value of an empty string.
     */
    private static final String EMPTY_VALUE = "";

    /**
     * The string contains zero value.
     */
    public static final String ZERO_VALUE = "0";

    /**
     * Tha value of {@link BigDecimal} number with fraction part with nines. Used to detect number with fraction part
     * with nine in period.
     */
    private static final BigDecimal FRACTION_PART_WITH_NINES = BigDecimal.valueOf(0.99);

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
     * The maximum fraction part length for numbers with point, that are positive and
     * greater than {@link BigDecimal#ONE}.
     */
    private static final int FRACTION_LENGTH_WITH_POINT = 16;

    /**
     * The maximum fraction part length for numbers with point, that are positive and
     * less than {@link BigDecimal#ONE}.
     */
    private static final int FRACTION_LENGTH_WITH_ZERO = 17;

    /**
     * The maximum fraction part length for numbers with point, that are negative and
     * less than {@link BigDecimal#ONE}.
     */
    private static final int FRACTION_LENGTH_WITH_MINUS_ZERO = 18;

    /**
     * The maximum fraction part length for numbers with point, that are negative and
     * greater than {@link BigDecimal#ONE}.
     */
    private static final int FRACTION_LENGTH_WITH_MINUS_AND_POINT = 17;

    /**
     * The {@link DecimalFormat} instance configured to format number to an exponential view without group
     * delimiters.
     */
    private static final DecimalFormat mathFormatWithExponent = getMathFormatWithExponent();

    /**
     * The {@link DecimalFormat} instance configured to format number with rounding without group delimiters.
     */
    private static final DecimalFormat mathFormatWithRounding = getMathFormatWithRounding();

    /**
     * The {@link DecimalFormat} instance configured to format number to an exponential view with group
     * delimiters.
     */
    private static final DecimalFormat exponentialFormatWithGroups = getExponentialFormatWithGroups();

    /**
     * The {@link DecimalFormat} instance configured to format number with rounding with group delimiters.
     */
    private static final DecimalFormat roundingFormatWithGroups = getRoundingFormatWithGroups();

    /**
     * Returns the formatted specified {@link BigDecimal} number to Mathematical view (without group delimiters).
     * Removes trailing zeroes in fractional part of the number, rounds or converts to exponential view if needed.
     *
     * @param number a {@link BigDecimal} number to format
     * @return the formatted string containing number rounded or converted to an exponential view without
     * group delimiters
     */
    public static String formatToMathView(BigDecimal number) {
        String formattedNumber;
        if (isExponentFormattingNeed(number)) { // format with exponent
            formattedNumber = mathFormatWithExponent.format(number).toLowerCase();
            formattedNumber = adjustExponentialView(formattedNumber);

        } else { // format with rounding
            int maxFractionDigitsCount = getCountFractionDigits(number.toPlainString());
            mathFormatWithRounding.setMaximumFractionDigits(maxFractionDigitsCount);
            formattedNumber = mathFormatWithRounding.format(number).toLowerCase();
        }

        return formattedNumber;
    }

    /**
     * Returns the formatted specified {@link BigDecimal} number with group delimiters. Removes trailing zeroes,
     * rounds or converts to an exponential view if necessary.
     *
     * @param number a {@link BigDecimal} number to format
     * @return the formatted string containing number rounded or converted to an exponential view with
     * group delimiters
     */
    public static String formatWithGroupDelimiters(BigDecimal number) {
        String stringValue;
        if (isExponentFormattingNeed(number)) {
            stringValue = formatToExponentialViewWithGroups(number);
        } else {
            stringValue = formatWithRoundingWithGroups(number);
        }
        return stringValue;
    }

    /**
     * Formats the specified {@link BigDecimal} number by adding group delimiters. Do not change the fraction part of the number.
     * Returns the string contains this formatted number.
     *
     * @param number a {@link BigDecimal} number to format
     * @return the string contains this formatted number
     */
    public static String formatEnteredNumber(BigDecimal number, boolean isLastSymbolPoint) {
        String formattedIntPart = formatWithRoundingWithGroups(number.setScale(0, ROUND_DOWN));

        String numberStr = number.toPlainString();
        String fractionPart = EMPTY_VALUE;

        if (numberStr.contains(POINT)) {
            fractionPart = numberStr.substring(numberStr.indexOf(POINT));
        }
        if (numberStr.startsWith(MINUS + ZERO_VALUE)) {
            formattedIntPart = MINUS + formattedIntPart;
        }
        if (isLastSymbolPoint) {
            formattedIntPart += POINT;
        }
        return formattedIntPart + fractionPart;
    }

    /**
     * Formats the given {@link BigDecimal} number to an exponential view with group delimiters and returns this
     * formatted number represented by string.
     *
     * @param number a {@link BigDecimal} number to format to an exponential view with group delimiters
     * @return formatted given {@link BigDecimal} number represented by string
     */
    private static String formatToExponentialViewWithGroups(BigDecimal number) {
        String formattedNumber = exponentialFormatWithGroups.format(number).toLowerCase();
        return adjustExponentialView(formattedNumber);
    }

    /**
     * Rounds the given {@link BigDecimal} number and returns it's string representation.
     *
     * @param number a {@link BigDecimal} number to round
     * @return formatted given {@link BigDecimal} number represented by string
     */
    private static String formatWithRoundingWithGroups(BigDecimal number) {
        int fractionDigitsCount = getCountFractionDigits(number.toPlainString());

        // Check for tail with nine in period and round it
        BigDecimal tail = number.remainder(ONE).abs();
        if (tail.compareTo(FRACTION_PART_WITH_NINES) > 0 &&
                tail.toPlainString().length() > MAX_LENGTH_WITH_POINT_AND_MINUS) {

            fractionDigitsCount--;
        }
        roundingFormatWithGroups.setMaximumFractionDigits(fractionDigitsCount);
        return roundingFormatWithGroups.format(number).toLowerCase();
    }

    /**
     * Adjusts the given string contains number formatted to an exponential view by adding a plus sign to an
     * exponent positive degree.
     *
     * @param number a string representation of a number formatted to an exponential view
     * @return formatted number represented by string
     */
    private static String adjustExponentialView(String number) {
        String formattedNumber = number.toLowerCase();
        Pattern pattern = Pattern.compile(EXPONENT_PATTERN);
        Matcher matcher = pattern.matcher(number);

        if (matcher.find()) { // If exponent has no sign before grade add plus sign
            formattedNumber = formattedNumber.replace(EXPONENT, EXPONENT_REPLACEMENT);
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
        int fractionDigitsCount = 0;

        if (number.contains(POINT)) {
            fractionDigitsCount = FRACTION_LENGTH_WITH_POINT - number.indexOf(POINT);
        }

        if (number.startsWith(MINUS) && number.contains(POINT)) {
            fractionDigitsCount = FRACTION_LENGTH_WITH_MINUS_AND_POINT - number.indexOf(POINT);
        }

        if (number.startsWith(MINUS + ZERO_VALUE + POINT)) {
            fractionDigitsCount = FRACTION_LENGTH_WITH_MINUS_ZERO - number.indexOf(POINT);
        }

        if (number.startsWith(ZERO_VALUE + POINT)) {
            fractionDigitsCount = FRACTION_LENGTH_WITH_ZERO - number.indexOf(POINT);
        }

        return fractionDigitsCount;
    }

    /**
     * Returns a string contains formatted expression for the specified argument and {@link MathOperation}.
     * For example, square root: √(x); square: sqr(x); reverse: 1/(x).
     *
     * @param operation a {@link MathOperation} to use for formatting
     * @param argument  a string contains number or previous formatted unary expression to use for formatting
     * @return a string contains formatted expression for the specified argument and {@link MathOperation}
     */
    public static String formatUnaryOperation(MathOperation operation, String argument) {
        String formattedExpression = EMPTY_VALUE;

        switch (operation) {
            case SQUARE_ROOT:
                formattedExpression = format(SQUARE_ROOT_PATTERN, argument);
                break;

            case SQUARE:
                formattedExpression = format(SQUARE_PATTERN, argument);
                break;

            case REVERSE:
                formattedExpression = format(REVERSE_PATTERN, argument);
                break;

            case NEGATE:
                formattedExpression = format(NEGATE_PATTERN, argument);
        }
        return formattedExpression;
    }

    /**
     * Returns a new {@link DecimalFormat} instance configured for formatting to exponential view without
     * group delimiters.
     *
     * @return a new {@link DecimalFormat} instance configured for formatting to exponential view without
     * group delimiters
     */
    private static DecimalFormat getMathFormatWithExponent() {
        DecimalFormat format = getExponentialFormatWithGroups();
        format.setGroupingUsed(false);
        return format;
    }

    /**
     * Returns a new {@link DecimalFormat} instance configured for formatting to exponential view with
     * group delimiters.
     *
     * @return a new {@link DecimalFormat} instance configured for formatting to exponential view with
     * group delimiters
     */
    private static DecimalFormat getExponentialFormatWithGroups() {
        DecimalFormat format = new DecimalFormat(NUMBER_FORMAT_PATTERN);
        format.setRoundingMode(HALF_UP);
        format.setDecimalFormatSymbols(getDelimiters());
        format.setDecimalSeparatorAlwaysShown(true);
        return format;
    }

    /**
     * Returns a new {@link DecimalFormat} instance configured for formatting with rounding without
     * group delimiters.
     *
     * @return a new {@link DecimalFormat} instance configured for formatting with rounding without
     * group delimiters
     */
    private static DecimalFormat getMathFormatWithRounding() {
        DecimalFormat format = getRoundingFormatWithGroups();
        format.setGroupingUsed(false);
        return format;
    }

    /**
     * Returns a new {@link DecimalFormat} instance configured for formatting with rounding and
     * group delimiters.
     *
     * @return a new {@link DecimalFormat} instance configured for formatting with rounding and
     * group delimiters
     */
    private static DecimalFormat getRoundingFormatWithGroups() {
        DecimalFormat format = new DecimalFormat();
        format.setRoundingMode(HALF_UP);
        format.setDecimalFormatSymbols(getDelimiters());
        return format;
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
}
