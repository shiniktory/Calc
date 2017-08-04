package com.implemica.CalculatorProject.util;

import com.implemica.CalculatorProject.calculation.MathOperation;
import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.Test;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OutputFormatterTest {

    @Test
    public void testUnaryOperationFormat() {

        // square root operation
        testUnaryFormatting("√(5)", SQUARE_ROOT, "5");
        testUnaryFormatting("√(√(5))", SQUARE_ROOT, "√(5)");
        testUnaryFormatting("√(√(√(5)))", SQUARE_ROOT, "√(√(5))");
        testUnaryFormatting("√(0.25)", SQUARE_ROOT, "0.25");
        testUnaryFormatting("√(0)", SQUARE_ROOT, "0");
        testUnaryFormatting("√(-5)", SQUARE_ROOT, "-5");

        // square operation
        testUnaryFormatting("sqr(5)", SQUARE, "5");
        testUnaryFormatting("sqr(sqr(5))", SQUARE, "sqr(5)");
        testUnaryFormatting("sqr(sqr(sqr(5)))", SQUARE, "sqr(sqr(5))");
        testUnaryFormatting("sqr(0.25)", SQUARE, "0.25");
        testUnaryFormatting("sqr(0)", SQUARE, "0");
        testUnaryFormatting("sqr(-5)", SQUARE, "-5");

        // reverse operation
        testUnaryFormatting("1/(5)", REVERSE, "5");
        testUnaryFormatting("1/(1/(5))", REVERSE, "1/(5)");
        testUnaryFormatting("1/(1/(1/(5)))", REVERSE, "1/(1/(5))");
        testUnaryFormatting("1/(0.25)", REVERSE, "0.25");
        testUnaryFormatting("1/(0)", REVERSE, "0");
        testUnaryFormatting("1/(-5)", REVERSE, "-5");

        // unary operation that have no formatting for history - returns an empty string
        testUnaryFormatting("", NEGATE, "-5");
    }

    private void testUnaryFormatting(String expected, MathOperation operation, String input) {
        String formattedInput = formatUnaryOperation(operation, input);
        assertEquals(expected, formattedInput);
    }

    @Test
    public void testFormatNumberWithDelimiters() throws CalculationException {

        // negative numbers
        testFormatGroupDelimiters("-999999999999999", "-999,999,999,999,999");
        testFormatGroupDelimiters("-555555", "-555,555");
        testFormatGroupDelimiters("-5555.5555", "-5,555.5555");
        testFormatGroupDelimiters("-12.5", "-12.5");
        testFormatGroupDelimiters("-0.987654321", "-0.987654321");
        testFormatGroupDelimiters("-5.e-21", "-5.e-21");
        testFormatGroupDelimiters("0", "0");

        // positive numbers
        testFormatGroupDelimiters("5.e-21", "5.e-21");
        testFormatGroupDelimiters("0.987654321", "0.987654321");
        testFormatGroupDelimiters("12.5", "12.5");
        testFormatGroupDelimiters("5555.5555", "5,555.5555");
        testFormatGroupDelimiters("125555.91", "125,555.91");
        testFormatGroupDelimiters("555555", "555,555");
        testFormatGroupDelimiters("232343454565676", "232,343,454,565,676");
    }

    private void testFormatGroupDelimiters(String expectedNoDelimiters, String inputWithDelimiters) throws CalculationException {
        // Remove group delimiter from input number string
        String numberWithoutGroupDelimiters = removeGroupDelimiters(inputWithDelimiters);
        assertEquals(expectedNoDelimiters, numberWithoutGroupDelimiters);

        // Add group delimiters to number got before and check it's equivalence with input number
        String numberWithGroupDelimiters = addGroupDelimiters(numberWithoutGroupDelimiters);
        assertEquals(inputWithDelimiters, numberWithGroupDelimiters);
    }

    @Test
    public void testFormatNumberToMathView() throws CalculationException {

        // negative numbers
        testFormatNumberToMathView("-9.999999999999999e+17", "-999,999,999,999,999,900");
        testFormatNumberToMathView("-1.e+16", "-10,000,000,000,000,000");
        testFormatNumberToMathView("-125555.91", "-125,555.91");
        testFormatNumberToMathView("-555555", "-555,555");
        testFormatNumberToMathView("-5", "-5");
        testFormatNumberToMathView("-3.333333333333333", "-3.33333333333333333333333");
        testFormatNumberToMathView("-5.55555555555555e-4", "-0.000555555555555555");
        testFormatNumberToMathView("-4.572473708276177e-4", "-0.0004572473708276177258");
        testFormatNumberToMathView("-0.0001", "-0.00010");
        testFormatNumberToMathView("-0.0001", "-0.0001");

        // zero
        testFormatNumberToMathView("0", "0");
        testFormatNumberToMathView("0", "0.00");

        // positive numbers
        testFormatNumberToMathView("1.e-17", "0.00000000000000001");
        testFormatNumberToMathView("4.572473708276177e-4", "0.0004572473708276177258");
        testFormatNumberToMathView("1.666666666666667", "1.66666666666666666666666");
        testFormatNumberToMathView("3.333333333333333", "3.33333333333333333333333");
        testFormatNumberToMathView("125555.91", "125,555.91");
        testFormatNumberToMathView("555555", "555,555");
        testFormatNumberToMathView("9999999999999999", "9,999,999,999,999,999");
        testFormatNumberToMathView("1.e+16", "10,000,000,000,000,000");
        testFormatNumberToMathView("1.e+16", "1.E16");
        testFormatNumberToMathView("9.999999999999999e+17", "999,999,999,999,999,900");
    }

    private void testFormatNumberToMathView(String expected, String inputNumber) throws CalculationException {
        // formatting string contains number
        assertEquals(expected, formatToMathView(inputNumber));

        // formatting number
        BigDecimal number = new BigDecimal(removeGroupDelimiters(inputNumber));
        assertEquals(expected, formatToMathView(number));
    }

    @Test
    public void testFormatNumberForDisplaying() throws CalculationException {

        // negative numbers
        testFormatForDisplaying("-9.999999999999999e+17", "-999999999999999900");
        testFormatForDisplaying("-1.e+16", "-10000000000000000");
        testFormatForDisplaying("-676,767", "-676767");
        testFormatForDisplaying("-9", "-9");
        testFormatForDisplaying("-3.333333333333333", "-3.33333333333333333333333");
        testFormatForDisplaying("-5.55555555555555e-4", "-0.000555555555555555");
        testFormatForDisplaying("-5", "-5");
        testFormatForDisplaying("-3.333333333333333", "-3.33333333333333333333333");
        testFormatForDisplaying("-1.666666666666667", "-1.66666666666666666666666");
        testFormatForDisplaying("-1", "-0.999999999999999999999999");
        testFormatForDisplaying("-0.0001", "-0.00010");
        testFormatForDisplaying("-4.572473708276177e-4", "-0.0004572473708276177258");
        testFormatForDisplaying("-1.e-17", "-0.00000000000000001");

        // zero
        testFormatForDisplaying("0", "0");
        testFormatForDisplaying("0", "0.");
        testFormatForDisplaying("0", "0.00");
        testFormatForDisplaying("0", "0.0000000000000");

        // positive numbers
        testFormatForDisplaying("1", "0.999999999999999999999999");
        testFormatForDisplaying("1.e-17", "0.00000000000000001");
        testFormatForDisplaying("4.572473708276177e-4", "0.0004572473708276177258");
        testFormatForDisplaying("1.666666666666667", "1.66666666666666666666666");
        testFormatForDisplaying("3.333333333333333", "3.33333333333333333333333");
        testFormatForDisplaying("9", "9");
        testFormatForDisplaying("125,555.91", "125555.91");
        testFormatForDisplaying("555,555", "555555");
        testFormatForDisplaying("676,767", "676767");
        testFormatForDisplaying("9,999,999,999,999,999", "9999999999999999");
        testFormatForDisplaying("1.e+16", "10000000000000000");
        testFormatForDisplaying("1.e+16", "1.E16");
        testFormatForDisplaying("9.999999999999999e+17", "999999999999999900");
    }

    private void testFormatForDisplaying(String expected, String inputNumber) throws CalculationException {
        assertEquals(expected, formatNumberForDisplaying(inputNumber));
    }

    @Test
    public void testInvalidInput() {

        testFormatNotNumber("");
        testFormatNotNumber(" ");
        testFormatNotNumber(".");
        testFormatNotNumber("...");
        testFormatNotNumber("--1");
        testFormatNotNumber("1--");
        testFormatNotNumber("1...1");
        testFormatNotNumber("1eee-10");
        testFormatNotNumber("1.e+-10");
        testFormatNotNumber("1.e+10.");
        testFormatNotNumber("+40");
        testFormatNotNumber("40+");
        testFormatNotNumber("40.+");
        testFormatNotNumber("some string");
    }

    private void testFormatNotNumber(String notNumber) {
        try {
            formatNumberForDisplaying(notNumber);
            formatToMathView(notNumber);
            fail("Expected CalculationException with input not a number. Your input is " + notNumber);
        } catch (CalculationException e) {
            //expected
        }
    }
}