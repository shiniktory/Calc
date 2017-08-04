package com.implemica.CalculatorProject.calculation;

import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.util.ValueTransformerUtil.getBigDecimalValues;
import static java.lang.String.format;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.junit.Assert.assertEquals;

public class StandardCalculatorTest {

    private static int scale;

    private static final String ARGUMENT_DELIMITERS = "\\s+[=%\\s]*";

    @Test
    public void testAddOperation() throws CalculationException {

        // with zero arguments
        testBinaryOperations("0 + 555000000 = 555000000");
        testBinaryOperations("0 + 1000 = 1000");
        testBinaryOperations("0 + 100 = 100");
        testBinaryOperations("0 + 0.6666666666666667 = 0.6666666666666667");
        testBinaryOperations("0 + 0 = 0");
        testBinaryOperations("0 + -0.6666666666666667 = -0.6666666666666667");
        testBinaryOperations("0 + -100 = -100");
        testBinaryOperations("0 + -1000 = -1000");
        testBinaryOperations("0 + -555000000 = -555000000");

        // with positive arguments
        testBinaryOperations("9999999999999999 + 9999999999999999 = 19999999999999998");
        testBinaryOperations("5 + 55550 = 55555");
        testBinaryOperations("0.6666666666666667 + 0.0000000000000003 = 0.666666666666667");
        testBinaryOperations("0.000005 + 0.0000000000005 = 0.0000050000005");

        // with positive and negative arguments
        testBinaryOperations("9999999999999999 + -9999999999999999 = 0");
        testBinaryOperations("1000000.5 + -50000000 = -48999999.5");
        testBinaryOperations("1000000 + -50000000 = -49000000");
        testBinaryOperations("100 + -50 = 50");
        testBinaryOperations("5 + -5 = 0");
        testBinaryOperations("0.05 + -0.05 = 0");
        testBinaryOperations("0.000005 + -0.0000000000005 = 0.0000049999995");

        // with negative and positive arguments
        testBinaryOperations("-5 + 55550 = 55545");
        testBinaryOperations("-100 + 50 = -50");
        testBinaryOperations("-2147483648 + 2147483647 = -1");

        // with both negative arguments
        testBinaryOperations("-0.6 + -0.6 = -1.2");
        testBinaryOperations("-0.000005 + -0.0000000000005 = -0.0000050000005");
        testBinaryOperations("-0.1 + -0.1 = -0.2");
        testBinaryOperations("-1000000 + -50000000 = -51000000");
        testBinaryOperations("-1000000 + -50000000.5 = -51000000.5");
        testBinaryOperations("-9999999999999999 + -9999999999999999 = -19999999999999998");
    }

    @Test
    public void testSubtractOperation() throws CalculationException {

        // with zero arguments
        testBinaryOperations("0 − 555000000 = -555000000");
        testBinaryOperations("0 − 1000 = -1000");
        testBinaryOperations("0 − 100 = -100");
        testBinaryOperations("0 − 0.6666666666666667 = -0.6666666666666667");
        testBinaryOperations("0 − 0 = 0");
        testBinaryOperations("0 − -0.6666666666666667 = 0.6666666666666667");
        testBinaryOperations("0 − -100 = 100");
        testBinaryOperations("0 − -1000 = 1000");
        testBinaryOperations("0 − -555000000 = 555000000");

        // with positive arguments
        testBinaryOperations("9999999999999999 − 9999999999999999 = 0");
        testBinaryOperations("9999999999999999 − -9999999999999999 = 19999999999999998");
        testBinaryOperations("2147483647 − 2147483647 = 0");
        testBinaryOperations("5 − 55550 = -55545");
        testBinaryOperations("0.6666666666666667 − 0.0000000000000003 = 0.6666666666666664");
        testBinaryOperations("0.000005 − 0.0000000000005 = 0.0000049999995");

        //with positive and negative arguments
        testBinaryOperations("1000000.5 − -50000000 = 51000000.5");
        testBinaryOperations("1000000 − -50000000 = 51000000");
        testBinaryOperations("100 − -50 = 150");
        testBinaryOperations("5 − -5 = 10");
        testBinaryOperations("0.05 − -0.05 = 0.1");
        testBinaryOperations("0.000005 − -0.0000000000005 = 0.0000050000005");

        // with negative and positive arguments
        testBinaryOperations("-5 − 55550 = -55555");
        testBinaryOperations("-1000000.5 − 50000000 = -51000000.5");
        testBinaryOperations("-1000000 − 50000000 = -51000000");
        testBinaryOperations("-100 − 50 = -150");
        testBinaryOperations("-5 − 5 = -10");
        testBinaryOperations("-0.05 − 0.05 = -0.1");
        testBinaryOperations("-0.000005 − 0.0000000000005 = -0.0000050000005");

        // with negative arguments
        testBinaryOperations("-0.6 − -0.6 = 0");
        testBinaryOperations("-0.000005 − -0.0000000000005 = -0.0000049999995");
        testBinaryOperations("-0.1 − -0.1 = 0");
        testBinaryOperations("-1000000 − -50000000 = 49000000");
        testBinaryOperations("-1000000 − -50000000.5 = 49000000.5");
        testBinaryOperations("-9999999999999999 − -9999999999999999 = 0");
    }

