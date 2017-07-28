package com.implemica.CalculatorProject.calculation;

import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;

public class StandardCalculatorTest {

    private static int scale;

    @Test
    public void testInitWithWrongArgumentCount() {
        testInitForException(null);
        testInitForException(null, new BigDecimal[1]);

        // binary operation with wrong argument count
        testInitForException(ADD, null);
        testInitForException(ADD);
        testInitForException(ADD, new BigDecimal[1]);
        testInitForException(ADD, new BigDecimal[3]);
        testInitForException(ADD, new BigDecimal[4]);
        testInitForException(ADD, new BigDecimal[10]);

        // unary operation with wrong argument count
        testInitForException(SQUARE_ROOT, null);
        testInitForException(SQUARE_ROOT);
        testInitForException(SQUARE_ROOT, new BigDecimal[2]);
        testInitForException(SQUARE_ROOT, new BigDecimal[3]);
        testInitForException(SQUARE_ROOT, new BigDecimal[4]);
        testInitForException(SQUARE_ROOT, new BigDecimal[10]);
    }

    private void testInitForException(MathOperation operation, BigDecimal... number) {
        try {
            new StandardCalculator(operation, number);
            Assert.fail(String.format("Expected CalculationException with wrong arguments. Your operation is %s, count %s",
                    operation, (number == null) ? "null" : number.length));
        } catch (CalculationException e) {
            // expected
        }
    }

    @Test
    public void testAddOperation() throws CalculationException {
        scale = 16;

        // with zero arguments
        testCalculations(new BigDecimal(555000000), ADD, new BigDecimal(0), new BigDecimal(555000000));
        testCalculations(new BigDecimal(1000), ADD, new BigDecimal(0), new BigDecimal(1000));
        testCalculations(new BigDecimal(100), ADD, new BigDecimal(0), new BigDecimal(100));
        testCalculations(new BigDecimal(0.6666666666666667), ADD, new BigDecimal(0), new BigDecimal(0.6666666666666667));
        testCalculations(new BigDecimal(0), ADD, new BigDecimal(0), new BigDecimal(0));
        testCalculations(new BigDecimal(-0.6666666666666667), ADD, new BigDecimal(0), new BigDecimal(-0.6666666666666667));
        testCalculations(new BigDecimal(-100), ADD, new BigDecimal(0), new BigDecimal(-100));
        testCalculations(new BigDecimal(-1000), ADD, new BigDecimal(0), new BigDecimal(-1000));
        testCalculations(new BigDecimal(-555000000), ADD, new BigDecimal(0), new BigDecimal(-555000000));

        // with positive arguments
        testCalculations(new BigDecimal(19999999999999998L), ADD, new BigDecimal(9999999999999999L), new BigDecimal(9999999999999999L));
        testCalculations(new BigDecimal(55555), ADD, new BigDecimal(5), new BigDecimal(55550));
        testCalculations(new BigDecimal(0.666666666666667), ADD, new BigDecimal(0.6666666666666667), new BigDecimal(0.0000000000000003));
        testCalculations(new BigDecimal(0.0000050000005), ADD, new BigDecimal(0.000005), new BigDecimal(0.0000000000005));

        // with positive and negative arguments
        testCalculations(new BigDecimal(0), ADD, new BigDecimal(9999999999999999L), new BigDecimal(-9999999999999999L));
        testCalculations(new BigDecimal(-48999999.5), ADD, new BigDecimal(1000000.5), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(-49000000), ADD, new BigDecimal(1000000), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(50), ADD, new BigDecimal(100), new BigDecimal(-50));
        testCalculations(new BigDecimal(0), ADD, new BigDecimal(5), new BigDecimal(-5));
        testCalculations(new BigDecimal(0), ADD, new BigDecimal(0.05), new BigDecimal(-0.05));
        testCalculations(new BigDecimal(0.0000049999995), ADD, new BigDecimal(0.000005), new BigDecimal(-0.0000000000005));

        // with negative and positive arguments
        testCalculations(new BigDecimal(55545), ADD, new BigDecimal(-5), new BigDecimal(55550));
        testCalculations(new BigDecimal(-50), ADD, new BigDecimal(-100), new BigDecimal(50));
        testCalculations(new BigDecimal(-1), ADD, new BigDecimal(Integer.MIN_VALUE), new BigDecimal(Integer.MAX_VALUE));

        // with both negative arguments
        testCalculations(new BigDecimal(-1.2), ADD, new BigDecimal(-0.6), new BigDecimal(-0.6));
        testCalculations(new BigDecimal(-0.0000050000005), ADD, new BigDecimal(-0.000005), new BigDecimal(-0.0000000000005));
        testCalculations(new BigDecimal(-0.2), ADD, new BigDecimal(-0.1), new BigDecimal(-0.1));
        testCalculations(new BigDecimal(-51000000), ADD, new BigDecimal(-1000000), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(-51000000.5), ADD, new BigDecimal(-1000000), new BigDecimal(-50000000.5));
        testCalculations(new BigDecimal(-19999999999999998L), ADD, new BigDecimal(-9999999999999999L), new BigDecimal(-9999999999999999L));
    }

