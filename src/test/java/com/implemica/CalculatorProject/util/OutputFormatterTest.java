package com.implemica.CalculatorProject.util;

import com.implemica.CalculatorProject.calculation.MathOperation;
import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.formatNumberForDisplaying;
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

        // other operation that have no formatting
        testUnaryFormatting("", NEGATE, "-5");
        testUnaryFormatting("", ADD, "-5");
        testUnaryFormatting("", MULTIPLY, "-5");
        testUnaryFormatting("", SUBTRACT, "-5");
    }

    private void testUnaryFormatting(String expected, MathOperation operation, String input) {
        assertEquals(expected, formatUnaryOperation(operation, input));
    }

    @Test
    public void testGroupDelimitersFormatting() throws CalculationException {
        // remove group delimiters
        assertEquals("555555", removeGroupDelimiters("555,555"));
        assertEquals("0", removeGroupDelimiters("0"));
        assertEquals("125555.91", removeGroupDelimiters("125,555.91"));
        assertEquals("-555555", removeGroupDelimiters("-555,555"));
        assertEquals("121232343454565676", removeGroupDelimiters("121,232,343,454,565,676"));
        assertEquals("0.987654321", removeGroupDelimiters("0.987654321"));
        assertEquals("-0.987654321", addGroupDelimiters("-0.987654321"));
        assertEquals("5.e-21", removeGroupDelimiters("5.e-21"));

        // add group delimiters
        assertEquals("555,555", addGroupDelimiters("555555"));
        assertEquals("0", addGroupDelimiters("0"));
        assertEquals("125,555.91", addGroupDelimiters("125555.91"));
        assertEquals("-555,555", addGroupDelimiters("-555555"));
        assertEquals("232,343,454,565,676", addGroupDelimiters("232343454565676"));
        assertEquals("0.987654321", addGroupDelimiters("0.987654321"));
        assertEquals("-0.987654321", addGroupDelimiters("-0.987654321"));
        assertEquals("5.e-21", addGroupDelimiters("5.e-21"));
    }

    @Test
    public void testFormattingToMathView() throws CalculationException {

        // negative number
        testFormatToMathView("-9.999999999999999e+17", "-999999999999999900");
        testFormatToMathView("-1.e+16", "-10000000000000000");
        testFormatToMathView("-3.333333333333333", "-3.33333333333333333333333");
        testFormatToMathView("-5.55555555555555e-4", "-0.000555555555555555");
        testFormatToMathView("-5", "-5");
        testFormatToMathView("-0.0001", "-0.00010");

        // zero
        testFormatToMathView("0", "0");
        testFormatToMathView("0", "0.00");

        // positive number
        testFormatToMathView("1.e-17", "0.00000000000000001");
        testFormatToMathView("4.572473708276177e-4", "0.0004572473708276177258");
        testFormatToMathView("1.666666666666667", "1.66666666666666666666666");
        testFormatToMathView("3.333333333333333", "3.33333333333333333333333");
        testFormatToMathView("125555.91", "125,555.91");
        testFormatToMathView("555555", "555,555");
        testFormatToMathView("9999999999999999", "9,999,999,999,999,999");
        testFormatToMathView("1.e+16", "10000000000000000");
        testFormatToMathView("1.e+16", "1.E16");
        testFormatToMathView("9.999999999999999e+17", "999999999999999900");
    }

    private void testFormatToMathView(String expected, String numberString) throws CalculationException {
        assertEquals(expected, formatToMathView(numberString));

        BigDecimal number = new BigDecimal(removeGroupDelimiters(numberString));
        assertEquals(expected, formatToMathView(number));
    }

    @Test
    public void testFormattingForDisplaying() throws CalculationException {
        // negative number
        testFormattingForDisplaying("-9.999999999999999e+17", "-999999999999999900");
        testFormattingForDisplaying("-1.e+16", "-10000000000000000");
        testFormattingForDisplaying("-3.333333333333333", "-3.33333333333333333333333");
        testFormattingForDisplaying("-5.55555555555555e-4", "-0.000555555555555555");
        testFormattingForDisplaying("-5", "-5");
        testFormattingForDisplaying("-3.333333333333333", "-3.33333333333333333333333");
        testFormattingForDisplaying("-1.666666666666667", "-1.66666666666666666666666");
        testFormattingForDisplaying("-1", "-0.999999999999999999999999");
        testFormattingForDisplaying("-0.0001", "-0.00010");
        testFormattingForDisplaying("-4.572473708276177e-4", "-0.0004572473708276177258");
        testFormattingForDisplaying("-1.e-17", "-0.00000000000000001");

        // zero
        testFormattingForDisplaying("0", "0");
        testFormattingForDisplaying("0", "0.");
        testFormattingForDisplaying("0", "0.00");
        testFormattingForDisplaying("0", "0.0000000000000");

        // positive number
        testFormattingForDisplaying("1", "0.999999999999999999999999");
        testFormattingForDisplaying("1.e-17", "0.00000000000000001");
        testFormattingForDisplaying("4.572473708276177e-4", "0.0004572473708276177258");
        testFormattingForDisplaying("1.666666666666667", "1.66666666666666666666666");
        testFormattingForDisplaying("3.333333333333333", "3.33333333333333333333333");
        testFormattingForDisplaying("125,555.91", "125555.91");
        testFormattingForDisplaying("555,555", "555555");
        testFormattingForDisplaying("9,999,999,999,999,999", "9999999999999999");
        testFormattingForDisplaying("1.e+16", "10000000000000000");
        testFormattingForDisplaying("1.e+16", "1.E16");
        testFormattingForDisplaying("9.999999999999999e+17", "999999999999999900");
    }

    private void testFormattingForDisplaying(String expected, String inputNumber) throws CalculationException {
        assertEquals(expected, formatNumberForDisplaying(inputNumber));
    }

    @Test
    public void testInvalidInput() {
        testForException("");
        testForException(" ");
        testForException("--1");
        testForException("1...1");
        testForException("1eee-10");
        testForException("1.e+-10");
        testForException("+40");
        testForException("some string");
    }

    private void testForException(String number) {
        try {
            formatNumberForDisplaying(number);
            fail("Expected CalculationException with input not number. Your input is " + number);
        } catch (CalculationException e) {
            //expected
        }
    }
}