    @Test
    public void testMultiplyOperation() throws CalculationException {

        // with zero arguments
        testBinaryOperations("0 ☓ 555000000 = 0");
        testBinaryOperations("0 ☓ 1000 = 0");
        testBinaryOperations("0 ☓ 100 = 0");
        testBinaryOperations("0 ☓ 0.6666666666666667 = 0");
        testBinaryOperations("0 ☓ 0 = 0");
        testBinaryOperations("0 ☓ -0.6666666666666667 = 0");
        testBinaryOperations("0 ☓ -100 = 0");
        testBinaryOperations("0 ☓ -1000 = 0");
        testBinaryOperations("0 ☓ -555000000 = 0");

        // positive argument on positive
        testBinaryOperations("1000000.5 ☓ 50000000 = 50000025000000");
        testBinaryOperations("1000000 ☓ 50000000 = 50000000000000");
        testBinaryOperations("100 ☓ 50 = 5000");
        testBinaryOperations("5 ☓ 55550 = 277750");
        testBinaryOperations("5 ☓ 5 = 25");
        testBinaryOperations("0.6666666666666667 ☓ 0.0000000000000003 = 0.0000000000000002");
        testBinaryOperations("0.05 ☓ 0.05 = 0.0025");
        testBinaryOperations("0.000005 ☓ 0.0000000000005 = 2.5e-18");
        testBinaryOperations("0.000005 ☓ 0.0000000000005 = 2.5e-18");

        // positive argument on negative
        testBinaryOperations("1000000.5 ☓ -50000000 = -50000025000000");
        testBinaryOperations("1000000 ☓ -50000000 = -50000000000000");
        testBinaryOperations("100 ☓ -50 = -5000");
        testBinaryOperations("5 ☓ 55550 = 277750");
        testBinaryOperations("5 ☓ -5 = -25");
        testBinaryOperations("0.6666666666666667 ☓ 0.0000000000000003 = 0.0000000000000002");
        testBinaryOperations("0.05 ☓ -0.05 = -0.0025");
        testBinaryOperations("0.000005 ☓ 0.0000000000005 = 2.5e-18");
        testBinaryOperations("0.000005 ☓ -0.0000000000005 = -2.5e-18");

        // negative on positive
        testBinaryOperations("-5 ☓ 55550 = -277750");
        testBinaryOperations("-100 ☓ 50 = -5000");
        testBinaryOperations("-2147483648 ☓ 2147483647 = -4.611686016279904e+18");
        testBinaryOperations("-0.6 ☓ 0.6 = -0.36");
        testBinaryOperations("-0.000005 ☓ 0.0000000000005 = -2.5e-18");
        testBinaryOperations("-0.1 ☓ 0.1 = -0.01");
        testBinaryOperations("-1000000 ☓ 50000000 = -50000000000000");
        testBinaryOperations("-1000000 ☓ 50000000.5 = -50000000500000");

        // negative argument on negative
        testBinaryOperations("-5 ☓ -55550 = 277750");
        testBinaryOperations("-100 ☓ -50 = 5000");
        testBinaryOperations("-0.6 ☓ -0.6 = 0.36");
        testBinaryOperations("-0.000005 ☓ -0.0000000000005 = 2.5e-18");
        testBinaryOperations("-0.1 ☓ -0.1 = 0.01");
        testBinaryOperations("-1000000 ☓ -50000000 = 50000000000000");
        testBinaryOperations("-1000000 ☓ -50000000.5 = 50000000500000");

    }