    @Test
    public void testSubtractOperation() throws CalculationException {
        scale = 16;

        // with zero arguments
        testCalculations(new BigDecimal(-555000000), SUBTRACT, new BigDecimal(0), new BigDecimal(555000000));
        testCalculations(new BigDecimal(-1000), SUBTRACT, new BigDecimal(0), new BigDecimal(1000));
        testCalculations(new BigDecimal(-100), SUBTRACT, new BigDecimal(0), new BigDecimal(100));
        testCalculations(new BigDecimal(-0.6666666666666667), SUBTRACT, new BigDecimal(0), new BigDecimal(0.6666666666666667));
        testCalculations(new BigDecimal(0), SUBTRACT, new BigDecimal(0), new BigDecimal(0));
        testCalculations(new BigDecimal(0.6666666666666667), SUBTRACT, new BigDecimal(0), new BigDecimal(-0.6666666666666667));
        testCalculations(new BigDecimal(100), SUBTRACT, new BigDecimal(0), new BigDecimal(-100));
        testCalculations(new BigDecimal(1000), SUBTRACT, new BigDecimal(0), new BigDecimal(-1000));
        testCalculations(new BigDecimal(555000000), SUBTRACT, new BigDecimal(0), new BigDecimal(-555000000));

        // with positive arguments
        testCalculations(new BigDecimal(0), SUBTRACT, new BigDecimal(9999999999999999L), new BigDecimal(9999999999999999L));
        testCalculations(new BigDecimal(19999999999999998L), SUBTRACT, new BigDecimal(9999999999999999L), new BigDecimal(-9999999999999999L));
        testCalculations(new BigDecimal(0), SUBTRACT, new BigDecimal(Integer.MAX_VALUE), new BigDecimal(Integer.MAX_VALUE));
        testCalculations(new BigDecimal(-55545), SUBTRACT, new BigDecimal(5), new BigDecimal(55550));
        testCalculations(new BigDecimal(0.6666666666666664), SUBTRACT, new BigDecimal(0.6666666666666667), new BigDecimal(0.0000000000000003));
        testCalculations(new BigDecimal(0.0000049999995), SUBTRACT, new BigDecimal(0.000005), new BigDecimal(0.0000000000005));

        //with positive and negative arguments
        testCalculations(new BigDecimal(51000000.5), SUBTRACT, new BigDecimal(1000000.5), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(51000000), SUBTRACT, new BigDecimal(1000000), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(150), SUBTRACT, new BigDecimal(100), new BigDecimal(-50));
        testCalculations(new BigDecimal(10), SUBTRACT, new BigDecimal(5), new BigDecimal(-5));
        testCalculations(new BigDecimal(0.1), SUBTRACT, new BigDecimal(0.05), new BigDecimal(-0.05));
        testCalculations(new BigDecimal(0.0000050000005), SUBTRACT, new BigDecimal(0.000005), new BigDecimal(-0.0000000000005));

        // with negative and positive arguments
        testCalculations(new BigDecimal(-55555), SUBTRACT, new BigDecimal(-5), new BigDecimal(55550));
        testCalculations(new BigDecimal(-51000000.5), SUBTRACT, new BigDecimal(-1000000.5), new BigDecimal(50000000));
        testCalculations(new BigDecimal(-51000000), SUBTRACT, new BigDecimal(-1000000), new BigDecimal(50000000));
        testCalculations(new BigDecimal(-150), SUBTRACT, new BigDecimal(-100), new BigDecimal(50));
        testCalculations(new BigDecimal(-10), SUBTRACT, new BigDecimal(-5), new BigDecimal(5));
        testCalculations(new BigDecimal(-0.1), SUBTRACT, new BigDecimal(-0.05), new BigDecimal(0.05));
        testCalculations(new BigDecimal(-0.0000050000005), SUBTRACT, new BigDecimal(-0.000005), new BigDecimal(0.0000000000005));

        // with negative arguments
        testCalculations(new BigDecimal(0), SUBTRACT, new BigDecimal(-0.6), new BigDecimal(-0.6));
        testCalculations(new BigDecimal(-0.0000049999995), SUBTRACT, new BigDecimal(-0.000005), new BigDecimal(-0.0000000000005));
        testCalculations(new BigDecimal(0), SUBTRACT, new BigDecimal(-0.1), new BigDecimal(-0.1));
        testCalculations(new BigDecimal(49000000), SUBTRACT, new BigDecimal(-1000000), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(49000000.5), SUBTRACT, new BigDecimal(-1000000), new BigDecimal(-50000000.5));
        testCalculations(new BigDecimal(0), SUBTRACT, new BigDecimal(-9999999999999999L), new BigDecimal(-9999999999999999L));
    }

