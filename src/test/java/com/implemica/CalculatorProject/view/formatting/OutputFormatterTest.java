package com.implemica.CalculatorProject.view.formatting;

import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.exception.CalculationException;
import org.junit.Test;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.model.calculation.CalculatorTest.extractOperation;
import static com.implemica.CalculatorProject.view.formatting.OutputFormatter.*;
import static org.junit.Assert.assertEquals;

public class OutputFormatterTest {

    private static final String ARGUMENT_DELIMITERS = "\\s+[=>\\s]*";

    @Test
    public void testUnaryOperationFormat() {

        // square root operation
        testUnaryFormatting("√ 5", "√(5)");
        testUnaryFormatting("√ √ 5", "√(√(5))");
        testUnaryFormatting("√ √ √ 5", " √(√(√(5)))");
        testUnaryFormatting("√ 0.25", "√(0.25)");
        testUnaryFormatting("√ 0", "√(0)");
        testUnaryFormatting("√ -5", "√(-5)");

        // square operation
        testUnaryFormatting("sqr 5", "sqr(5)");
        testUnaryFormatting("sqr sqr 5", "sqr(sqr(5))");
        testUnaryFormatting("sqr sqr sqr 5", "sqr(sqr(sqr(5)))");
        testUnaryFormatting("sqr 0.25", "sqr(0.25)");
        testUnaryFormatting("sqr 0", "sqr(0)");
        testUnaryFormatting("sqr -5", "sqr(-5)");

        // reverse operation
        testUnaryFormatting("1/ 5", "1/(5)");
        testUnaryFormatting("1/ 1/ 5", "1/(1/(5))");
        testUnaryFormatting("1/ 1/ 1/ 5", "1/(1/(1/(5)))");
        testUnaryFormatting("1/ 0.25", " 1/(0.25)");
        testUnaryFormatting("1/ 0 ", "1/(0)");
        testUnaryFormatting("1/ -5", " 1/(-5)");
    }

    private void testUnaryFormatting(String expression, String expectedHistory) {
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int index = expressionParts.length - 1;

        String formattedInput = expressionParts[index--];

        for (int i = 0; i <= index; i++) {
            MathOperation operation = extractOperation(expressionParts[i]);
            formattedInput = formatUnaryOperation(operation, formattedInput);
        }

        assertEquals(expectedHistory.trim(), formattedInput.trim());
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
        // formatting number
        BigDecimal number = new BigDecimal(removeGroupDelimiters(inputNumber));
        assertEquals(expected, formatToMathView(number));
    }

    private String removeGroupDelimiters(String number) {
        return number.replaceAll(",", "").toLowerCase();
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
//        testFormatForDisplaying("0", "0.");
//        testFormatForDisplaying("0", "0.00");
//        testFormatForDisplaying("0", "0.0000000000000");

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
        assertEquals(expected, formatWithGroupDelimiters(new BigDecimal(inputNumber)));
    }
}