    @Test
    public void testDivideOperation() throws CalculationException {

        // zero by non-zero number
        testBinaryOperations("0 ÷ 555000000 = 0");
        testBinaryOperations("0 ÷ 1000 = 0");
        testBinaryOperations("0 ÷ 100 = 0");
        testBinaryOperations("0 ÷ 0.6666666666666667 = 0");
        testBinaryOperations("0 ÷ -0.6666666666666667 = 0");
        testBinaryOperations("0 ÷ -100 = 0");
        testBinaryOperations("0 ÷ -1000 = 0");
        testBinaryOperations("0 ÷ -555000000 = 0");

        // positive number by any non-zero number
        testBinaryOperations("9999999999999999 ÷ 9999999999999999 = 1");
        testBinaryOperations("9999999999999999 ÷ -9999999999999999 = -1");
        testBinaryOperations("1000000.5 ÷ -50000000 = -0.02000001");
        testBinaryOperations("1000000 ÷ -50000000 = -0.02");
        testBinaryOperations("100 ÷ -50 = -2");
        testBinaryOperations("5 ÷ 55550 = 9.000900090009001e-5");
        testBinaryOperations("5 ÷ -5 = -1");
        testBinaryOperations("0.05 ÷ -0.05 = -1");
        testBinaryOperations("0.000005 ÷ 0.0000000000005 = 10000000");
        testBinaryOperations("0.000005 ÷ -0.0000000000005 = -10000000");

        // negative number by any non-zero
        testBinaryOperations("-0.6 ÷ -0.6 = 1");
        testBinaryOperations("-0.000005 ÷ -0.0000000000005 = 10000000");
        testBinaryOperations("-0.1 ÷ -0.1 = 1");
        testBinaryOperations("-5 ÷ 55550 = -9.000900090009001e-5");
        testBinaryOperations("-100 ÷ 50 = -2");
        testBinaryOperations("-1000000 ÷ -50000000 = 0.02");
        testBinaryOperations("-1000000 ÷ -50000000.5 = 0.0199999998");
        testBinaryOperations("-2147483648 ÷ 2147483647 = -1.000000000465661");
        testBinaryOperations("-9999999999999999 ÷ -9999999999999999 = 1");
    }

    @Test
    public void testPercentOperation() throws CalculationException {

        // percentage for base zero and any percent
        testBinaryOperations("0 % 555000000 = 0");
        testBinaryOperations("0 % 1000 = 0");
        testBinaryOperations("0 % 100 = 0");
        testBinaryOperations("0 % 0.6666666666666667 = 0");
        testBinaryOperations("0 % 0 = 0");
        testBinaryOperations("0 % -0.6666666666666667 = 0");
        testBinaryOperations("0 % -100 = 0");
        testBinaryOperations("0 % -1000 = 0");
        testBinaryOperations("0 % -555000000 = 0");

        // percentage for positive base
        testBinaryOperations("1000000.5 % -50000000 = -500000250000");
        testBinaryOperations("1000000 % -50000000 = -500000000000");
        testBinaryOperations("100 % -50 = -50");
        testBinaryOperations("5 % 55550 = 2777.5");
        testBinaryOperations("5 % -5 = -0.25");
        testBinaryOperations("0.6666666666666667 % 0.0000000000000003 = 2.e-18");
        testBinaryOperations("0.6666666666666667 % 0 = 0");
        testBinaryOperations("0.05 % -0.05 = -0.000025");
        testBinaryOperations("0.000005 % 0.0000000000005 = 2.5e-20");
        testBinaryOperations("0.000005 % -0.0000000000005 = -2.5e-20");

        // percentage for negative base
        testBinaryOperations("-0.6 % -0.6 = 0.0036");
        testBinaryOperations("-0.000005 % -0.0000000000005 = 2.5e-20");
        testBinaryOperations("-0.1 % -0.1 = 0.0001");
        testBinaryOperations("-5 % 55550 = -2777.5");
        testBinaryOperations("-5 % 0 = 0");
        testBinaryOperations("-100 % 50 = -50");
        testBinaryOperations("-1000000 % -50000000 = 500000000000");
        testBinaryOperations("-1000000 % -50000000.5 = 500000005000");
    }

    // TODO add separated method for percentage

    private void testBinaryOperations(String expression) throws CalculationException {
        // Expression string has format: firstNumber operation secondNumber = expectedResult
        // To extract these parts need to split expression by white spaces
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int index = 0;

        // Extract first number, operation and second number from expression
        BigDecimal firstNumber = new BigDecimal(expressionParts[index++]);
        MathOperation operation = MathOperation.getOperation(expressionParts[index++]);
        BigDecimal secondNumber = new BigDecimal(expressionParts[index++]);

        // Calculate and verify result
        BigDecimal expectedNumber = new BigDecimal(expressionParts[index]);
        scale = expectedNumber.scale();
        Calculator calculator = new StandardCalculator(operation, firstNumber, secondNumber);
        BigDecimal calculationResult = calculator.calculate().setScale(scale, ROUND_HALF_UP);
        assertEquals(expectedNumber, calculationResult);
    }