    @Test
    public void testMultiplyOperation() throws CalculationException {
        scale = 16;

        // with zero arguments
        testCalculations(new BigDecimal(0), MULTIPLY, new BigDecimal(0), new BigDecimal(555000000));
        testCalculations(new BigDecimal(0), MULTIPLY, new BigDecimal(0), new BigDecimal(1000));
        testCalculations(new BigDecimal(0), MULTIPLY, new BigDecimal(0), new BigDecimal(100));
        testCalculations(new BigDecimal(0), MULTIPLY, new BigDecimal(0), new BigDecimal(0.6666666666666667));
        testCalculations(new BigDecimal(0), MULTIPLY, new BigDecimal(0), new BigDecimal(0));
        testCalculations(new BigDecimal(0), MULTIPLY, new BigDecimal(0), new BigDecimal(-0.6666666666666667));
        testCalculations(new BigDecimal(0), MULTIPLY, new BigDecimal(0), new BigDecimal(-100));
        testCalculations(new BigDecimal(0), MULTIPLY, new BigDecimal(0), new BigDecimal(-1000));
        testCalculations(new BigDecimal(0), MULTIPLY, new BigDecimal(0), new BigDecimal(-555000000));

        // positive argument on positive
        testCalculations(new BigDecimal(50000025000000L), MULTIPLY, new BigDecimal(1000000.5), new BigDecimal(50000000));
        testCalculations(new BigDecimal(50000000000000L), MULTIPLY, new BigDecimal(1000000), new BigDecimal(50000000));
        testCalculations(new BigDecimal(5000), MULTIPLY, new BigDecimal(100), new BigDecimal(50));
        testCalculations(new BigDecimal(277750), MULTIPLY, new BigDecimal(5), new BigDecimal(55550));
        testCalculations(new BigDecimal(25), MULTIPLY, new BigDecimal(5), new BigDecimal(5));
        testCalculations(new BigDecimal(0.0000000000000002), MULTIPLY, new BigDecimal(0.6666666666666667), new BigDecimal(0.0000000000000003));
        testCalculations(new BigDecimal(0.0025), MULTIPLY, new BigDecimal(0.05), new BigDecimal(0.05));
        testCalculations(new BigDecimal(2.5e-18), MULTIPLY, new BigDecimal(0.000005), new BigDecimal(0.0000000000005));
        testCalculations(new BigDecimal(2.5e-18), MULTIPLY, new BigDecimal(0.000005), new BigDecimal(0.0000000000005));

        // positive argument on negative
        testCalculations(new BigDecimal(-50000025000000L), MULTIPLY, new BigDecimal(1000000.5), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(-50000000000000L), MULTIPLY, new BigDecimal(1000000), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(-5000), MULTIPLY, new BigDecimal(100), new BigDecimal(-50));
        testCalculations(new BigDecimal(277750), MULTIPLY, new BigDecimal(5), new BigDecimal(55550));
        testCalculations(new BigDecimal(-25), MULTIPLY, new BigDecimal(5), new BigDecimal(-5));
        testCalculations(new BigDecimal(0.0000000000000002), MULTIPLY, new BigDecimal(0.6666666666666667), new BigDecimal(0.0000000000000003));
        testCalculations(new BigDecimal(-0.0025), MULTIPLY, new BigDecimal(0.05), new BigDecimal(-0.05));
        testCalculations(new BigDecimal(2.5e-18), MULTIPLY, new BigDecimal(0.000005), new BigDecimal(0.0000000000005));
        testCalculations(new BigDecimal(-2.5e-18), MULTIPLY, new BigDecimal(0.000005), new BigDecimal(-0.0000000000005));

        // negative on positive
        testCalculations(new BigDecimal(-277750), MULTIPLY, new BigDecimal(-5), new BigDecimal(55550));
        testCalculations(new BigDecimal(-5000), MULTIPLY, new BigDecimal(-100), new BigDecimal(50));
        testCalculations(new BigDecimal(-4.611686016279904e+18), MULTIPLY, new BigDecimal(Integer.MIN_VALUE), new BigDecimal(Integer.MAX_VALUE));
        testCalculations(new BigDecimal(-0.36), MULTIPLY, new BigDecimal(-0.6), new BigDecimal(0.6));
        testCalculations(new BigDecimal(-2.5e-18), MULTIPLY, new BigDecimal(-0.000005), new BigDecimal(0.0000000000005));
        testCalculations(new BigDecimal(-0.01), MULTIPLY, new BigDecimal(-0.1), new BigDecimal(0.1));
        testCalculations(new BigDecimal(-50000000000000L), MULTIPLY, new BigDecimal(-1000000), new BigDecimal(50000000));
        testCalculations(new BigDecimal(-50000000500000L), MULTIPLY, new BigDecimal(-1000000), new BigDecimal(50000000.5));

        // negative argument on negative
        testCalculations(new BigDecimal(277750), MULTIPLY, new BigDecimal(-5), new BigDecimal(-55550));
        testCalculations(new BigDecimal(5000), MULTIPLY, new BigDecimal(-100), new BigDecimal(-50));
        testCalculations(new BigDecimal(0.36), MULTIPLY, new BigDecimal(-0.6), new BigDecimal(-0.6));
        testCalculations(new BigDecimal(2.5e-18), MULTIPLY, new BigDecimal(-0.000005), new BigDecimal(-0.0000000000005));
        testCalculations(new BigDecimal(0.01), MULTIPLY, new BigDecimal(-0.1), new BigDecimal(-0.1));
        testCalculations(new BigDecimal(50000000000000L), MULTIPLY, new BigDecimal(-1000000), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(50000000500000L), MULTIPLY, new BigDecimal(-1000000), new BigDecimal(-50000000.5));

    }

