package com.implemica.CalculatorProject.validation;

import org.junit.Test;

import java.math.BigDecimal;

import static com.implemica.CalculatorProject.validation.DataValidator.*;
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
    public void testIsDigitValidation() {
        // Valid digits
        assertTrue(isDigit("0"));
        assertTrue(isDigit("1"));
        assertTrue(isDigit("2"));
        assertTrue(isDigit("3"));
        assertTrue(isDigit("4"));
        assertTrue(isDigit("5"));
        assertTrue(isDigit("6"));
        assertTrue(isDigit("7"));
        assertTrue(isDigit("8"));
        assertTrue(isDigit("9"));

        // Not valid
        assertFalse(isDigit(""));
        assertFalse(isDigit(" "));
        assertFalse(isDigit("55"));
        assertFalse(isDigit("0.0"));
        assertFalse(isDigit("-1"));
        assertFalse(isDigit("+5"));
        assertFalse(isDigit("e"));
        assertFalse(isDigit("string"));
        assertFalse(isDigit("--5"));
    }

    @Test
    public void testIsNumberValidation() {
        // Valid values
        // Positive numbers
        assertTrue(isNumber("0"));
        assertTrue(isNumber("1"));
        assertTrue(isNumber("1.2222222222222222222222222222222222"));
        assertTrue(isNumber("11111111000000"));
        assertTrue(isNumber("999999999.00000000001"));
        assertTrue(isNumber("9999999999999999999999"));

        // Negative numbers
        assertTrue(isNumber("-1"));
        assertTrue(isNumber("-0.666666666667"));
        assertTrue(isNumber("-10.99999999999999999998"));
        assertTrue(isNumber("-1111111111110000"));
        assertTrue(isNumber("-999999999999.99999"));
        assertTrue(isNumber("-10000000000000000000000000000000"));

        // Double numbers with exponent
        assertTrue(isNumber("0.1e-15"));
        assertTrue(isNumber("0.09e+2"));
        assertTrue(isNumber("0.9e2"));
        assertTrue(isNumber("0.9E2"));
        assertTrue(isNumber("0.9E+2"));
        assertTrue(isNumber("0.9E-2"));
        assertTrue(isNumber("2.e-25"));
        assertTrue(isNumber("2.E-25"));


        // Not valid string with numbers
        assertFalse(isNumber(null));
        assertFalse(isNumber(""));
        assertFalse(isNumber("not empty"));
        assertFalse(isNumber("--595"));
        assertFalse(isNumber("5.5.55"));
        assertFalse(isNumber("0.9e*2"));
        assertFalse(isNumber("5.e/-3"));
        assertFalse(isNumber("5555string999"));
    }

    @Test
    public void testNumberLengthValidation() {
        // Valid number length
        // Positive numbers
        assertTrue(isNumberLengthValid("0"));
        assertTrue(isNumberLengthValid("1"));
        assertTrue(isNumberLengthValid("1321321"));
        assertTrue(isNumberLengthValid("5454545454545"));
        assertTrue(isNumberLengthValid("888888888888888"));
        assertTrue(isNumberLengthValid("9999999999999999"));

        // Positive double numbers
        assertTrue(isNumberLengthValid("0.1"));
        assertTrue(isNumberLengthValid("0.001"));
        assertTrue(isNumberLengthValid("0.166666666666667"));
        assertTrue(isNumberLengthValid("0.500000000000005"));
        assertTrue(isNumberLengthValid("87.9999999999995"));
        assertTrue(isNumberLengthValid("999999999999999.9"));

        // Negative numbers
        assertTrue(isNumberLengthValid("-1"));
        assertTrue(isNumberLengthValid("-1321321"));
        assertTrue(isNumberLengthValid("-5454545454545"));
        assertTrue(isNumberLengthValid("-888888888888888"));
        assertTrue(isNumberLengthValid("-9999999999999999"));
        assertTrue(isNumberLengthValid("-5555555555555555"));

        // Negative double numbers
        assertTrue(isNumberLengthValid("-0.1"));
        assertTrue(isNumberLengthValid("-0.001"));
        assertTrue(isNumberLengthValid("-0.166666666666667"));
        assertTrue(isNumberLengthValid("-0.500000000000005"));
        assertTrue(isNumberLengthValid("-87.9999999999995"));
        assertTrue(isNumberLengthValid("-999999999999999.9"));

        // Not valid number length
        // Empty strings
        assertFalse(isNumberLengthValid(null));
        assertFalse(isNumberLengthValid(""));

        // Positive numbers
        assertFalse(isNumberLengthValid("99999999999999991"));
        assertFalse(isNumberLengthValid("5656565656565656565656565"));

        // Positive double numbers
        assertFalse(isNumberLengthValid("56565.5666666666666"));
        assertFalse(isNumberLengthValid("9999999999999999.90"));

        // Negative numbers
        assertFalse(isNumberLengthValid("-55555555555555559"));
        assertFalse(isNumberLengthValid("-5555555555555555599999"));

        // Negative double numbers
        assertFalse(isNumberLengthValid("-9999999999999999.91"));
        assertFalse(isNumberLengthValid("-999999999999999.9199999999"));
    }

    @Test
    public void testIsZeroCheck() {
        // is zero
        assertTrue(isZero(new BigDecimal("0")));
        assertTrue(isZero(new BigDecimal("0.")));
        assertTrue(isZero(new BigDecimal("0.0")));
        assertTrue(isZero(new BigDecimal("0.0000")));
        assertTrue(isZero(new BigDecimal("0000")));

        // not zero
        assertFalse(isZero(new BigDecimal("0.1")));
        assertFalse(isZero(new BigDecimal("0.000000000000000000001")));
        assertFalse(isZero(new BigDecimal("-0.0000000000000000000001")));
        assertFalse(isZero(new BigDecimal("5")));
        assertFalse(isZero(new BigDecimal("-5")));
        assertFalse(isZero(new BigDecimal("9999999999999999")));
        assertFalse(isZero(new BigDecimal("-9999999999999999")));
    }

    @Test
    public void testWithExponent() {
        // need formatting to exponential view
        testExponentNeed("0.000000000000000099");
        testExponentNeed("0.00000000000000000000005");
        testExponentNeed("0.00099999999999999999");
        testExponentNeed("0.00000055632559999999");
        testExponentNeed("0.000016666666666667");
        testExponentNeed("-0.00001000005000009");
        testExponentNeed("-0.000000000000000099");
        testExponentNeed("-555555555555555599");
        testExponentNeed("-9999999999999999.91");
        testExponentNeed("10000000000000000");
        testExponentNeed("19999999999999999");
        testExponentNeed("-990000000000000111.00000001");
        testExponentNeed("165145454687943158");
        testExponentNeed("15151546479754244444444444.4444449");

        // no need formatting to exponential view
        testNoNeedExponent("-56565.56666666666666");
        testNoNeedExponent("0.01");
        testNoNeedExponent("-0.01");
        testNoNeedExponent("599");
        testNoNeedExponent("-599");
        testNoNeedExponent("9999999");
        testNoNeedExponent("-912930444");
        testNoNeedExponent("0");
        testNoNeedExponent("0.00");
        testNoNeedExponent("15555");
        testNoNeedExponent("9999999999");
        testNoNeedExponent("-1333.3");
        testNoNeedExponent("0.00001524");
        testNoNeedExponent("0.00005555525");
    }

    private void testExponentNeed(String stringValue) {
        BigDecimal number = new BigDecimal(stringValue);
        assertTrue(isExponentFormattingNeed(number));
    }

    private void testNoNeedExponent(String stringValue) {
        BigDecimal number = new BigDecimal(stringValue);
        assertFalse(isExponentFormattingNeed(number));
    }
}
