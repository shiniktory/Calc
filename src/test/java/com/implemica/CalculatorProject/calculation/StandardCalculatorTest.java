package com.implemica.CalculatorProject.calculation;

import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static java.lang.String.format;

public class StandardCalculatorTest {

    private static int scale;

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

    @Test
    public void testAddOperation() throws CalculationException {

        // with zero arguments
        testBinaryOperations("555000000", "0", ADD, "555000000");
        testBinaryOperations("1000", "0", ADD, "1000");
        testBinaryOperations("100", "0", ADD, "100");
        testBinaryOperations("0.6666666666666667", "0", ADD, "0.6666666666666667");
        testBinaryOperations("0", "0", ADD, "0");
        testBinaryOperations("-0.6666666666666667", "0", ADD, "-0.6666666666666667");
        testBinaryOperations("-100", "0", ADD, "-100");
        testBinaryOperations("-1000", "0", ADD, "-1000");
        testBinaryOperations("-555000000", "0", ADD, "-555000000");

        // with positive arguments
        testBinaryOperations("19999999999999998", "9999999999999999", ADD, "9999999999999999");
        testBinaryOperations("55555", "5", ADD, "55550");
        testBinaryOperations("0.666666666666667", "0.6666666666666667", ADD, "0.0000000000000003");
        testBinaryOperations("0.0000050000005", "0.000005", ADD, "0.0000000000005");

        // with positive and negative arguments
        testBinaryOperations("0", "9999999999999999", ADD, "-9999999999999999");
        testBinaryOperations("-48999999.5", "1000000.5", ADD, "-50000000");
        testBinaryOperations("-49000000", "1000000", ADD, "-50000000");
        testBinaryOperations("50", "100", ADD, "-50");
        testBinaryOperations("0", "5", ADD, "-5");
        testBinaryOperations("0", "0.05", ADD, "-0.05");
        testBinaryOperations("0.0000049999995", "0.000005", ADD, "-0.0000000000005");

        // with negative and positive arguments
        testBinaryOperations("55545", "-5", ADD, "55550");
        testBinaryOperations("-50", "-100", ADD, "50");
        testBinaryOperations("-1", String.valueOf(Integer.MIN_VALUE), ADD, String.valueOf(Integer.MAX_VALUE));

        // with both negative arguments
        testBinaryOperations("-1.2", "-0.6", ADD, "-0.6");
        testBinaryOperations("-0.0000050000005", "-0.000005", ADD, "-0.0000000000005");
        testBinaryOperations("-0.2", "-0.1", ADD, "-0.1");
        testBinaryOperations("-51000000", "-1000000", ADD, "-50000000");
        testBinaryOperations("-51000000.5", "-1000000", ADD, "-50000000.5");
        testBinaryOperations("-19999999999999998", "-9999999999999999", ADD, "-9999999999999999");
    }