    @Test
    public void testNegateOperation() throws CalculationException {
        scale = 16;

        // negate operation
        testCalculations(new BigDecimal(-9999999999999999L), NEGATE, new BigDecimal(9999999999999999L));
        testCalculations(new BigDecimal(-1000000.5), NEGATE, new BigDecimal(1000000.5));
        testCalculations(new BigDecimal(-1000), NEGATE, new BigDecimal(1000));
        testCalculations(new BigDecimal(-100), NEGATE, new BigDecimal(100));
        testCalculations(new BigDecimal(-0.6666666666666667), NEGATE, new BigDecimal(0.6666666666666667));
        testCalculations(new BigDecimal(-0.05), NEGATE, new BigDecimal(0.05));
        testCalculations(new BigDecimal(-0.0000000000005), NEGATE, new BigDecimal(0.0000000000005));
        testCalculations(new BigDecimal(0), NEGATE, new BigDecimal(0));
        testCalculations(new BigDecimal(0.0000000000005), NEGATE, new BigDecimal(-0.0000000000005));
        testCalculations(new BigDecimal(0.05), NEGATE, new BigDecimal(-0.05));
        testCalculations(new BigDecimal(0.6666666666666667), NEGATE, new BigDecimal(-0.6666666666666667));
        testCalculations(new BigDecimal(100), NEGATE, new BigDecimal(-100));
        testCalculations(new BigDecimal(1000), NEGATE, new BigDecimal(-1000));
        testCalculations(new BigDecimal(555000000), NEGATE, new BigDecimal(-555000000));
        testCalculations(new BigDecimal(9999999999999999L), NEGATE, new BigDecimal(-9999999999999999L));
    }