    @Test
    public void testNegateOperation() throws CalculationException {
        // Expression format: enteredNumber = expected result after unary operation
        // with positive argument
        testUnaryOperations(NEGATE, "9999999999999999 = -9999999999999999");
        testUnaryOperations(NEGATE, "1000000.5 = -1000000.5");
        testUnaryOperations(NEGATE, "1000 = -1000");
        testUnaryOperations(NEGATE, "100 = -100");
        testUnaryOperations(NEGATE, "0.6666666666666667 = -0.6666666666666667");
        testUnaryOperations(NEGATE, "0.05 = -0.05");
        testUnaryOperations(NEGATE, "0.0000000000005 = -0.0000000000005");

        // with zero
        testUnaryOperations(NEGATE, "0 = 0");

        // with negative argument
        testUnaryOperations(NEGATE, "-0.0000000000005 = 0.0000000000005");
        testUnaryOperations(NEGATE, "-0.05 = 0.05");
        testUnaryOperations(NEGATE, "-0.6666666666666667 = 0.6666666666666667");
        testUnaryOperations(NEGATE, "-100 = 100");
        testUnaryOperations(NEGATE, "-1000 = 1000");
        testUnaryOperations(NEGATE, "-555000000 = 555000000");
        testUnaryOperations(NEGATE, "-9999999999999999 = 9999999999999999");
    }

    @Test
    public void testSquareOperation() throws CalculationException {
        // Expression format: enteredNumber = expected result after unary operation
        // with positive argument
        testUnaryOperations(SQUARE, "1000000.5 = 1000001000000.25");
        testUnaryOperations(SQUARE, "1000 = 1000000");
        testUnaryOperations(SQUARE, "100 = 10000");
        testUnaryOperations(SQUARE, "0.6666666666666667 = 0.4444444444444445");
        testUnaryOperations(SQUARE, "0.05 = 0.0025");
        testUnaryOperations(SQUARE, "0.0000000000005 = 2.5e-25");

        // with zero
        testUnaryOperations(SQUARE, "0 = 0");

        // with negative argument
        testUnaryOperations(SQUARE, "-0.0000000000005 = 2.5e-25");
        testUnaryOperations(SQUARE, "-0.05 = 0.0025");
        testUnaryOperations(SQUARE, "-0.6666666666666667 = 0.4444444444444445");
        testUnaryOperations(SQUARE, "-100 = 10000");
        testUnaryOperations(SQUARE, "-1000 = 1000000");
        testUnaryOperations(SQUARE, "-555000000 = 3.08025e+17");
    }

    @Test
    public void testSquareRootOperation() throws CalculationException {
        // Expression format: enteredNumber = expected result after unary operation
        testUnaryOperations(SQUARE_ROOT, "9999999999999999 = 99999999.999999995");
        testUnaryOperations(SQUARE_ROOT, "58585858585 = 242045.158152358");
        testUnaryOperations(SQUARE_ROOT, "1000000.5 = 1000.00024999996875");
        testUnaryOperations(SQUARE_ROOT, "1000 = 31.62277660168379");
        testUnaryOperations(SQUARE_ROOT, "100 = 10");
        testUnaryOperations(SQUARE_ROOT, "0.6666666666666667 = 0.8164965809277261");
        testUnaryOperations(SQUARE_ROOT, "0.05 = 0.223606797749979");
        testUnaryOperations(SQUARE_ROOT, "0.0000000000005 = 7.071067811865475e-7");
        testUnaryOperations(SQUARE_ROOT, "0 = 0");
    }

    @Test
    public void testReverseOperation() throws CalculationException {
        // Expression format: enteredNumber = expected result after unary operation
        // positive argument
        testUnaryOperations(REVERSE, "9999999999999999 = 0.0000000000000001");
        testUnaryOperations(REVERSE, "555000000 = 1.801801801801802e-9");
        testUnaryOperations(REVERSE, "1000 = 0.001");
        testUnaryOperations(REVERSE, "100 = 0.01");
        testUnaryOperations(REVERSE, "0.6666666666666667 = 1.499999999999999925");
        testUnaryOperations(REVERSE, "0.05 = 20");

        // negative argument
        testUnaryOperations(REVERSE, "-0.05 = -20");
        testUnaryOperations(REVERSE, "-0.6666666666666667 = -1.499999999999999925");
        testUnaryOperations(REVERSE, "-100 = -0.01");
        testUnaryOperations(REVERSE, "-1000 = -0.001");
        testUnaryOperations(REVERSE, "-555000000 = -1.801801801801802e-9");
        testUnaryOperations(REVERSE, "-9999999999999999 = -0.0000000000000001");
    }


