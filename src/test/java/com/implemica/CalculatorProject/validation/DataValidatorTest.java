package com.implemica.CalculatorProject.validation;

import org.junit.Test;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.validation.DataValidator.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataValidatorTest {

    @Test
    public void testEmptyStringValidation() {
        assertTrue(isEmptyString(null));
        assertTrue(isEmptyString(""));

        assertFalse(isEmptyString("!"));
        assertFalse(isEmptyString("not empty"));
        assertFalse(isEmptyString("...................."));
    }

    @Test
    public void testDigitValidation() {
        // Valid digits
        testIsDigit(true, "0");
        testIsDigit(true, "1");
        testIsDigit(true, "2");
        testIsDigit(true, "3");
        testIsDigit(true, "4");
        testIsDigit(true, "5");
        testIsDigit(true, "6");
        testIsDigit(true, "7");
        testIsDigit(true, "8");
        testIsDigit(true, "9");

        // Not valid
        testIsDigit(false, "");
        testIsDigit(false, " ");
        testIsDigit(false, "55");
        testIsDigit(false, "0.0");
        testIsDigit(false, "-1");
        testIsDigit(false, "+5");
        testIsDigit(false, "e");
        testIsDigit(false, "string");
        testIsDigit(false, "--5");
    }

    private void testIsDigit(boolean expected, String digit) {
        boolean validationResult = isDigit(digit);
        assertEquals(expected, validationResult);
    }

    @Test
    public void testNumberValidation() {
        // Valid values
        // Positive numbers
        testIsNumber(true, "0");
        testIsNumber(true, "1");
        testIsNumber(true, "1.");
        testIsNumber(true, "1.2222222222222222222222222222222222");
        testIsNumber(true, "11111111000000");
        testIsNumber(true, "999999999.00000000001");
        testIsNumber(true, "9999999999999999999999");

        // Negative numbers
        testIsNumber(true, "-1");
        testIsNumber(true, "-1.");
        testIsNumber(true, "-0.666666666667");
        testIsNumber(true, "-10.99999999999999999998");
        testIsNumber(true, "-1111111111110000");
        testIsNumber(true, "-999999999999.99999");
        testIsNumber(true, "-10000000000000000000000000000000");

        // Double numbers with exponent
        testIsNumber(true, "0.1e-15");
        testIsNumber(true, "0.09e+2");
        testIsNumber(true, "-0.9e2");
        testIsNumber(true, "0.9E2");
        testIsNumber(true, "-0.9E+2");
        testIsNumber(true, "0.9E-2");
        testIsNumber(true, "-2.e-25");
        testIsNumber(true, "2.E-25");

        // Not valid strings
        testIsNumber(false, null);
        testIsNumber(false, "");
        testIsNumber(false, "not empty");
        testIsNumber(false, "--595");
        testIsNumber(false, "595+");
        testIsNumber(false, "5.5.55");
        testIsNumber(false, "0.9e*2");
        testIsNumber(false, "5.e/-3");
        testIsNumber(false, "5555string999");
    }

    private void testIsNumber(boolean expected, String number) {
        boolean validationResult = isNumber(number);
        assertEquals(expected, validationResult);
    }

    @Test
    public void testNumberLengthValidation() {
        // Valid number length
        // Positive numbers
        testNumberLength(true, "0");
        testNumberLength(true, "1");
        testNumberLength(true, "1321321");
        testNumberLength(true, "5454545454545");
        testNumberLength(true, "888888888888888");
        testNumberLength(true, "9999999999999999");

        // Positive double numbers
        testNumberLength(true, "0.1");
        testNumberLength(true, "0.001");
        testNumberLength(true, "0.166666666666667");
        testNumberLength(true, "0.500000000000005");
        testNumberLength(true, "87.9999999999995");
        testNumberLength(true, "999999999999999.9");

        // Negative numbers
        testNumberLength(true, "-1");
        testNumberLength(true, "-1321321");
        testNumberLength(true, "-5454545454545");
        testNumberLength(true, "-888888888888888");
        testNumberLength(true, "-9999999999999999");
        testNumberLength(true, "-5555555555555555");

        // Negative double numbers
        testNumberLength(true, "-0.1");
        testNumberLength(true, "-0.001");
        testNumberLength(true, "-0.166666666666667");
        testNumberLength(true, "-0.500000000000005");
        testNumberLength(true, "-87.9999999999995");
        testNumberLength(true, "-999999999999999.9");

        // Not valid number length
        // Empty strings
        testNumberLength(false, null);
        testNumberLength(false, "");

        // Positive numbers
        testNumberLength(false, "99999999999999991");
        testNumberLength(false, "999999999999999951");
        testNumberLength(false, "9999999999999999551");
        testNumberLength(false, "99999999999999995551");
        testNumberLength(false, "999999999999999955551");
        testNumberLength(false, "5656565656565656565656565");

        // Positive double numbers
        testNumberLength(false, "56565.5666666666666");
        testNumberLength(false, "0.565655666666666666");
        testNumberLength(false, "0.5656556666666666667");
        testNumberLength(false, "1.56565566666666666");
        testNumberLength(false, "1.5656556666666666667");
        testNumberLength(false, "9999999999999999.90");

        // Negative numbers
        testNumberLength(false, "-55555555555555559");
        testNumberLength(false, "-555555555555555579");
        testNumberLength(false, "-5555555555555555779");
        testNumberLength(false, "-55555555555555557779");
        testNumberLength(false, "-555555555555555577779");
        testNumberLength(false, "-5555555555555555599999");

        // Negative double numbers
        testNumberLength(false, "-9999999999999999.91");
        testNumberLength(false, "-9999999999999999.911");
        testNumberLength(false, "-9999999999999999.9111");
        testNumberLength(false, "-9999999999999999.91111");
        testNumberLength(false, "-9999999999999999.911111");
        testNumberLength(false, "-999999999999999.9199999999");
    }

    private void testNumberLength(boolean expected, String number) {
        boolean validationResult = isNumberLengthValid(number);
        assertEquals(expected, validationResult);
    }

    @Test
    public void testZeroCheck() {
        // is zero
        testIsZero(true, "0");
        testIsZero(true, "0.");
        testIsZero(true, "0.0");
        testIsZero(true, "0.0000");
        testIsZero(true, "0000");
        testIsZero(true, "0000.");
        testIsZero(true, "0000.0");
        testIsZero(true, "0000.000");

        // not zero
        testIsZero(false, "0.1");
        testIsZero(false, "0.000000000000000000001");
        testIsZero(false, "-0.0000000000000000000001");
        testIsZero(false, "5");
        testIsZero(false, "-5");
        testIsZero(false, "9999999999999999");
        testIsZero(false, "-9999999999999999");
    }

    private void testIsZero(boolean expected, String number) {
        boolean validationResult = isZero(new BigDecimal(number));
        assertEquals(expected, validationResult);
    }

    @Test
    public void testExponentValidation() {
        // need formatting to exponential view
        testExponentNeed(true, "0.000000000000000099");
        testExponentNeed(true, "0.00000000000000000000005");
        testExponentNeed(true, "0.00099999999999999999");
        testExponentNeed(true, "0.00000055632559999999");
        testExponentNeed(true, "0.000016666666666667");
        testExponentNeed(true, "-0.00001000005000009");
        testExponentNeed(true, "-0.000000000000000099");
        testExponentNeed(true, "-555555555555555599");
        testExponentNeed(true, "-9999999999999999.91");
        testExponentNeed(true, "10000000000000000");
        testExponentNeed(true, "19999999999999999");
        testExponentNeed(true, "-990000000000000111.00000001");
        testExponentNeed(true, "165145454687943158");
        testExponentNeed(true, "15151546479754244444444444.4444449");

        // no need formatting to exponential view
        testExponentNeed(false, "-56565.56666666666666");
        testExponentNeed(false, "0.01");
        testExponentNeed(false, "-0.01");
        testExponentNeed(false, "599");
        testExponentNeed(false, "-599");
        testExponentNeed(false, "9999999");
        testExponentNeed(false, "-912930444");
        testExponentNeed(false, "0");
        testExponentNeed(false, "0.00");
        testExponentNeed(false, "15555");
        testExponentNeed(false, "9999999999");
        testExponentNeed(false, "-1333.3");
        testExponentNeed(false, "0.00001524");
        testExponentNeed(false, "0.00005555525");
    }

    private void testExponentNeed(boolean expected, String stringValue) {
        BigDecimal number = new BigDecimal(stringValue);
        boolean validationResult = isExponentFormattingNeed(number);
        assertEquals(expected, validationResult);
    }
}