    @Test
    public void testSubtractOperation() throws CalculationException {

        // with zero arguments
        testBinaryOperations("-555000000", "0", SUBTRACT, "555000000");
        testBinaryOperations("-1000", "0", SUBTRACT, "1000");
        testBinaryOperations("-100", "0", SUBTRACT, "100");
        testBinaryOperations("-0.6666666666666667", "0", SUBTRACT, "0.6666666666666667");
        testBinaryOperations("0", "0", SUBTRACT, "0");
        testBinaryOperations("0.6666666666666667", "0", SUBTRACT, "-0.6666666666666667");
        testBinaryOperations("100", "0", SUBTRACT, "-100");
        testBinaryOperations("1000", "0", SUBTRACT, "-1000");
        testBinaryOperations("555000000", "0", SUBTRACT, "-555000000");

        // with positive arguments
        testBinaryOperations("0", "9999999999999999", SUBTRACT, "9999999999999999");
        testBinaryOperations("19999999999999998", "9999999999999999", SUBTRACT, "-9999999999999999");
        testBinaryOperations("0", String.valueOf(Integer.MAX_VALUE), SUBTRACT, String.valueOf(Integer.MAX_VALUE));
        testBinaryOperations("-55545", "5", SUBTRACT, "55550");
        testBinaryOperations("0.6666666666666664", "0.6666666666666667", SUBTRACT, "0.0000000000000003");
        testBinaryOperations("0.0000049999995", "0.000005", SUBTRACT, "0.0000000000005");

        //with positive and negative arguments
        testBinaryOperations("51000000.5", "1000000.5", SUBTRACT, "-50000000");
        testBinaryOperations("51000000", "1000000", SUBTRACT, "-50000000");
        testBinaryOperations("150", "100", SUBTRACT, "-50");
        testBinaryOperations("10", "5", SUBTRACT, "-5");
        testBinaryOperations("0.1", "0.05", SUBTRACT, "-0.05");
        testBinaryOperations("0.0000050000005", "0.000005", SUBTRACT, "-0.0000000000005");

        // with negative and positive arguments
        testBinaryOperations("-55555", "-5", SUBTRACT, "55550");
        testBinaryOperations("-51000000.5", "-1000000.5", SUBTRACT, "50000000");
        testBinaryOperations("-51000000", "-1000000", SUBTRACT, "50000000");
        testBinaryOperations("-150", "-100", SUBTRACT, "50");
        testBinaryOperations("-10", "-5", SUBTRACT, "5");
        testBinaryOperations("-0.1", "-0.05", SUBTRACT, "0.05");
        testBinaryOperations("-0.0000050000005", "-0.000005", SUBTRACT, "0.0000000000005");

        // with negative arguments
        testBinaryOperations("0", "-0.6", SUBTRACT, "-0.6");
        testBinaryOperations("-0.0000049999995", "-0.000005", SUBTRACT, "-0.0000000000005");
        testBinaryOperations("0", "-0.1", SUBTRACT, "-0.1");
        testBinaryOperations("49000000", "-1000000", SUBTRACT, "-50000000");
        testBinaryOperations("49000000.5", "-1000000", SUBTRACT, "-50000000.5");
        testBinaryOperations("0", "-9999999999999999", SUBTRACT, "-9999999999999999");
    }

    @Test
    public void testMultiplyOperation() throws CalculationException {

        // with zero arguments
        testBinaryOperations("0", "0", MULTIPLY, "555000000");
        testBinaryOperations("0", "0", MULTIPLY, "1000");
        testBinaryOperations("0", "0", MULTIPLY, "100");
        testBinaryOperations("0", "0", MULTIPLY, "0.6666666666666667");
        testBinaryOperations("0", "0", MULTIPLY, "0");
        testBinaryOperations("0", "0", MULTIPLY, "-0.6666666666666667");
        testBinaryOperations("0", "0", MULTIPLY, "-100");
        testBinaryOperations("0", "0", MULTIPLY, "-1000");
        testBinaryOperations("0", "0", MULTIPLY, "-555000000");

        // positive argument on positive
        testBinaryOperations("50000025000000", "1000000.5", MULTIPLY, "50000000");
        testBinaryOperations("50000000000000", "1000000", MULTIPLY, "50000000");
        testBinaryOperations("5000", "100", MULTIPLY, "50");
        testBinaryOperations("277750", "5", MULTIPLY, "55550");
        testBinaryOperations("25", "5", MULTIPLY, "5");
        testBinaryOperations("0.0000000000000002", "0.6666666666666667", MULTIPLY, "0.0000000000000003");
        testBinaryOperations("0.0025", "0.05", MULTIPLY, "0.05");
        testBinaryOperations("2.5e-18", "0.000005", MULTIPLY, "0.0000000000005");
        testBinaryOperations("2.5e-18", "0.000005", MULTIPLY, "0.0000000000005");

        // positive argument on negative
        testBinaryOperations("-50000025000000", "1000000.5", MULTIPLY, "-50000000");
        testBinaryOperations("-50000000000000", "1000000", MULTIPLY, "-50000000");
        testBinaryOperations("-5000", "100", MULTIPLY, "-50");
        testBinaryOperations("277750", "5", MULTIPLY, "55550");
        testBinaryOperations("-25", "5", MULTIPLY, "-5");
        testBinaryOperations("0.0000000000000002", "0.6666666666666667", MULTIPLY, "0.0000000000000003");
        testBinaryOperations("-0.0025", "0.05", MULTIPLY, "-0.05");
        testBinaryOperations("2.5e-18", "0.000005", MULTIPLY, "0.0000000000005");
        testBinaryOperations("-2.5e-18", "0.000005", MULTIPLY, "-0.0000000000005");

        // negative on positive
        testBinaryOperations("-277750", "-5", MULTIPLY, "55550");
        testBinaryOperations("-5000", "-100", MULTIPLY, "50");
        testBinaryOperations("-4.611686016279904e+18", String.valueOf(Integer.MIN_VALUE), MULTIPLY, String.valueOf(Integer.MAX_VALUE));
        testBinaryOperations("-0.36", "-0.6", MULTIPLY, "0.6");
        testBinaryOperations("-2.5e-18", "-0.000005", MULTIPLY, "0.0000000000005");
        testBinaryOperations("-0.01", "-0.1", MULTIPLY, "0.1");
        testBinaryOperations("-50000000000000", "-1000000", MULTIPLY, "50000000");
        testBinaryOperations("-50000000500000", "-1000000", MULTIPLY, "50000000.5");

        // negative argument on negative
        testBinaryOperations("277750", "-5", MULTIPLY, "-55550");
        testBinaryOperations("5000", "-100", MULTIPLY, "-50");
        testBinaryOperations("0.36", "-0.6", MULTIPLY, "-0.6");
        testBinaryOperations("2.5e-18", "-0.000005", MULTIPLY, "-0.0000000000005");
        testBinaryOperations("0.01", "-0.1", MULTIPLY, "-0.1");
        testBinaryOperations("50000000000000", "-1000000", MULTIPLY, "-50000000");
        testBinaryOperations("50000000500000", "-1000000", MULTIPLY, "-50000000.5");

    }

