package com.implemica.CalculatorProject.util;

import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.Test;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.util.ValueTransformerUtil.getBigDecimalValues;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ValueTransformerUtilTest {

    private static final int SCALE = 10;

    @Test
    public void testGetBigDecimalValues() throws CalculationException {

        testGetBigDecimalValues(null, null);

        testGetBigDecimalValues(new BigDecimal[0]);

        BigDecimal[] numbers1 = new BigDecimal[]{new BigDecimal(Integer.MIN_VALUE), new BigDecimal(Integer.MAX_VALUE)};
        String[] stringValues1 = new String[]{"-2147483648", "2147483647"};
        testGetBigDecimalValues(numbers1, stringValues1);

        BigDecimal[] numbers2 = new BigDecimal[]{new BigDecimal(0.17177171717171777778), new BigDecimal(-0.999999999999)};
        String[] stringValues2 = new String[]{"0.17177171717171777778", "-0.999999999999"};
        testGetBigDecimalValues(numbers2, stringValues2);

        BigDecimal[] numbers3 = new BigDecimal[]{new BigDecimal(1), new BigDecimal(0)};
        String[] stringValues3 = new String[]{"1", "0"};
        testGetBigDecimalValues(numbers3, stringValues3);

        BigDecimal[] numbers4 = new BigDecimal[]{new BigDecimal(1.00000000001), new BigDecimal(555555)};
        String[] stringValues4 = new String[]{"1.00000000001", "555555"};
        testGetBigDecimalValues(numbers4, stringValues4);

        BigDecimal[] numbers5 = new BigDecimal[]{new BigDecimal(2.e-1), new BigDecimal(9999999999999999L)};
        String[] stringValues5 = new String[]{"2.e-1", "9999999999999999"};
        testGetBigDecimalValues(numbers5, stringValues5);

        BigDecimal[] numbers6 = new BigDecimal[]{new BigDecimal(5.e+17), new BigDecimal(-77777.8888888888)};
        String[] stringValues6 = new String[]{"5.e+17", "-77777.8888888888"};
        testGetBigDecimalValues(numbers6, stringValues6);

    }

    private void testGetBigDecimalValues(BigDecimal[] expected, String... stringValues) throws CalculationException {
        BigDecimal[] transformedArray = getBigDecimalValues(stringValues);
        if (expected == null && transformedArray == null) {
            assertTrue(true);
            return;
        }
        if (expected == null || stringValues == null) {
            fail("One of arguments is null");
        }
        assertTrue(expected.length == transformedArray.length);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].setScale(SCALE, BigDecimal.ROUND_HALF_UP),
                    transformedArray[i].setScale(SCALE, BigDecimal.ROUND_HALF_UP));
        }
    }

    @Test
    public void testWithWrongArguments() {
        testForException("", "");
        testForException("not a number", "not a number too");
        testForException("--5", "0");
        testForException("0..5", "5555");
        testForException("5Ee");
        testForException("99", "e+-9");
    }

    private void testForException(String... stringValues) {
        try {
            getBigDecimalValues(stringValues);
            fail("Expected CalculationException caused by wrong arguments");
        } catch (CalculationException e) {
            // expected
        }
    }
}
