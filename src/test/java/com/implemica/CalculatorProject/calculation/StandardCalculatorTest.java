package com.implemica.CalculatorProject.calculation;

import com.implemica.CalculatorProject.exception.CalculationException;
import com.implemica.CalculatorProject.processing.InputValueProcessor;
import org.junit.Test;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.*;
import static com.implemica.CalculatorProject.util.ValueTransformerUtil.getBigDecimalValues;
import static com.implemica.CalculatorProject.validation.DataValidator.isEmptyString;
import static com.implemica.CalculatorProject.validation.DataValidator.isNumber;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StandardCalculatorTest {

    private static final String ARGUMENT_DELIMITERS = "\\s+[=\\s]*";

    private final InputValueProcessor valueProcessor = new InputValueProcessor();

    @Test
    public void testAddOperation() throws CalculationException {

        // with zero arguments
        testCalculations("0 + 555000000 = 555,000,000", "0 + ");
        testCalculations("0 + 1000 = 1,000", "0 + ");
        testCalculations("0 + 100 = 100", "0 + ");
        testCalculations("0 + 0.6666666666666667 = 0.6666666666666667", "0 + ");
        testCalculations("0 + 0 = 0", "0 + ");
        testCalculations("0 + -0.6666666666666667 = -0.6666666666666667", "0 + ");
        testCalculations("0 + -100 = -100", "0 + ");
        testCalculations("0 + -1000 = -1,000", "0 + ");
        testCalculations("0 + -555000000 = -555,000,000", "0 + ");

        // with positive arguments
        testCalculations("9999999999999999 + 9999999999999999 = 2.e+16", "9999999999999999 + ");
        testCalculations("5 + 55550 = 55,555", "5 + ");
        testCalculations("0.6666666666666667 + 0.0000000000000003 = 0.666666666666667", "0.6666666666666667 + ");
        testCalculations("0.000005 + 0.0000000000005 = 0.0000050000005", "0.000005 + ");

        // with positive and negative arguments
        testCalculations("9999999999999999 + -9999999999999999 = 0", "9999999999999999 + ");
        testCalculations("1000000.5 + -50000000 = -48,999,999.5", "1000000.5 + ");
        testCalculations("1000000 + -50000000 = -49,000,000", "1000000 + ");
        testCalculations("100 + -50 = 50", "100 + ");
        testCalculations("5 + -5 = 0", "5 + ");
        testCalculations("0.05 + -0.05 = 0", "0.05 + ");
        testCalculations("0.000005 + -0.0000000000005 = 0.0000049999995", "0.000005 + ");

        // with negative and positive arguments
        testCalculations("-5 + 55550 = 55,545", "-5 + ");
        testCalculations("-100 + 50 = -50", "-100 + ");
        testCalculations("-2147483648 + 2147483647 = -1", "-2147483648 + ");

        // with both negative arguments
        testCalculations("-0.6 + -0.6 = -1.2", "-0.6 + ");
        testCalculations("-0.000005 + -0.0000000000005 = -0.0000050000005", "-0.000005 + ");
        testCalculations("-0.1 + -0.1 = -0.2", "-0.1 + ");
        testCalculations("-1000000 + -50000000 = -51,000,000", "-1000000 + ");
        testCalculations("-1000000 + -50000000.5 = -51,000,000.5", "-1000000 + ");
        testCalculations("-9999999999999999 + -9999999999999999 = -2.e+16", "-9999999999999999 + ");

        // with more than one operation in sequence
        testCalculations("-2 + 3 + 5 = 6", "-2 + 3 + ");
        testCalculations("0 + 0 + 0 = 0", "0 + 0 + ");
        testCalculations("0 + 5 ÷ 500 = 0.01", "0 + 5 ÷ ");
        testCalculations("2 + 3 + 5 = 10", "2 + 3 + ");
        testCalculations("5000 + -6 + 50 = 5,044", "5000 + -6 + ");
        testCalculations("999999999999999 + 1 ÷ 2 = 500,000,000,000,000", "999999999999999 + 1 ÷ ");
        testCalculations("9999999999999999 + 1 ÷ 2 = 5,000,000,000,000,000", "9999999999999999 + 1 ÷ ");
    }

    @Test
    public void testSubtractOperation() throws CalculationException {

        // with zero arguments
        testCalculations("0 − 555000000 = -555,000,000", "0 − ");
        testCalculations("0 − 1000 = -1,000", "0 − ");
        testCalculations("0 − 100 = -100", "0 − ");
        testCalculations("0 − 0.6666666666666667 = -0.6666666666666667", "0 − ");
        testCalculations("0 − 0 = 0", "0 − ");
        testCalculations("0 − -0.6666666666666667 = 0.6666666666666667", "0 − ");
        testCalculations("0 − -100 = 100", "0 − ");
        testCalculations("0 − -1000 = 1,000", "0 − ");
        testCalculations("0 − -555000000 = 555,000,000", "0 − ");

        // with positive arguments
        testCalculations("9999999999999999 − 9999999999999999 = 0", "9999999999999999 − ");
        testCalculations("9999999999999999 − -9999999999999999 = 2.e+16", "9999999999999999 − ");
        testCalculations("2147483647 − 2147483647 = 0", "2147483647 − ");
        testCalculations("5 − 55550 = -55,545", "5 − ");
        testCalculations("0.6666666666666667 − 0.0000000000000003 = 0.6666666666666664", "0.6666666666666667 − ");
        testCalculations("0.000005 − 0.0000000000005 = 0.0000049999995", "0.000005 − ");

        //with positive and negative arguments
        testCalculations("1000000.5 − -50000000 = 51,000,000.5", "1000000.5 − ");
        testCalculations("1000000 − -50000000 = 51,000,000", "1000000 − ");
        testCalculations("100 − -50 = 150", "100 − ");
        testCalculations("5 − -5 = 10","5 − ");
        testCalculations("0.05 − -0.05 = 0.1", "0.05 − ");
        testCalculations("0.000005 − -0.0000000000005 = 0.0000050000005", "0.000005 − ");

        // with negative and positive arguments
        testCalculations("-5 − 55550 = -55,555", "-5 − ");
        testCalculations("-1000000.5 − 50000000 = -51,000,000.5", "-1000000.5 − ");
        testCalculations("-1000000 − 50000000 = -51,000,000", "-1000000 − ");
        testCalculations("-100 − 50 = -150", "-100 − ");
        testCalculations("-5 − 5 = -10", "-5 − ");
        testCalculations("-0.05 − 0.05 = -0.1", "-0.05 − ");
        testCalculations("-0.000005 − 0.0000000000005 = -0.0000050000005", "-0.000005 − ");

        // with negative arguments
        testCalculations("-0.6 − -0.6 = 0", "-0.6 − ");
        testCalculations("-0.000005 − -0.0000000000005 = -0.0000049999995", "-0.000005 − ");
        testCalculations("-0.1 − -0.1 = 0", "-0.1 − ");
        testCalculations("-1000000 − -50000000 = 49,000,000", "-1000000 − ");
        testCalculations("-1000000 − -50000000.5 = 49,000,000.5", "-1000000 − ");
        testCalculations("-9999999999999999 − -9999999999999999 = 0", "-9999999999999999 − ");

        // with more than one operation in sequence
        testCalculations("-2 − 3 + 5 = 0", "-2 − 3 + ");
        testCalculations("0 − 0 + 0 = 0", "0 − 0 + ");
        testCalculations("0 − 5 ÷ 500 = -0.01", "0 − 5 ÷ ");
        testCalculations("2 − 3 + 5 = 4", "2 − 3 + ");
        testCalculations("5000 − -6 + 50 = 5,056", "5000 − -6 + ");
        testCalculations("999999999999999 − 1 ☓ 2 = 1,999,999,999,999,996", "999999999999999 − 1 ☓ ");
        testCalculations("9999999999999999 − 1 ÷ 2 = 4,999,999,999,999,999", "9999999999999999 − 1 ÷ ");
    }

    @Test
    public void testMultiplyOperation() throws CalculationException {

        // with zero arguments
        testCalculations("0 ☓ 555000000 = 0", "0 ☓ ");
        testCalculations("0 ☓ 1000 = 0", "0 ☓ ");
        testCalculations("0 ☓ 100 = 0", "0 ☓ ");
        testCalculations("0 ☓ 0.6666666666666667 = 0", "0 ☓ ");
        testCalculations("0 ☓ 0 = 0", "0 ☓ ");
        testCalculations("0 ☓ -0.6666666666666667 = 0", "0 ☓ ");
        testCalculations("0 ☓ -100 = 0", "0 ☓ ");
        testCalculations("0 ☓ -1000 = 0", "0 ☓ ");
        testCalculations("0 ☓ -555000000 = 0", "0 ☓ ");

        // positive argument on positive
        testCalculations("1000000.5 ☓ 50000000 = 50,000,025,000,000", "1000000.5 ☓ ");
        testCalculations("1000000 ☓ 50000000 = 50,000,000,000,000", "1000000 ☓ ");
        testCalculations("100 ☓ 50 = 5,000", "100 ☓ ");
        testCalculations("5 ☓ 55550 = 277,750", "5 ☓ ");
        testCalculations("5 ☓ 5 = 25", "5 ☓ ");
        testCalculations("0.6666666666666667 ☓ 0.0000000000000003 = 0.0000000000000002", "0.6666666666666667 ☓ ");
        testCalculations("0.05 ☓ 0.05 = 0.0025", "0.05 ☓ ");
        testCalculations("0.000005 ☓ 0.0000000000005 = 2.5e-18", "0.000005 ☓ ");
        testCalculations("0.000005 ☓ 0.1 = 0.0000005", "0.000005 ☓ ");

        // positive argument on negative
        testCalculations("1000000.5 ☓ -50000000 = -50,000,025,000,000","1000000.5 ☓ ");
        testCalculations("1000000 ☓ -50000000 = -50,000,000,000,000", "1000000 ☓ ");
        testCalculations("100 ☓ -50 = -5,000", "100 ☓ ");
        testCalculations("5 ☓ 55550 = 277,750", "5 ☓ ");
        testCalculations("5 ☓ -5 = -25", "5 ☓ ");
        testCalculations("0.6666666666666667 ☓ 0.0000000000000003 = 0.0000000000000002", "0.6666666666666667 ☓ ");
        testCalculations("0.05 ☓ -0.05 = -0.0025", "0.05 ☓ ");
        testCalculations("0.000005 ☓ -0.0000000000005 = -2.5e-18", "0.000005 ☓ ");
        testCalculations("0.000005 ☓ -0.1 = -0.0000005", "0.000005 ☓ ");

        // negative on positive
        testCalculations("-5 ☓ 55550 = -277,750", "-5 ☓ ");
        testCalculations("-100 ☓ 50 = -5,000", "-100 ☓ ");
        testCalculations("-2147483648 ☓ 2147483647 = -4.611686016279904e+18", "-2147483648 ☓ ");
        testCalculations("-0.6 ☓ 0.6 = -0.36", "-0.6 ☓ ");
        testCalculations("-0.000005 ☓ 0.0000000000005 = -2.5e-18", "-0.000005 ☓ ");
        testCalculations("-0.1 ☓ 0.1 = -0.01", "-0.1 ☓ ");
        testCalculations("-1000000 ☓ 50000000 = -50,000,000,000,000", "-1000000 ☓ ");
        testCalculations("-1000000 ☓ 50000000.5 = -50,000,000,500,000", "-1000000 ☓ ");

        // negative argument on negative
        testCalculations("-5 ☓ -55550 = 277,750", "-5 ☓ ");
        testCalculations("-100 ☓ -50 = 5,000", "-100 ☓ ");
        testCalculations("-0.6 ☓ -0.6 = 0.36", "-0.6 ☓ ");
        testCalculations("-0.000005 ☓ -0.0000000000005 = 2.5e-18", "-0.000005 ☓ ");
        testCalculations("-0.1 ☓ -0.1 = 0.01", "-0.1 ☓ ");
        testCalculations("-1000000 ☓ -50000000 = 50,000,000,000,000", "-1000000 ☓ ");
        testCalculations("-1000000 ☓ -50000000.5 = 50,000,000,500,000", "-1000000 ☓ ");

        // with more than one operation in sequence
        testCalculations("-1000000000000000 ÷ 3 ☓ 3 = -1,000,000,000,000,000", "-1000000000000000 ÷ 3 ☓ ");
        testCalculations("-1000 ÷ 3 ☓ 3 = -1,000", "-1000 ÷ 3 ☓ ");
        testCalculations("0 ÷ 50 − 0 = 0", "0 ÷ 50 − ");
        testCalculations("1 ÷ 3 ☓ 3 = 1", "1 ÷ 3 ☓ ");
        testCalculations("10 ÷ 3 ☓ 3 = 10", "10 ÷ 3 ☓ ");
        testCalculations("1000 ÷ 3 ☓ 3 = 1,000", "1000 ÷ 3 ☓ ");
        testCalculations("5000 ÷ -6 + 50 = -783.3333333333333", "5000 ÷ -6 + ");
    }

    @Test
    public void testDivideOperation() throws CalculationException {

        // zero by non-zero number
        testCalculations("0 ÷ 555000000 = 0");
        testCalculations("0 ÷ 1000 = 0");
        testCalculations("0 ÷ 100 = 0");
        testCalculations("0 ÷ 0.6666666666666667 = 0");
        testCalculations("0 ÷ -0.6666666666666667 = 0");
        testCalculations("0 ÷ -100 = 0");
        testCalculations("0 ÷ -1000 = 0");
        testCalculations("0 ÷ -555000000 = 0");

        // positive number by any non-zero number
        testCalculations("9999999999999999 ÷ 9999999999999999 = 1");
        testCalculations("9999999999999999 ÷ -9999999999999999 = -1");
        testCalculations("1000000.5 ÷ -50000000 = -0.02000001");
        testCalculations("1000000 ÷ -50000000 = -0.02");
        testCalculations("100 ÷ -50 = -2");
        testCalculations("5 ÷ 55550 = 9.000900090009001e-5");
        testCalculations("5 ÷ -5 = -1");
        testCalculations("0.05 ÷ -0.05 = -1");
        testCalculations("0.000005 ÷ 0.0000000000005 = 10,000,000");
        testCalculations("0.000005 ÷ -0.0000000000005 = -10,000,000");

        // negative number by any non-zero
        testCalculations("-0.6 ÷ -0.6 = 1");
        testCalculations("-0.000005 ÷ -0.0000000000005 = 10,000,000");
        testCalculations("-0.1 ÷ -0.1 = 1");
        testCalculations("-5 ÷ 55550 = -9.000900090009001e-5");
        testCalculations("-100 ÷ 50 = -2");
        testCalculations("-1000000 ÷ -50000000 = 0.02");
        testCalculations("-1000000 ÷ -50000000.5 = 0.0199999998");
        testCalculations("-2147483648 ÷ 2147483647 = -1.000000000465661");
        testCalculations("-9999999999999999 ÷ -9999999999999999 = 1");

        // with more than one operation in sequence
        testCalculations("-859999 ☓ 66 + 9999999 = -46,759,935");
        testCalculations("-1000 ☓ 3 ÷ 3 = -1,000");
        testCalculations("-2 ☓ 3 + 5 = -1");
        testCalculations("0 ☓ 0 − 1500 = -1,500");
        testCalculations("2 ☓ 3 + 5 = 11");
        testCalculations("90 ☓ 22222 ÷ -50 = -39,999.6");
        testCalculations("9999999999999999 ☓ 9999999999999999 ☓ 5 = 4.999999999999999e+32");
    }

    @Test
    public void testPercentOperation() throws CalculationException {

        // percentage for base zero and any percent
        testCalculations("0 + 555000000 % = 0");
        testCalculations("0 + 1000 % = 0");
        testCalculations("0 + 100 % = 0");
        testCalculations("0 + 0.6666666666666667 % = 0");
        testCalculations("0 + 0 % = 0");
        testCalculations("0 + -0.6666666666666667 % = 0");
        testCalculations("0 + -100 % = 0");
        testCalculations("0 + -1000 % = 0");
        testCalculations("0 + -555000000 % = 0");

        // percentage for positive base
        testCalculations("1000000.5  + -50000000 % = -499,999,249,999.5");
        testCalculations("1000000  + -50000000 % = -499,999,000,000");
        testCalculations("100  + -50 % = 50");
        testCalculations("5  + 55550 % = 2,782.5");
        testCalculations("5  + -5 % = 4.75");
        testCalculations("0.6666666666666667  + 0.0000000000000003 % = 0.6666666666666667");
        testCalculations("0.6666666666666667  + 0 % = 0.6666666666666667");
        testCalculations("0.05  + -0.05 % = 0.049975");
        testCalculations("0.000005  + 0.0000000000005 % = 5.000000000000025e-6");
        testCalculations("0.000005  + -0.0000000000005 % = 4.999999999999975e-6");

        // percentage for negative base
        testCalculations("-0.6 + -0.6 % = -0.5964");
        testCalculations("-0.000005 + -0.0000000000005 % = -4.999999999999975e-6");
        testCalculations("-0.1 + -0.1 % = -0.0999");
        testCalculations("-5 + 55550 % = -2,782.5");
        testCalculations("-5 + 0 % = -5");
        testCalculations("-100 + 50 % = -150");
        testCalculations("-1000000 + -50000000 % = 499,999,000,000");
        testCalculations("-1000000 + -50000000.5 % = 499,999,005,000");
    }

    @Test
    public void testNegateOperation() throws CalculationException {
        // Expression format: unaryOperation enteredNumber = expected result after unary operation
        // with positive argument
        testCalculations("± 9999999999999999 = -9,999,999,999,999,999");
        testCalculations("± 1000000.5 = -1,000,000.5");
        testCalculations("± 1000 = -1,000");
        testCalculations("± 100 = -100");
        testCalculations("± 0.6666666666666667 = -0.6666666666666667");
        testCalculations("± 0.05 = -0.05");
        testCalculations("± 0.0000000000005 = -0.0000000000005");

        // with zero
        testCalculations("± 0 = 0");

        // with negative argument
        testCalculations("± -0.0000000000005 = 0.0000000000005");
        testCalculations("± -0.05 = 0.05");
        testCalculations("± -0.6666666666666667 = 0.6666666666666667");
        testCalculations("± -100 = 100");
        testCalculations("± -1000 = 1,000");
        testCalculations("± -555000000 = 555,000,000");
        testCalculations("± -9999999999999999 = 9,999,999,999,999,999");

        // with more than one unary operations for one number
        testCalculations("± √ 0 = 0");
        testCalculations("± sqr 700 = 490,000");
        testCalculations("± sqr 50 = 2,500");
        testCalculations("± ± 55 = 55");
        testCalculations("± 1/ 9999999999999999 = -0.0000000000000001");
    }

    @Test
    public void testSquareOperation() throws CalculationException {
        // Expression format: enteredNumber = expected result after unary operation
        // with positive argument
        testCalculations("sqr 1000000.5 = 1,000,001,000,000.25");
        testCalculations("sqr 1000 = 1,000,000");
        testCalculations("sqr 100 = 10,000");
        testCalculations("sqr 0.6666666666666667 = 0.4444444444444445");
        testCalculations("sqr 0.05 = 0.0025");
        testCalculations("sqr 0.0000000000005 = 2.5e-25");

        // with zero
        testCalculations("sqr 0 = 0");

        // with negative argument
        testCalculations("sqr -0.0000000000005 = 2.5e-25");
        testCalculations("sqr -0.05 = 0.0025");
        testCalculations("sqr -0.6666666666666667 = 0.4444444444444445");
        testCalculations("sqr -100 = 10,000");
        testCalculations("sqr -1000 = 1,000,000");
        testCalculations("sqr -555000000 = 3.08025e+17");

        // with more than one unary operations for one number
        testCalculations("sqr sqr 0 = 0");
        testCalculations("sqr sqr 0.005 = 0.000000000625");
        testCalculations("sqr √ 700 = 700");
        testCalculations("sqr 1/ 55 = 3.305785123966942e-4");
        testCalculations("sqr sqr 9999999999999999 = 9.999999999999996e+63");
        testCalculations("sqr ± 55 = -3,025");
    }

    @Test
    public void testSquareRootOperation() throws CalculationException {
        // Expression format: enteredNumber = expected result after unary operation
        testCalculations("√ 9999999999999999 = 100,000,000");
        testCalculations("√ 58585858585 = 242,045.158152358");
        testCalculations("√ 1000000.5 = 1,000.000249999969");
        testCalculations("√ 1000 = 31.62277660168379");
        testCalculations("√ 100 = 10");
        testCalculations("√ 0.6666666666666667 = 0.8164965809277261");
        testCalculations("√ 0.05 = 0.223606797749979");
        testCalculations("√ 0.0000000000005 = 7.071067811865475e-7");
        testCalculations("√ 0 = 0");

        // with more than one unary operations for one number
        testCalculations("√ √ 0 = 0");
        testCalculations("√ sqr 700 = 700");
        testCalculations("√ √ 50 = 2.659147948472494");
        testCalculations("√ ± 55 = -7.416198487095663");
        testCalculations("√ 1/ 9999999999999999 = 0.00000001");
    }

    @Test
    public void testReverseOperation() throws CalculationException {
        // Expression format: enteredNumber = expected result after unary operation
        // positive argument
        testCalculations("1/ 9999999999999999 = 0.0000000000000001");
        testCalculations("1/ 555000000 = 1.801801801801802e-9");
        testCalculations("1/ 1000 = 0.001");
        testCalculations("1/ 100 = 0.01");
        testCalculations("1/ 0.6666666666666667 = 1.5");
        testCalculations("1/ 0.05 = 20");

        // negative argument
        testCalculations("1/ -0.05 = -20");
        testCalculations("1/ -0.6666666666666667 = -1.5");
        testCalculations("1/ -100 = -0.01");
        testCalculations("1/ -1000 = -0.001");
        testCalculations("1/ -555000000 = -1.801801801801802e-9");
        testCalculations("1/ -9999999999999999 = -0.0000000000000001");

        // with more than one unary operations for one number
        testCalculations("1/ 1/ 5 = 5");
        testCalculations("1/ 1/ -5 = -5");
        testCalculations("1/ ± -5 = 0.2");
        testCalculations("1/ √ 35 = 0.1690308509457034");
        testCalculations("1/ sqr -5 = 0.04");
    }


    private void testCalculations(String expression, String expectedHistory) throws CalculationException {
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        String expectedResult = expressionParts[expressionParts.length - 1];


        if (isNumber(expressionParts[0])) {
            performBinaryCalculations(expressionParts);
        } else {
            performUnaryOperations(expressionParts);
        }
        testHistory(expectedHistory);
        testResult(expectedResult);
    }

    private String performBinaryCalculations(String[] expressionParts) throws CalculationException {
        int index = expressionParts.length - 2;
        String lastEnteredNumber = expressionParts[index--];

        boolean wasPercentage = lastEnteredNumber.equals("%");
        if (wasPercentage) {
            lastEnteredNumber = expressionParts[index--];
        }

        StringBuilder history = new StringBuilder();
        for (int i = 0; i <= index; i++) {
            history.append(processElement(expressionParts, i));
        }
        enterNumber(lastEnteredNumber);
        history.append(processPercentage(wasPercentage));

        return history.toString();
    }

    private String processElement(String[] expressionParts, int index) throws CalculationException {
        String history;
        if (index % 2 == 0) { // even indexes for numbers
            String currentNumber = expressionParts[index];
            enterNumber(currentNumber);
            history = currentNumber + " ";

        } else { // odd indexes for math operations
            MathOperation operation = extractOperation(expressionParts[index]);
            valueProcessor.executeMathOperation(operation);
            history = operation.getCode() + " ";
        }
        return history;
    }

    private String processPercentage(boolean wasPercentage) throws CalculationException {
        String history = "";
        if (wasPercentage) {
            valueProcessor.executeMathOperation(PERCENT);
            String percentageResult = removeGroupDelimiters(valueProcessor.getLastNumber());
            history = percentageResult + " ";
        }
        return history;
    }


    private String performUnaryOperations(String[] expressionParts) throws CalculationException {
        valueProcessor.cleanAll();
        int index = expressionParts.length - 2;
        String base = expressionParts[index--];
        enterNumber(base);

        String history = "";
        // execute all unary operations
        for (int i = 0; i <= index; i++) {
            MathOperation operation = extractOperation(expressionParts[i]);

            if (operation != NEGATE && isEmptyString(history)) {
                history = base;
            }
            valueProcessor.executeMathOperation(operation);

            if (operation != NEGATE) {
                history = formatUnaryOperation(operation, history);

            } else if (isEmptyString(history)) { // if the first unary operations is negate replace base
                base = formatToMathView(valueProcessor.getLastNumber());
            }
        }
        return history;
    }

    private void enterNumber(String number) throws CalculationException {
        int i = 0;
        if (number.startsWith(MINUS)) { // skip minus
            i++;
        }
        for (; i < number.length(); i++) { // add digit or point
            String digit = String.valueOf(number.charAt(i));
            addDigit(digit);
        }
        if (number.startsWith(MINUS)) { // add minus
            valueProcessor.executeMathOperation(NEGATE);
        }
    }

    private void addDigit(String digit) throws CalculationException {
        if (POINT.equals(digit)) {
            valueProcessor.addPoint();
        } else {
            valueProcessor.updateCurrentNumber(digit);
        }
    }

    public static MathOperation extractOperation(String operation) {
        switch (operation) {
            case "1/":
                return REVERSE;
            case "sqr":
                return SQUARE;
            default:
                return MathOperation.getOperation(operation);
        }
    }

    private void testHistory(String expected) {
        assertEquals(expected.trim(), valueProcessor.getExpression().trim());
    }

    private void testResult(String expectedResult) throws CalculationException {
        assertEquals(expectedResult, valueProcessor.calculateResult());
    }

    @Test
    public void testInitWithWrongArgumentCount() {
        testInitForException(null, 0);
        testInitForException(null, 1);

        // binary operation with wrong argument count
        testInitForException(ADD, 0);
        testInitForException(ADD, 1);
        testInitForException(ADD, 3);
        testInitForException(ADD, 4);
        testInitForException(ADD, 10);

        // unary operation with wrong argument count
        testInitForException(SQUARE_ROOT, 0);
        testInitForException(SQUARE_ROOT, 2);
        testInitForException(SQUARE_ROOT, 3);
        testInitForException(SQUARE_ROOT, 4);
        testInitForException(SQUARE_ROOT, 10);
    }

    private void testInitForException(MathOperation operation, int argumentsCount) {
        try {
            new StandardCalculator(operation, new BigDecimal[argumentsCount]);
            fail(format("Expected CalculationException with wrong arguments. Your operation is %s, count of arguments is %d",
                    operation, argumentsCount));
        } catch (CalculationException e) {
            // expected
        }
    }

    @Test
    public void testOperationWithWrongArguments() {

        // division by zero
        testOperationForException("555000000 ÷ 0");
        testOperationForException("1000 ÷ 0");
        testOperationForException("100 ÷ 0");
        testOperationForException("0.6666666666666667 ÷ 0");
        testOperationForException("0 ÷ 0");
        testOperationForException("0 ÷ 0.0");
        testOperationForException("-0.6666666666666667 ÷ 0");
        testOperationForException("-100 ÷ 0");
        testOperationForException("-1000 ÷ 0");
        testOperationForException("-555000000 ÷ 0");

        // square root with negative argument
        testOperationForException("√ -1");
        testOperationForException("√ -5");
        testOperationForException("√ -5.5");
        testOperationForException("√ -10000000");
        testOperationForException("√ -2147483648");
        testOperationForException("√ -555555555000005");
        testOperationForException("√ -9999999999999999");
        testOperationForException("√ -9223372036854775808");

        // reverse with zero base
        testOperationForException("1/ 0");
        testOperationForException("1/ 0.0");

        // result is overflow
        testOperationForException("1.e-9999 ÷ 10");
        testOperationForException("1.e-9999 ÷ 100");
        testOperationForException("1.e-9999 ÷ 1000000");

        testOperationForException("1.e+9999 ☓ 10");
        testOperationForException("1.e+9999 ☓ 100");
        testOperationForException("1.e+9999 ☓ 1000000");
    }

    private void testOperationForException(String expression) {
        try {
            initAndCalculate(expression);
            fail(format("Expected CalculationException caused by wrong operation argument. Your expression: %s",
                    expression));
        } catch (CalculationException e) {
            // expected
        }
    }

    private void initAndCalculate(String expression) throws CalculationException {
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        MathOperation operation;
        String[] numbers;

        if (expressionParts.length == 2) { // Expression for unary operations has format: operation baseNumber
            operation = extractOperation(expressionParts[0]);
            numbers = new String[]{expressionParts[1]};

        } else { // Expression for binary operations has format: firstNumber operation secondNumber
            operation = extractOperation(expressionParts[1]);
            numbers = new String[]{expressionParts[0], expressionParts[2]};
        }

        Calculator calculator = new StandardCalculator(operation, getBigDecimalValues(numbers));
        calculator.calculate();
    }
}