    @Test
    public void testPercentOperation() throws CalculationException {
        scale = 16;

        // percentage for base zero and any percent
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(0), new BigDecimal(555000000));
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(0), new BigDecimal(1000));
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(0), new BigDecimal(100));
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(0), new BigDecimal(0.6666666666666667));
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(0), new BigDecimal(0));
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(0), new BigDecimal(-0.6666666666666667));
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(0), new BigDecimal(-100));
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(0), new BigDecimal(-1000));
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(0), new BigDecimal(-555000000));

        // percentage for positive base
        testCalculations(new BigDecimal(-500000250000L), PERCENT, new BigDecimal(1000000.5), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(-500000000000L), PERCENT, new BigDecimal(1000000), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(-50), PERCENT, new BigDecimal(100), new BigDecimal(-50));
        testCalculations(new BigDecimal(2777.5), PERCENT, new BigDecimal(5), new BigDecimal(55550));
        testCalculations(new BigDecimal(-0.25), PERCENT, new BigDecimal(5), new BigDecimal(-5));
        testCalculations(new BigDecimal(2.e-18), PERCENT, new BigDecimal(0.6666666666666667), new BigDecimal(0.0000000000000003));
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(0.6666666666666667), new BigDecimal(0));
        testCalculations(new BigDecimal(-0.000025), PERCENT, new BigDecimal(0.05), new BigDecimal(-0.05));
        testCalculations(new BigDecimal(2.5e-20), PERCENT, new BigDecimal(0.000005), new BigDecimal(0.0000000000005));
        testCalculations(new BigDecimal(-2.5e-20), PERCENT, new BigDecimal(0.000005), new BigDecimal(-0.0000000000005));

        // percentage for negative base
        testCalculations(new BigDecimal(0.0036), PERCENT, new BigDecimal(-0.6), new BigDecimal(-0.6));
        testCalculations(new BigDecimal(2.5e-20), PERCENT, new BigDecimal(-0.000005), new BigDecimal(-0.0000000000005));
        testCalculations(new BigDecimal(0.0001), PERCENT, new BigDecimal(-0.1), new BigDecimal(-0.1));
        testCalculations(new BigDecimal(-2777.5), PERCENT, new BigDecimal(-5), new BigDecimal(55550));
        testCalculations(new BigDecimal(0), PERCENT, new BigDecimal(-5), new BigDecimal(0));
        testCalculations(new BigDecimal(-50), PERCENT, new BigDecimal(-100), new BigDecimal(50));
        testCalculations(new BigDecimal(500000000000L), PERCENT, new BigDecimal(-1000000), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(500000005000L), PERCENT, new BigDecimal(-1000000), new BigDecimal(-50000000.5));
    }

    @Test
    public void testSquareOperation() throws CalculationException {
        scale = 16;

        // with positive argument
        testCalculations(new BigDecimal(1000001000000.25), SQUARE, new BigDecimal(1000000.5));
        testCalculations(new BigDecimal(1000000), SQUARE, new BigDecimal(1000));
        testCalculations(new BigDecimal(10000), SQUARE, new BigDecimal(100));
        testCalculations(new BigDecimal(0.4444444444444445), SQUARE, new BigDecimal(0.6666666666666667));
        testCalculations(new BigDecimal(0.0025), SQUARE, new BigDecimal(0.05));
        testCalculations(new BigDecimal(2.5e-25), SQUARE, new BigDecimal(0.0000000000005));

        // with zero
        testCalculations(new BigDecimal(0), SQUARE, new BigDecimal(0));

        // with negative argument
        testCalculations(new BigDecimal(2.5e-25), SQUARE, new BigDecimal(-0.0000000000005));
        testCalculations(new BigDecimal(0.0025), SQUARE, new BigDecimal(-0.05));
        testCalculations(new BigDecimal(0.4444444444444445), SQUARE, new BigDecimal(-0.6666666666666667));
        testCalculations(new BigDecimal(10000), SQUARE, new BigDecimal(-100));
        testCalculations(new BigDecimal(1000000), SQUARE, new BigDecimal(-1000));
        testCalculations(new BigDecimal(3.08025e+17), SQUARE, new BigDecimal(-555000000));
    }

    @Test
    public void testDivideOperation() throws CalculationException {
        scale = 7;
        // zero by non-zero number
        testCalculations(new BigDecimal(0), DIVIDE, new BigDecimal(0), new BigDecimal(555000000));
        testCalculations(new BigDecimal(0), DIVIDE, new BigDecimal(0), new BigDecimal(1000));
        testCalculations(new BigDecimal(0), DIVIDE, new BigDecimal(0), new BigDecimal(100));
        testCalculations(new BigDecimal(0), DIVIDE, new BigDecimal(0), new BigDecimal(0.6666666666666667));
        testCalculations(new BigDecimal(0), DIVIDE, new BigDecimal(0), new BigDecimal(-0.6666666666666667));
        testCalculations(new BigDecimal(0), DIVIDE, new BigDecimal(0), new BigDecimal(-100));
        testCalculations(new BigDecimal(0), DIVIDE, new BigDecimal(0), new BigDecimal(-1000));
        testCalculations(new BigDecimal(0), DIVIDE, new BigDecimal(0), new BigDecimal(-555000000));

        // positive number by any non-zero number
        testCalculations(new BigDecimal(1), DIVIDE, new BigDecimal(9999999999999999L), new BigDecimal(9999999999999999L));
        testCalculations(new BigDecimal(-1), DIVIDE, new BigDecimal(9999999999999999L), new BigDecimal(-9999999999999999L));
        testCalculations(new BigDecimal(-0.02000001), DIVIDE, new BigDecimal(1000000.5), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(-0.02), DIVIDE, new BigDecimal(1000000), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(-2), DIVIDE, new BigDecimal(100), new BigDecimal(-50));
        testCalculations(new BigDecimal(9.000900090009001e-5), DIVIDE, new BigDecimal(5), new BigDecimal(55550));
        testCalculations(new BigDecimal(-1), DIVIDE, new BigDecimal(5), new BigDecimal(-5));
        testCalculations(new BigDecimal(-1), DIVIDE, new BigDecimal(0.05), new BigDecimal(-0.05));
        testCalculations(new BigDecimal(10000000), DIVIDE, new BigDecimal(0.000005), new BigDecimal(0.0000000000005));
        testCalculations(new BigDecimal(-10000000), DIVIDE, new BigDecimal(0.000005), new BigDecimal(-0.0000000000005));

        // negative number by any non-zero
        testCalculations(new BigDecimal(1), DIVIDE, new BigDecimal(-0.6), new BigDecimal(-0.6));
        testCalculations(new BigDecimal(10000000), DIVIDE, new BigDecimal(-0.000005), new BigDecimal(-0.0000000000005));
        testCalculations(new BigDecimal(1), DIVIDE, new BigDecimal(-0.1), new BigDecimal(-0.1));
        testCalculations(new BigDecimal(-9.000900090009001e-5), DIVIDE, new BigDecimal(-5), new BigDecimal(55550));
        testCalculations(new BigDecimal(-2), DIVIDE, new BigDecimal(-100), new BigDecimal(50));
        testCalculations(new BigDecimal(0.02), DIVIDE, new BigDecimal(-1000000), new BigDecimal(-50000000));
        testCalculations(new BigDecimal(0.0199999998), DIVIDE, new BigDecimal(-1000000), new BigDecimal(-50000000.5));
        testCalculations(new BigDecimal(-1.000000000465661), DIVIDE, new BigDecimal(Integer.MIN_VALUE), new BigDecimal(Integer.MAX_VALUE));
        testCalculations(new BigDecimal(1), DIVIDE, new BigDecimal(-9999999999999999L), new BigDecimal(-9999999999999999L));
    }

    @Test
    public void testSquareRootOperation() throws CalculationException {
        scale = 7;

        testCalculations(new BigDecimal(99999999.999999995), SQUARE_ROOT, new BigDecimal(9999999999999999L));
        testCalculations(new BigDecimal(242045.158152358), SQUARE_ROOT, new BigDecimal(58585858585L));
        testCalculations(new BigDecimal(1000.00024999996875), SQUARE_ROOT, new BigDecimal(1000000.5));
        testCalculations(new BigDecimal(31.62277660168379), SQUARE_ROOT, new BigDecimal(1000));
        testCalculations(new BigDecimal(10), SQUARE_ROOT, new BigDecimal(100));
        testCalculations(new BigDecimal(0.8164965809277261), SQUARE_ROOT, new BigDecimal(0.6666666666666667));
        testCalculations(new BigDecimal(0.223606797749979), SQUARE_ROOT, new BigDecimal(0.05));
        testCalculations(new BigDecimal(7.071067811865475e-7), SQUARE_ROOT, new BigDecimal(0.0000000000005));
        testCalculations(new BigDecimal(0), SQUARE_ROOT, new BigDecimal(0));
    }

    @Test
    public void testReverseOperation() throws CalculationException {
        scale = 14;

        // positive argument
        testCalculations(new BigDecimal(0.0000000000000001), REVERSE, new BigDecimal(9999999999999999L));
        testCalculations(new BigDecimal(1.801801801801802e-9), REVERSE, new BigDecimal(555000000));
        testCalculations(new BigDecimal(0.001), REVERSE, new BigDecimal(1000));
        testCalculations(new BigDecimal(0.01), REVERSE, new BigDecimal(100));
        testCalculations(new BigDecimal(1.499999999999999925), REVERSE, new BigDecimal(0.6666666666666667));
        testCalculations(new BigDecimal(20), REVERSE, new BigDecimal(0.05));

        // negative argument
        testCalculations(new BigDecimal(-20), REVERSE, new BigDecimal(-0.05));
        testCalculations(new BigDecimal(-1.499999999999999925), REVERSE, new BigDecimal(-0.6666666666666667));
        testCalculations(new BigDecimal(-0.01), REVERSE, new BigDecimal(-100));
        testCalculations(new BigDecimal(-0.001), REVERSE, new BigDecimal(-1000));
        testCalculations(new BigDecimal(-1.801801801801802e-9), REVERSE, new BigDecimal(-555000000));
        testCalculations(new BigDecimal(-0.0000000000000001), REVERSE, new BigDecimal(-9999999999999999L));
    }

    private void testCalculations(BigDecimal expected, MathOperation operation, BigDecimal... numbers) throws CalculationException {
        Calculator calculator = new StandardCalculator(operation, numbers);
        BigDecimal calculationResult = calculator.calculate().setScale(scale, BigDecimal.ROUND_HALF_UP);
        Assert.assertEquals(expected.setScale(scale, BigDecimal.ROUND_HALF_UP), calculationResult);
    }

    @Test
    public void testOperationWithWrongArguments() {

        // division by zero
        testOperationForException(DIVIDE, new BigDecimal(555000000), new BigDecimal(0));
        testOperationForException(DIVIDE, new BigDecimal(1000), new BigDecimal(0));
        testOperationForException(DIVIDE, new BigDecimal(100), new BigDecimal(0));
        testOperationForException(DIVIDE, new BigDecimal(0.6666666666666667), new BigDecimal(0));
        testOperationForException(DIVIDE, new BigDecimal(0), new BigDecimal(0));
        testOperationForException(DIVIDE, new BigDecimal(0), new BigDecimal(0.0));
        testOperationForException(DIVIDE, new BigDecimal(-0.6666666666666667), new BigDecimal(0));
        testOperationForException(DIVIDE, new BigDecimal(-100), new BigDecimal(0));
        testOperationForException(DIVIDE, new BigDecimal(-1000), new BigDecimal(0));
        testOperationForException(DIVIDE, new BigDecimal(-555000000), new BigDecimal(0));

        // square root with negative argument
        testOperationForException(SQUARE_ROOT, new BigDecimal(-1));
        testOperationForException(SQUARE_ROOT, new BigDecimal(-5));
        testOperationForException(SQUARE_ROOT, new BigDecimal(-5.5));
        testOperationForException(SQUARE_ROOT, new BigDecimal(-10000000));
        testOperationForException(SQUARE_ROOT, new BigDecimal(Integer.MIN_VALUE));
        testOperationForException(SQUARE_ROOT, new BigDecimal(-555555555000005L));
        testOperationForException(SQUARE_ROOT, new BigDecimal(-9999999999999999L));
        testOperationForException(SQUARE_ROOT, new BigDecimal(Long.MIN_VALUE));

        // reverse with zero argument
        testOperationForException(REVERSE, new BigDecimal(0));
        testOperationForException(REVERSE, new BigDecimal(0.0));
    }

    private void testOperationForException(MathOperation operation, BigDecimal... numbers) {
        try {
            Calculator calculator = new StandardCalculator(operation, numbers);
            calculator.calculate();
            Assert.fail("Expected CalculationException caused by wrong operation argument. Your operation is %s");
        } catch (CalculationException e) {
            // expected
        }
    }
}