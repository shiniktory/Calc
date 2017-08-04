package com.implemica.CalculatorProject.calculation;

import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.Test;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.util.ValueTransformerUtil.getBigDecimalValues;
import static java.lang.String.format;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StandardCalculatorTest {

    private static final String ARGUMENT_DELIMITERS = "\\s+[=\\s]*";

    @Test
    public void testAddOperation() throws CalculationException {

        // with zero arguments
        testCalculations("0 + 555000000 = 555000000");
        testCalculations("0 + 1000 = 1000");
        testCalculations("0 + 100 = 100");
        testCalculations("0 + 0.6666666666666667 = 0.6666666666666667");
        testCalculations("0 + 0 = 0");
        testCalculations("0 + -0.6666666666666667 = -0.6666666666666667");
        testCalculations("0 + -100 = -100");
        testCalculations("0 + -1000 = -1000");
        testCalculations("0 + -555000000 = -555000000");

        // with positive arguments
        testCalculations("9999999999999999 + 9999999999999999 = 19999999999999998");
        testCalculations("5 + 55550 = 55555");
        testCalculations("0.6666666666666667 + 0.0000000000000003 = 0.666666666666667");
        testCalculations("0.000005 + 0.0000000000005 = 0.0000050000005");

        // with positive and negative arguments
        testCalculations("9999999999999999 + -9999999999999999 = 0");
        testCalculations("1000000.5 + -50000000 = -48999999.5");
        testCalculations("1000000 + -50000000 = -49000000");
        testCalculations("100 + -50 = 50");
        testCalculations("5 + -5 = 0");
        testCalculations("0.05 + -0.05 = 0");
        testCalculations("0.000005 + -0.0000000000005 = 0.0000049999995");

        // with negative and positive arguments
        testCalculations("-5 + 55550 = 55545");
        testCalculations("-100 + 50 = -50");
        testCalculations("-2147483648 + 2147483647 = -1");

        // with both negative arguments
        testCalculations("-0.6 + -0.6 = -1.2");
        testCalculations("-0.000005 + -0.0000000000005 = -0.0000050000005");
        testCalculations("-0.1 + -0.1 = -0.2");
        testCalculations("-1000000 + -50000000 = -51000000");
        testCalculations("-1000000 + -50000000.5 = -51000000.5");
        testCalculations("-9999999999999999 + -9999999999999999 = -19999999999999998");
    }

    @Test
    public void testSubtractOperation() throws CalculationException {

        // with zero arguments
        testCalculations("0 − 555000000 = -555000000");
        testCalculations("0 − 1000 = -1000");
        testCalculations("0 − 100 = -100");
        testCalculations("0 − 0.6666666666666667 = -0.6666666666666667");
        testCalculations("0 − 0 = 0");
        testCalculations("0 − -0.6666666666666667 = 0.6666666666666667");
        testCalculations("0 − -100 = 100");
        testCalculations("0 − -1000 = 1000");
        testCalculations("0 − -555000000 = 555000000");

        // with positive arguments
        testCalculations("9999999999999999 − 9999999999999999 = 0");
        testCalculations("9999999999999999 − -9999999999999999 = 19999999999999998");
        testCalculations("2147483647 − 2147483647 = 0");
        testCalculations("5 − 55550 = -55545");
        testCalculations("0.6666666666666667 − 0.0000000000000003 = 0.6666666666666664");
        testCalculations("0.000005 − 0.0000000000005 = 0.0000049999995");

        //with positive and negative arguments
        testCalculations("1000000.5 − -50000000 = 51000000.5");
        testCalculations("1000000 − -50000000 = 51000000");
        testCalculations("100 − -50 = 150");
        testCalculations("5 − -5 = 10");
        testCalculations("0.05 − -0.05 = 0.1");
        testCalculations("0.000005 − -0.0000000000005 = 0.0000050000005");

        // with negative and positive arguments
        testCalculations("-5 − 55550 = -55555");
        testCalculations("-1000000.5 − 50000000 = -51000000.5");
        testCalculations("-1000000 − 50000000 = -51000000");
        testCalculations("-100 − 50 = -150");
        testCalculations("-5 − 5 = -10");
        testCalculations("-0.05 − 0.05 = -0.1");
        testCalculations("-0.000005 − 0.0000000000005 = -0.0000050000005");

        // with negative arguments
        testCalculations("-0.6 − -0.6 = 0");
        testCalculations("-0.000005 − -0.0000000000005 = -0.0000049999995");
        testCalculations("-0.1 − -0.1 = 0");
        testCalculations("-1000000 − -50000000 = 49000000");
        testCalculations("-1000000 − -50000000.5 = 49000000.5");
        testCalculations("-9999999999999999 − -9999999999999999 = 0");
    }

    @Test
    public void testMultiplyOperation() throws CalculationException {

        // with zero arguments
        testCalculations("0 ☓ 555000000 = 0");
        testCalculations("0 ☓ 1000 = 0");
        testCalculations("0 ☓ 100 = 0");
        testCalculations("0 ☓ 0.6666666666666667 = 0");
        testCalculations("0 ☓ 0 = 0");
        testCalculations("0 ☓ -0.6666666666666667 = 0");
        testCalculations("0 ☓ -100 = 0");
        testCalculations("0 ☓ -1000 = 0");
        testCalculations("0 ☓ -555000000 = 0");

        // positive argument on positive
        testCalculations("1000000.5 ☓ 50000000 = 50000025000000");
        testCalculations("1000000 ☓ 50000000 = 50000000000000");
        testCalculations("100 ☓ 50 = 5000");
        testCalculations("5 ☓ 55550 = 277750");
        testCalculations("5 ☓ 5 = 25");
        testCalculations("0.6666666666666667 ☓ 0.0000000000000003 = 0.0000000000000002");
        testCalculations("0.05 ☓ 0.05 = 0.0025");
        testCalculations("0.000005 ☓ 0.0000000000005 = 2.5e-18");
        testCalculations("0.000005 ☓ 0.0000000000005 = 2.5e-18");

        // positive argument on negative
        testCalculations("1000000.5 ☓ -50000000 = -50000025000000");
        testCalculations("1000000 ☓ -50000000 = -50000000000000");
        testCalculations("100 ☓ -50 = -5000");
        testCalculations("5 ☓ 55550 = 277750");
        testCalculations("5 ☓ -5 = -25");
        testCalculations("0.6666666666666667 ☓ 0.0000000000000003 = 0.0000000000000002");
        testCalculations("0.05 ☓ -0.05 = -0.0025");
        testCalculations("0.000005 ☓ 0.0000000000005 = 2.5e-18");
        testCalculations("0.000005 ☓ -0.0000000000005 = -2.5e-18");

        // negative on positive
        testCalculations("-5 ☓ 55550 = -277750");
        testCalculations("-100 ☓ 50 = -5000");
        testCalculations("-2147483648 ☓ 2147483647 = -4.611686016279904e+18");
        testCalculations("-0.6 ☓ 0.6 = -0.36");
        testCalculations("-0.000005 ☓ 0.0000000000005 = -2.5e-18");
        testCalculations("-0.1 ☓ 0.1 = -0.01");
        testCalculations("-1000000 ☓ 50000000 = -50000000000000");
        testCalculations("-1000000 ☓ 50000000.5 = -50000000500000");

        // negative argument on negative
        testCalculations("-5 ☓ -55550 = 277750");
        testCalculations("-100 ☓ -50 = 5000");
        testCalculations("-0.6 ☓ -0.6 = 0.36");
        testCalculations("-0.000005 ☓ -0.0000000000005 = 2.5e-18");
        testCalculations("-0.1 ☓ -0.1 = 0.01");
        testCalculations("-1000000 ☓ -50000000 = 50000000000000");
        testCalculations("-1000000 ☓ -50000000.5 = 50000000500000");

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
        testCalculations("0.000005 ÷ 0.0000000000005 = 10000000");
        testCalculations("0.000005 ÷ -0.0000000000005 = -10000000");

        // negative number by any non-zero
        testCalculations("-0.6 ÷ -0.6 = 1");
        testCalculations("-0.000005 ÷ -0.0000000000005 = 10000000");
        testCalculations("-0.1 ÷ -0.1 = 1");
        testCalculations("-5 ÷ 55550 = -9.000900090009001e-5");
        testCalculations("-100 ÷ 50 = -2");
        testCalculations("-1000000 ÷ -50000000 = 0.02");
        testCalculations("-1000000 ÷ -50000000.5 = 0.0199999998");
        testCalculations("-2147483648 ÷ 2147483647 = -1.000000000465661");
        testCalculations("-9999999999999999 ÷ -9999999999999999 = 1");
    }

    @Test
    public void testPercentOperation() throws CalculationException {
        // Expression format: base % percentCount = expected result
        // percentage for base zero and any percent
        testCalculations("0 % 555000000 = 0");
        testCalculations("0 % 1000 = 0");
        testCalculations("0 % 100 = 0");
        testCalculations("0 % 0.6666666666666667 = 0");
        testCalculations("0 % 0 = 0");
        testCalculations("0 % -0.6666666666666667 = 0");
        testCalculations("0 % -100 = 0");
        testCalculations("0 % -1000 = 0");
        testCalculations("0 % -555000000 = 0");

        // percentage for positive base
        testCalculations("1000000.5 % -50000000 = -500000250000");
        testCalculations("1000000 % -50000000 = -500000000000");
        testCalculations("100 % -50 = -50");
        testCalculations("5 % 55550 = 2777.5");
        testCalculations("5 % -5 = -0.25");
        testCalculations("0.6666666666666667 % 0.0000000000000003 = 2.e-18");
        testCalculations("0.6666666666666667 % 0 = 0");
        testCalculations("0.05 % -0.05 = -0.000025");
        testCalculations("0.000005 % 0.0000000000005 = 2.5e-20");
        testCalculations("0.000005 % -0.0000000000005 = -2.5e-20");

        // percentage for negative base
        testCalculations("-0.6 % -0.6 = 0.0036");
        testCalculations("-0.000005 % -0.0000000000005 = 2.5e-20");
        testCalculations("-0.1 % -0.1 = 0.0001");
        testCalculations("-5 % 55550 = -2777.5");
        testCalculations("-5 % 0 = 0");
        testCalculations("-100 % 50 = -50");
        testCalculations("-1000000 % -50000000 = 500000000000");
        testCalculations("-1000000 % -50000000.5 = 500000005000");
    }

    @Test
    public void testNegateOperation() throws CalculationException {
        // Expression format: unaryOperation enteredNumber = expected result after unary operation
        // with positive argument
        testCalculations("± 9999999999999999 = -9999999999999999");
        testCalculations("± 1000000.5 = -1000000.5");
        testCalculations("± 1000 = -1000");
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
        testCalculations("± -1000 = 1000");
        testCalculations("± -555000000 = 555000000");
        testCalculations("± -9999999999999999 = 9999999999999999");
    }

    @Test
    public void testSquareOperation() throws CalculationException {
        // Expression format: enteredNumber = expected result after unary operation
        // with positive argument
        testCalculations("sqr 1000000.5 = 1000001000000.25");
        testCalculations("sqr 1000 = 1000000");
        testCalculations("sqr 100 = 10000");
        testCalculations("sqr 0.6666666666666667 = 0.4444444444444445");
        testCalculations("sqr 0.05 = 0.0025");
        testCalculations("sqr 0.0000000000005 = 2.5e-25");

        // with zero
        testCalculations("sqr 0 = 0");

        // with negative argument
        testCalculations("sqr -0.0000000000005 = 2.5e-25");
        testCalculations("sqr -0.05 = 0.0025");
        testCalculations("sqr -0.6666666666666667 = 0.4444444444444445");
        testCalculations("sqr -100 = 10000");
        testCalculations("sqr -1000 = 1000000");
        testCalculations("sqr -555000000 = 3.08025e+17");
    }

    @Test
    public void testSquareRootOperation() throws CalculationException {
        // Expression format: enteredNumber = expected result after unary operation
        testCalculations("√ 9999999999999999 = 99999999.999999995");
        testCalculations("√ 58585858585 = 242045.158152358");
        testCalculations("√ 1000000.5 = 1000.00024999996875");
        testCalculations("√ 1000 = 31.62277660168379");
        testCalculations("√ 100 = 10");
        testCalculations("√ 0.6666666666666667 = 0.8164965809277261");
        testCalculations("√ 0.05 = 0.223606797749979");
        testCalculations("√ 0.0000000000005 = 7.071067811865475e-7");
        testCalculations("√ 0 = 0");
    }

    @Test
    public void testReverseOperation() throws CalculationException {
        // Expression format: enteredNumber = expected result after unary operation
        // positive argument
        testCalculations("1/ 9999999999999999 = 0.0000000000000001");
        testCalculations("1/ 555000000 = 1.801801801801802e-9");
        testCalculations("1/ 1000 = 0.001");
        testCalculations("1/ 100 = 0.01");
        testCalculations("1/ 0.6666666666666667 = 1.499999999999999925");
        testCalculations("1/ 0.05 = 20");

        // negative argument
        testCalculations("1/ -0.05 = -20");
        testCalculations("1/ -0.6666666666666667 = -1.499999999999999925");
        testCalculations("1/ -100 = -0.01");
        testCalculations("1/ -1000 = -0.001");
        testCalculations("1/ -555000000 = -1.801801801801802e-9");
        testCalculations("1/ -9999999999999999 = -0.0000000000000001");
    }

    private void testCalculations(String expression) throws CalculationException {
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        String[] numbers;
        MathOperation operation;

        if (expressionParts.length == 3) { // Expression for unary operations has format: operation baseNumber = expectedResult
            operation = extractOperation(expressionParts[0]);
            numbers = new String[]{expressionParts[1]};
        } else { // Expression for binary operations has format: firstNumber operation secondNumber = expectedResult
            operation = extractOperation(expressionParts[1]);
            numbers = new String[]{expressionParts[0], expressionParts[2]};
        }

        // Last expression part is expected result
        BigDecimal expectedNumber = new BigDecimal(expressionParts[expressionParts.length - 1]);
        int scale = expectedNumber.scale();
        BigDecimal calculationResult = getResult(operation, scale, numbers);

        assertEquals(expectedNumber, calculationResult);
    }

    private MathOperation extractOperation(String operation) {
        switch (operation) {
            case "1/":
                return REVERSE;
            case "sqr":
                return SQUARE;
            default:
                return MathOperation.getOperation(operation);
        }
    }

    private BigDecimal getResult(MathOperation operation, int scale, String... numbers) throws CalculationException {
        Calculator calculator = new StandardCalculator(operation, getBigDecimalValues(numbers));
        return calculator.calculate().setScale(scale, ROUND_HALF_UP);
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
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        MathOperation operation;
        String[] numbers;

        try {
            if (expressionParts.length == 2) { // Expression for unary operations has format: operation baseNumber
                operation = extractOperation(expressionParts[0]);
                numbers = new String[]{expressionParts[1]};

            } else { // Expression for binary operations has format: firstNumber operation secondNumber
                operation = extractOperation(expressionParts[1]);
                numbers = new String[]{expressionParts[0], expressionParts[2]};
            }

            getResult(operation, 0, numbers); // scale is ignored, because exception occur before got result
            fail(format("Expected CalculationException caused by wrong operation argument. Your expression: %s",
                    expression));
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
            fail(format("Expected CalculationException with wrong arguments. Your operation is %s, count %s",
                    operation, (number == null) ? "null" : number.length));
        } catch (CalculationException e) {
            // expected
        }
    }
}