    @Test
    public void testDivideOperation() throws CalculationException {

        // zero by non-zero number
        testBinaryOperations("0", "0", DIVIDE, "555000000");
        testBinaryOperations("0", "0", DIVIDE, "1000");
        testBinaryOperations("0", "0", DIVIDE, "100");
        testBinaryOperations("0", "0", DIVIDE, "0.6666666666666667");
        testBinaryOperations("0", "0", DIVIDE, "-0.6666666666666667");
        testBinaryOperations("0", "0", DIVIDE, "-100");
        testBinaryOperations("0", "0", DIVIDE, "-1000");
        testBinaryOperations("0", "0", DIVIDE, "-555000000");

        // positive number by any non-zero number
        testBinaryOperations("1", "9999999999999999", DIVIDE, "9999999999999999");
        testBinaryOperations("-1", "9999999999999999", DIVIDE, "-9999999999999999");
        testBinaryOperations("-0.02000001", "1000000.5", DIVIDE, "-50000000");
        testBinaryOperations("-0.02", "1000000", DIVIDE, "-50000000");
        testBinaryOperations("-2", "100", DIVIDE, "-50");
        testBinaryOperations("9.000900090009001e-5", "5", DIVIDE, "55550");
        testBinaryOperations("-1", "5", DIVIDE, "-5");
        testBinaryOperations("-1", "0.05", DIVIDE, "-0.05");
        testBinaryOperations("10000000", "0.000005", DIVIDE, "0.0000000000005");
        testBinaryOperations("-10000000", "0.000005", DIVIDE, "-0.0000000000005");

        // negative number by any non-zero
        testBinaryOperations("1", "-0.6", DIVIDE, "-0.6");
        testBinaryOperations("10000000", "-0.000005", DIVIDE, "-0.0000000000005");
        testBinaryOperations("1", "-0.1", DIVIDE, "-0.1");
        testBinaryOperations("-9.000900090009001e-5", "-5", DIVIDE, "55550");
        testBinaryOperations("-2", "-100", DIVIDE, "50");
        testBinaryOperations("0.02", "-1000000", DIVIDE, "-50000000");
        testBinaryOperations("0.0199999998", "-1000000", DIVIDE, "-50000000.5");
        testBinaryOperations("-1.000000000465661", String.valueOf(Integer.MIN_VALUE), DIVIDE, String.valueOf(Integer.MAX_VALUE));
        testBinaryOperations("1", "-9999999999999999", DIVIDE, "-9999999999999999");
    }

