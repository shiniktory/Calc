package com.implemica.CalculatorProject.model.formatting;

import com.implemica.CalculatorProject.model.calculation.MathOperation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.implemica.CalculatorProject.model.validation.DataValidator.*;
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


    private static final int FRACTION_LENGTH_WITH_MINUS_ZERO = 18;
    private static final int FRACTION_LENGTH_WITH_MINUS_AND_POINT = 17;
    private static final int FRACTION_LENGTH_WITH_ZERO = 17;
    private static final int FRACTION_LENGTH_WITH_POINT = 16;


    /**
     * Formats the specified number represented by string to Mathematical view. Removes group delimiters
     * from the number and trailing zeroes in fractional part of the number and converts to exponential view
     * if needed.
     *
     * @param number
     * @return the formatted string containing number for the next calculations
     */
    public static String formatToMathView(BigDecimal number) {

        DecimalFormat mathFormat = new DecimalFormat(); // TODO move DecimalFormat as field

        mathFormat.setGroupingUsed(false);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(DECIMAL_SEPARATOR);
        mathFormat.setDecimalFormatSymbols(symbols);
        mathFormat.setRoundingMode(HALF_UP);
        if (isExponentFormattingNeed(number)) {
            mathFormat.applyPattern(NUMBER_FORMAT_PATTERN);
            mathFormat.setDecimalSeparatorAlwaysShown(true);
        } else {
            mathFormat.setMaximumFractionDigits(getCountFractionDigits(number.toPlainString()));
        }

        String formattedNumber = mathFormat.format(number).toLowerCase();
        if (formattedNumber.contains(EXPONENT)) {
            formattedNumber = adjustExponentialView(formattedNumber);
        }
        return formattedNumber;
    }

    /**
     * Returns the formatted specified number represented by string for displaying in
     * calculator's text field. Removes trailing zeroes, round or adds an exponent if
     * necessary and adds group delimiters to number.
     *
     * @param number
     * @return the formatted specified number represented by string for displaying
     */
    public static String formatNumberForDisplaying(BigDecimal number) {
        String stringValue;
        if (isExponentFormattingNeed(number)) {
            stringValue = formatToExponentialView(number);
        } else {
            stringValue = formatWithRounding(number);
        }
        return stringValue;
    }

    public static String formatEnteredNumber(BigDecimal number) {

        String formattedIntPart = formatWithRounding(number.setScale(0, ROUND_DOWN));
        String numberStr = number.toPlainString();
        String fractionPart = "";
        if (numberStr.contains(POINT)) {
            fractionPart = numberStr.substring(numberStr.indexOf(POINT));
        }
        if (numberStr.startsWith(MINUS + "0")) {
            formattedIntPart = MINUS + formattedIntPart;
        }
        return formattedIntPart + fractionPart;

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
        format.setDecimalSeparatorAlwaysShown(true);
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
        int fractionDigitsCount = getCountFractionDigits(number.toPlainString());
        BigDecimal tail = number.remainder(ONE);
        if (tail.abs().compareTo(new BigDecimal(0.99)) > 0 &&
                tail.toPlainString().length() > MAX_LENGTH_WITH_POINT_AND_MINUS) {
            fractionDigitsCount--;
        }
        format.setMaximumFractionDigits(fractionDigitsCount);
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
        String formattedNumber = number.toLowerCase();
        Pattern pattern = Pattern.compile(EXPONENT_PATTERN);
        Matcher matcher = pattern.matcher(number);
        if (matcher.find()) { // If exponent has no sign after add plus
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
        if (number.startsWith(MINUS + "0" + POINT)) {
            fractionDigitsCount = FRACTION_LENGTH_WITH_MINUS_ZERO - number.indexOf(POINT);
        }
        if (number.startsWith("0" + POINT)) {
            fractionDigitsCount = FRACTION_LENGTH_WITH_ZERO - number.indexOf(POINT);
        }

        return fractionDigitsCount;
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

    /**
     * Removes group delimiters from the specified number represented by string and returns formatted string.
     *
     * @param number the string with number to format
     * @return the formatted string containing number without group delimiters
     */
    public static String removeGroupDelimiters(String number) {
        return number.replaceAll(String.valueOf(GROUP_SEPARATOR), EMPTY_VALUE).toLowerCase();
    } // TODO remove it

}
