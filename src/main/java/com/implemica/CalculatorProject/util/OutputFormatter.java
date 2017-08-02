package com.implemica.CalculatorProject.util;

import com.implemica.CalculatorProject.calculation.MathOperation;
import com.implemica.CalculatorProject.exception.CalculationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.implemica.CalculatorProject.processing.InputValueProcessor.ZERO_VALUE;
import static com.implemica.CalculatorProject.validation.DataValidator.*;

public class OutputFormatter {


    private static final String EXPONENT = "E";

    public static final String MINUS = "-";

    public static final String EMPTY_VALUE = "";

    private static final String MINUS_ZERO = "-0";

    private static final String NUMBER_FORMAT_PATTERN = "0.###############E0";
    private static final String EXPONENT_REPLACEMENT = "e+";

    /**
     * The string contains a point for double numbers.
     */
    public static final String POINT = ".";

    private static final char DECIMAL_SEPARATOR = '.';
    private static final char GROUP_SEPARATOR = ',';

    private static final String EXPONENT_PATTERN = "[Ee]\\d+";
    private static final String NUMBER_WITH_EXPONENT_PATTERN = "([-]?\\d+[.]?\\d*[1-9]*)([0+]*e)(.*)";
    private static final String NUMBER_LESS_ONE_WITH_TRAILING_9_PATTERN = "([-]?\\d+[.]\\d*[0-8]*)(9{2,})(e.*)?";
    private static final String NUMBER_GREATER_ONE_WITH_TRAILING_9_PATTERN = "([-]?9+[.])(9+)(e.*)?";

    private static final String SQUARE_ROOT_PATTERN = "√(%s)";
    private static final String SQUARE_PATTERN = "sqr(%s)";
    private static final String REVERSE_PATTERN = "1/(%s)";

    private static final String INVALID_INPUT_ERROR = "Invalid input";

    /**
     * Formats the specified number represented by string to Mathematical view. Removes group delimiters
     * from the number and trailing zeroes in fractional part of the number and converts to exponential view
     * if needed.
     *
     * @param number the string with number to format
     * @return the formatted string containing number for the next calculations
     */
    public static String formatToMathView(String number) throws CalculationException {
        String formattedNumber = removeGroupDelimiters(number);
        if (!isNumber(formattedNumber)) {
            throw new CalculationException(INVALID_INPUT_ERROR);
        }
        int scale = 0;
        if (formattedNumber.contains(POINT)) {
            if (formattedNumber.toUpperCase().contains(EXPONENT)) {
                scale = formattedNumber.toUpperCase().indexOf(EXPONENT) - formattedNumber.indexOf(POINT);
            } else {
                scale = formattedNumber.length() - formattedNumber.indexOf(POINT);
            }
        }
        return formatToMathView(new BigDecimal(formattedNumber).setScale(scale, RoundingMode.HALF_UP));
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

    public static String formatToMathView(BigDecimal calculationResult) throws CalculationException {
        if (BigDecimal.ZERO.compareTo(calculationResult) == 0) {
            return ZERO_VALUE;
        }
        //Without trailing zeroes after point and with groups delimiters
        String number = formatNumberForDisplaying(calculationResult.toString());
        number = removeGroupDelimiters(number);
        return number;
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
        BigDecimal number = new BigDecimal(numberStr);
        if (number.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO_VALUE;
        }

        if (!numberStr.toUpperCase().contains(EXPONENT)) { // Round if there are trailing "9"
            if (number.abs().compareTo(BigDecimal.ONE) > 0) {
                Pattern pattern2 = Pattern.compile(NUMBER_GREATER_ONE_WITH_TRAILING_9_PATTERN);
                Matcher matcher2 = pattern2.matcher(removeGroupDelimiters(numberStr));
                if (matcher2.matches() && numberStr.length() >= MAX_LENGTH_WITH_POINT) {
                    // If after point is all 9s up to max length
                    int scale = 0;
                    return number.setScale(scale, BigDecimal.ROUND_UP).toString();

                }
            } else {
                Pattern pattern = Pattern.compile(NUMBER_LESS_ONE_WITH_TRAILING_9_PATTERN);
                Matcher matcher = pattern.matcher(removeGroupDelimiters(numberStr));
                if (matcher.matches() && numberStr.length() > MAX_LENGTH_WITH_POINT) {
                    int scale = numberStr.indexOf(matcher.group(2)) - numberStr.indexOf(POINT) - 1;
                    return number.setScale(scale, BigDecimal.ROUND_UP).toString();
                }
            }

        }

        String stringValue;
        if (isExponentFormattingNeed(number)) {
            stringValue = formatToExponentialView(number);
        } else {
            stringValue = formatWithRounding(number);
        }
        return stringValue;
    }

    private static String formatToExponentialView(BigDecimal number) {
        DecimalFormat format = new DecimalFormat(NUMBER_FORMAT_PATTERN);
        format.setRoundingMode(RoundingMode.HALF_UP);
        format.setDecimalFormatSymbols(getDelimiters());
        String formattedNumber = format.format(number).toLowerCase();
        return adjustExponentialView(formattedNumber);
    }

    private static String formatWithRounding(BigDecimal number) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(getCountFractionDigits(number.toPlainString()));
        format.setRoundingMode(RoundingMode.HALF_UP);
        format.setDecimalFormatSymbols(getDelimiters());
        return format.format(number).toLowerCase();
    }