    @Test
    public void testPercentOperation() throws CalculationException {

        // percentage for base zero and any percent
        testBinaryOperations("0", "0", PERCENT, "555000000");
        testBinaryOperations("0", "0", PERCENT, "1000");
        testBinaryOperations("0", "0", PERCENT, "100");
        testBinaryOperations("0", "0", PERCENT, "0.6666666666666667");
        testBinaryOperations("0", "0", PERCENT, "0");
        testBinaryOperations("0", "0", PERCENT, "-0.6666666666666667");
        testBinaryOperations("0", "0", PERCENT, "-100");
        testBinaryOperations("0", "0", PERCENT, "-1000");
        testBinaryOperations("0", "0", PERCENT, "-555000000");

        // percentage for positive base
        testBinaryOperations("-500000250000", "1000000.5", PERCENT, "-50000000");
        testBinaryOperations("-500000000000", "1000000", PERCENT, "-50000000");
        testBinaryOperations("-50", "100", PERCENT, "-50");
        testBinaryOperations("2777.5", "5", PERCENT, "55550");
        testBinaryOperations("-0.25", "5", PERCENT, "-5");
        testBinaryOperations("2.e-18", "0.6666666666666667", PERCENT, "0.0000000000000003");
        testBinaryOperations("0", "0.6666666666666667", PERCENT, "0");
        testBinaryOperations("-0.000025", "0.05", PERCENT, "-0.05");
        testBinaryOperations("2.5e-20", "0.000005", PERCENT, "0.0000000000005");
        testBinaryOperations("-2.5e-20", "0.000005", PERCENT, "-0.0000000000005");

        // percentage for negative base
        testBinaryOperations("0.0036", "-0.6", PERCENT, "-0.6");
        testBinaryOperations("2.5e-20", "-0.000005", PERCENT, "-0.0000000000005");
        testBinaryOperations("0.0001", "-0.1", PERCENT, "-0.1");
        testBinaryOperations("-2777.5", "-5", PERCENT, "55550");
        testBinaryOperations("0", "-5", PERCENT, "0");
        testBinaryOperations("-50", "-100", PERCENT, "50");
        testBinaryOperations("500000000000", "-1000000", PERCENT, "-50000000");
        testBinaryOperations("500000005000", "-1000000", PERCENT, "-50000000.5");
    }

    private void testBinaryOperations(String expected, String firstValue, MathOperation operation, String secondValue) throws CalculationException {
        BigDecimal expectedNumber = new BigDecimal(expected);
        scale = expectedNumber.scale();

        BigDecimal firstNumber = new BigDecimal(firstValue);
        BigDecimal secondNumber = new BigDecimal(secondValue);

        Calculator calculator = new StandardCalculator(operation, firstNumber, secondNumber);
        BigDecimal calculationResult = calculator.calculate().setScale(scale, BigDecimal.ROUND_HALF_UP);
        Assert.assertEquals(expectedNumber, calculationResult);
    }

    @Test
    public void testNegateOperation() throws CalculationException {

        // with positive argument
        testUnaryOperations("-9999999999999999", NEGATE, "9999999999999999");
        testUnaryOperations("-1000000.5", NEGATE, "1000000.5");
        testUnaryOperations("-1000", NEGATE, "1000");
        testUnaryOperations("-100", NEGATE, "100");
        testUnaryOperations("-0.6666666666666667", NEGATE, "0.6666666666666667");
        testUnaryOperations("-0.05", NEGATE, "0.05");
        testUnaryOperations("-0.0000000000005", NEGATE, "0.0000000000005");

        // with zero
        testUnaryOperations("0", NEGATE, "0");

        // with negative argument
        testUnaryOperations("0.0000000000005", NEGATE, "-0.0000000000005");
        testUnaryOperations("0.05", NEGATE, "-0.05");
        testUnaryOperations("0.6666666666666667", NEGATE, "-0.6666666666666667");
        testUnaryOperations("100", NEGATE, "-100");
        testUnaryOperations("1000", NEGATE, "-1000");
        testUnaryOperations("555000000", NEGATE, "-555000000");
        testUnaryOperations("9999999999999999", NEGATE, "-9999999999999999");
    }

