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

        BigDecimal[] nums1 = new BigDecimal[]{new BigDecimal(Integer.MIN_VALUE), new BigDecimal(Integer.MAX_VALUE)};
        String[] numStrs1 = new String[]{"-2147483648", "2147483647"};
        testGetBigDecimalValues(nums1, numStrs1);

        BigDecimal[] nums2 = new BigDecimal[]{new BigDecimal(0.17177171717171777778), new BigDecimal(-0.999999999999)};
        String[] numStrs2 = new String[]{"0.17177171717171777778", "-0.999999999999"};
        testGetBigDecimalValues(nums2, numStrs2);

        BigDecimal[] nums3 = new BigDecimal[]{new BigDecimal(1), new BigDecimal(0)};
        String[] numStrs3 = new String[]{"1", "0"};
        testGetBigDecimalValues(nums3, numStrs3);

        BigDecimal[] nums4 = new BigDecimal[]{new BigDecimal(1.00000000001), new BigDecimal(555555)};
        String[] numStrs4 = new String[]{"1.00000000001", "555555"};
        testGetBigDecimalValues(nums4, numStrs4);

        BigDecimal[] nums5 = new BigDecimal[]{new BigDecimal(2.e-1), new BigDecimal(9999999999999999L)};
        String[] numStrs5 = new String[]{"2.e-1", "9999999999999999"};
        testGetBigDecimalValues(nums5, numStrs5);

        BigDecimal[] nums6 = new BigDecimal[]{new BigDecimal(5.e+17), new BigDecimal(-77777.8888888888)};
        String[] numStrs6 = new String[]{"5.e+17", "-77777.8888888888"};
        testGetBigDecimalValues(nums6, numStrs6);

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