    private void testUnaryOperations(MathOperation operation, String expression) throws CalculationException {
        // Expression string has format "baseForOperation = expectedResult"
        // To extract these parts need to split expression by white spaces
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int index = 0;

        // Extract base and expected result from expression
        BigDecimal base = new BigDecimal(expressionParts[index++]);
        BigDecimal expectedNumber = new BigDecimal(expressionParts[index]);
        scale = expectedNumber.scale();

        // Calculate and verify result
        Calculator calculator = new StandardCalculator(operation, base);
        BigDecimal calculationResult = calculator.calculate().setScale(scale, ROUND_HALF_UP);
        assertEquals(expectedNumber, calculationResult);
    }

    @Test
    public void testOperationWithWrongArguments() {

        // division by zero
        testOperationForException(DIVIDE, "555000000", "0");
        testOperationForException(DIVIDE, "1000", "0");
        testOperationForException(DIVIDE, "100", "0");
        testOperationForException(DIVIDE, "0.6666666666666667", "0");
        testOperationForException(DIVIDE, "0", "0");
        testOperationForException(DIVIDE, "0", "0.0");
        testOperationForException(DIVIDE, "-0.6666666666666667", "0");
        testOperationForException(DIVIDE, "-100", "0");
        testOperationForException(DIVIDE, "-1000", "0");
        testOperationForException(DIVIDE, "-555000000", "0");

        // square root with negative argument
        testOperationForException(SQUARE_ROOT, "-1");
        testOperationForException(SQUARE_ROOT, "-5");
        testOperationForException(SQUARE_ROOT, "-5.5");
        testOperationForException(SQUARE_ROOT, "-10000000");
        testOperationForException(SQUARE_ROOT, String.valueOf(Integer.MIN_VALUE));
        testOperationForException(SQUARE_ROOT, "-555555555000005");
        testOperationForException(SQUARE_ROOT, "-9999999999999999");
        testOperationForException(SQUARE_ROOT, String.valueOf(Long.MIN_VALUE));

        // reverse with zero base
        testOperationForException(REVERSE, "0");
        testOperationForException(REVERSE, "0.0");

        // result is overflow
        testOperationForException(DIVIDE, "1.e-9999", "10");
        testOperationForException(DIVIDE, "1.e-9999", "100");
        testOperationForException(DIVIDE, "1.e-9999", "1000000");
        testOperationForException(MULTIPLY, "1.e+9999", "10");
        testOperationForException(MULTIPLY, "1.e+9999", "100");
        testOperationForException(MULTIPLY, "1.e+9999", "1000000");
    }

    private void testOperationForException(MathOperation operation, String... numbers) {
        try {
            new StandardCalculator(operation, getBigDecimalValues(numbers)).calculate();

            Assert.fail(format("Expected CalculationException caused by wrong operation argument. Your operation is %s, arguments: %s",
                    operation.toString(), Arrays.toString(numbers)));
        } catch (CalculationException e) {
            // expected
        }
    }

    @Test
    public void testInitWithWrongArgumentCount() {
        testInitForException(null);
        testInitForException(null, new BigDecimal[1]);

        // binary operation with wrong argument count
        testInitForException(ADD);
        testInitForException(ADD, new BigDecimal[1]);
        testInitForException(ADD, new BigDecimal[3]);
        testInitForException(ADD, new BigDecimal[4]);
        testInitForException(ADD, new BigDecimal[10]);

        // unary operation with wrong argument count
        testInitForException(SQUARE_ROOT);
        testInitForException(SQUARE_ROOT, new BigDecimal[2]);
        testInitForException(SQUARE_ROOT, new BigDecimal[3]);
        testInitForException(SQUARE_ROOT, new BigDecimal[4]);
        testInitForException(SQUARE_ROOT, new BigDecimal[10]);
    }

    private void testInitForException(MathOperation operation, BigDecimal... number) {
        try {
            new StandardCalculator(operation, number);
            Assert.fail(format("Expected CalculationException with wrong arguments. Your operation is %s, count %s",
                    operation, (number == null) ? "null" : number.length));
        } catch (CalculationException e) {
            // expected
        }
    }
}