    @Test
    public void testSquareOperation() throws CalculationException {

        // with positive argument
        testUnaryOperations("1000001000000.25", SQUARE, "1000000.5");
        testUnaryOperations("1000000", SQUARE, "1000");
        testUnaryOperations("10000", SQUARE, "100");
        testUnaryOperations("0.4444444444444445", SQUARE, "0.6666666666666667");
        testUnaryOperations("0.0025", SQUARE, "0.05");
        testUnaryOperations("2.5e-25", SQUARE, "0.0000000000005");

        // with zero
        testUnaryOperations("0", SQUARE, "0");

        // with negative argument
        testUnaryOperations("2.5e-25", SQUARE, "-0.0000000000005");
        testUnaryOperations("0.0025", SQUARE, "-0.05");
        testUnaryOperations("0.4444444444444445", SQUARE, "-0.6666666666666667");
        testUnaryOperations("10000", SQUARE, "-100");
        testUnaryOperations("1000000", SQUARE, "-1000");
        testUnaryOperations("3.08025e+17", SQUARE, "-555000000");
    }

    @Test
    public void testSquareRootOperation() throws CalculationException {

        testUnaryOperations("99999999.999999995", SQUARE_ROOT, "9999999999999999");
        testUnaryOperations("242045.158152358", SQUARE_ROOT, "58585858585");
        testUnaryOperations("1000.00024999996875", SQUARE_ROOT, "1000000.5");
        testUnaryOperations("31.62277660168379", SQUARE_ROOT, "1000");
        testUnaryOperations("10", SQUARE_ROOT, "100");
        testUnaryOperations("0.8164965809277261", SQUARE_ROOT, "0.6666666666666667");
        testUnaryOperations("0.223606797749979", SQUARE_ROOT, "0.05");
        testUnaryOperations("7.071067811865475e-7", SQUARE_ROOT, "0.0000000000005");
        testUnaryOperations("0", SQUARE_ROOT, "0");
    }

    @Test
    public void testReverseOperation() throws CalculationException {

        // positive argument
        testUnaryOperations("0.0000000000000001", REVERSE, "9999999999999999");
        testUnaryOperations("1.801801801801802e-9", REVERSE, "555000000");
        testUnaryOperations("0.001", REVERSE, "1000");
        testUnaryOperations("0.01", REVERSE, "100");
        testUnaryOperations("1.499999999999999925", REVERSE, "0.6666666666666667");
        testUnaryOperations("20", REVERSE, "0.05");

        // negative argument
        testUnaryOperations("-20", REVERSE, "-0.05");
        testUnaryOperations("-1.499999999999999925", REVERSE, "-0.6666666666666667");
        testUnaryOperations("-0.01", REVERSE, "-100");
        testUnaryOperations("-0.001", REVERSE, "-1000");
        testUnaryOperations("-1.801801801801802e-9", REVERSE, "-555000000");
        testUnaryOperations("-0.0000000000000001", REVERSE, "-9999999999999999");
    }


    private void testUnaryOperations(String expected, MathOperation operation, String number) throws CalculationException {
        BigDecimal expectedNumber = new BigDecimal(expected);
        scale = expectedNumber.scale();
        BigDecimal base = new BigDecimal(number);

        Calculator calculator = new StandardCalculator(operation, base);
        BigDecimal calculationResult = calculator.calculate().setScale(scale, BigDecimal.ROUND_HALF_UP);
        Assert.assertEquals(expectedNumber, calculationResult);
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
    }

    private void testOperationForException(MathOperation operation, String... numbers) {
        try {
            String message = "";
            if (numbers.length == 1) {
                testUnaryOperations("0", operation, numbers[0]);
                message = format("Expected CalculationException caused by wrong operation argument. Your operation is %s, " +
                        "argument is %s", operation.toString(), numbers[0]);
            }
            if (numbers.length == 2) {
                testBinaryOperations("0", numbers[0], operation, numbers[1]);
                message = format("Expected CalculationException caused by wrong operation argument. Your operation is %s," +
                                " your first value is %s, second value is %s",
                        operation.toString(), numbers[0], numbers[1]);
            }
            Assert.fail(message);
        } catch (CalculationException e) {
            // expected
        }
    }
}