    private static DecimalFormatSymbols getDelimiters() {
        DecimalFormatSymbols delimiters = new DecimalFormatSymbols();
        delimiters.setDecimalSeparator(DECIMAL_SEPARATOR);
        delimiters.setGroupingSeparator(GROUP_SEPARATOR);
        return delimiters;
    }

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
                formattedNumber = matcher.replaceAll("$1e$3"); // Remove trailing zeroes before exponent
            } else {
                formattedNumber = matcher.replaceAll("$1.e$3"); // Remove trailing zeroes before exponent and add point
            }
        }
        return formattedNumber;
    }

    private static int getCountFractionDigits(String number) {
        if (!number.contains(POINT)) {
            return 0;
        }
        if (number.startsWith(MINUS + "0.")) {
            return MAX_LENGTH_WITH_POINT_AND_MINUS - number.indexOf(POINT) - 1;
        }
        if (number.startsWith(MINUS)) {
            return MAX_LENGTH_WITH_POINT_AND_MINUS - number.indexOf(POINT) - 2;
        }
        if (number.startsWith("0.")) {
            return MAX_LENGTH_WITH_POINT - number.indexOf(POINT);
        }
        return MAX_LENGTH_WITH_POINT - number.indexOf(POINT) - 1;
    }

    public static String addGroupDelimiters(String number) throws CalculationException {
        if (!isNumber(number)) {
            throw new CalculationException(INVALID_INPUT_ERROR);
        }
        if (new BigDecimal(number).compareTo(BigDecimal.ZERO) == 0) {
            return number;
        }
        String formattedIntPart;
        int pointIndex = number.indexOf(POINT);
        if (pointIndex == -1) {
            return formatNumberForDisplaying(number);
        } else {
            formattedIntPart = formatNumberForDisplaying(number.substring(0, pointIndex));
        }
        String formattedNumber = formattedIntPart + number.substring(pointIndex, number.length());
        if (number.startsWith(MINUS_ZERO)) {
            formattedNumber = MINUS + formattedNumber;
        }
        return formattedNumber;
    }

    /*
    MathOperation      In expression    In current number text field

    Square root ->      √ (x)        ->      result
    Square      ->      sqr(x)       ->      result
    Reverse     ->      1/(x)        ->      result
    Percentage  ->      y + result   ->      result
    Negate      ->      none         ->      result
 */


    public static String formatUnaryOperation(MathOperation operation, String argument) {
        switch (operation) {
            case SQUARE_ROOT:
                return String.format(SQUARE_ROOT_PATTERN, argument);
            case SQUARE:
                return String.format(SQUARE_PATTERN, argument);
            case REVERSE:
                return String.format(REVERSE_PATTERN, argument);
        }
        return EMPTY_VALUE;
    }
}
