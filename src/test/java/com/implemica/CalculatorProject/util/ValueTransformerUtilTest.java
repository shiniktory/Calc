package com.implemica.CalculatorProject.util;

import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.implemica.CalculatorProject.util.ValueTransformerUtil.getBigDecimalValues;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ValueTransformerUtilTest {

    private static final int SCALE = 10;

    @Test
    public void testGetBigDecimalValues() throws CalculationException {

        testGetBigDecimalValues(null, null);

        testGetBigDecimalValues(new BigDecimal[0]);

        BigDecimal[] numbers1 = new BigDecimal[]{valueOf(Integer.MIN_VALUE), valueOf(Integer.MAX_VALUE)};
        String[] stringValues1 = new String[]{"-2147483648", "2147483647"};
        testGetBigDecimalValues(numbers1, stringValues1);

        BigDecimal[] numbers2 = new BigDecimal[]{valueOf(0.17177171717171777778), valueOf(-0.999999999999)};
        String[] stringValues2 = new String[]{"0.17177171717171777778", "-0.999999999999"};
        testGetBigDecimalValues(numbers2, stringValues2);

        BigDecimal[] numbers3 = new BigDecimal[]{valueOf(1), valueOf(0)};
        String[] stringValues3 = new String[]{"1", "0"};
        testGetBigDecimalValues(numbers3, stringValues3);

        BigDecimal[] numbers4 = new BigDecimal[]{valueOf(1.00000000001), valueOf(555555)};
        String[] stringValues4 = new String[]{"1.00000000001", "555555"};
        testGetBigDecimalValues(numbers4, stringValues4);

        BigDecimal[] numbers5 = new BigDecimal[]{valueOf(2.e-1), valueOf(9999999999999999L)};
        String[] stringValues5 = new String[]{"2.e-1", "9999999999999999"};
        testGetBigDecimalValues(numbers5, stringValues5);

        BigDecimal[] numbers6 = new BigDecimal[]{valueOf(5.e+17), valueOf(-77777.8888888888)};
        String[] stringValues6 = new String[]{"5.e+17", "-77777.8888888888"};
        testGetBigDecimalValues(numbers6, stringValues6);

    }

    private void testGetBigDecimalValues(BigDecimal[] expected, String... stringValues) throws CalculationException {
        BigDecimal[] transformedArray = getBigDecimalValues(stringValues);
        if (expected == null && transformedArray == null) { // if both arrays are null - no error
            assertTrue(true);
            return;
        }
        if (expected == null || stringValues == null) {
            fail(String.format("One of arguments is null. Input array is null: %s, expected array with numbers is null: %s",
                    stringValues == null, expected == null));
        }

        // verify that expected and resulted arrays' lengths are equal
        assertTrue(expected.length == transformedArray.length);

        // verify that appropriate values are equal
        for (int i = 0; i < expected.length; i++) {
            BigDecimal expectedNumber = expected[i].setScale(SCALE, ROUND_HALF_UP);
            BigDecimal transformedNumber = transformedArray[i].setScale(SCALE, ROUND_HALF_UP);
            assertEquals(expectedNumber, transformedNumber);
        }
    }

    @Test
    public void testWithWrongArguments() {
        testForException("", "");
        testForException(" ", " ");
        testForException(".", "_");
        testForException("--5", "0");
        testForException("0..5", "5555");
        testForException("5Ee");
        testForException("5E-");
        testForException("5e +50");
        testForException("99", "e+-9");
        testForException("not a number", "not a number too");
    }

    private void testForException(String... stringValues) {
        try {
            getBigDecimalValues(stringValues);
            fail("Expected CalculationException caused by wrong arguments. Your arguments are: " + Arrays.toString(stringValues));
        } catch (CalculationException e) {
            // expected
        }
    }
}
