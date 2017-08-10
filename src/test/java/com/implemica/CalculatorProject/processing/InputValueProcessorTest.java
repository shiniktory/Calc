package com.implemica.CalculatorProject.processing;

import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.After;
import org.junit.Test;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.*;
import static org.junit.Assert.assertEquals;

public class InputValueProcessorTest {
//
//    private final InputValueProcessor processor = new InputValueProcessor();
//
//    private static final String ARGUMENT_DELIMITERS = "\\s+[=\\s]*";
//
//    @After
//    public void tearDown() {
//        processor.cleanAll();
//    }
//
//    @Test
//    public void testEnteringTooLongNumbers() throws CalculationException {
//        // read initial value from text field
//        testEnterDigit("0", "");
//
//        // enter long number with max length and try to add more digits
//        enterNumber("9999999999999999");
//        testAddMoreDigits("9,999,999,999,999,999");
//
//        // negate number and try to add more digits
//        processor.executeMathOperation(NEGATE);
//        testAddMoreDigits("-9,999,999,999,999,999");
//        processor.cleanAll();
//
//        // enter double number less than one and try to add more digits
//        enterNumber("0.8888888888888888");
//        testAddMoreDigits("0.8888888888888888");
//
//        // try to add one more point
//        processor.addPoint();
//        testAddMoreDigits("0.8888888888888888");
//
//        // negate number and try to add more digits
//        processor.executeMathOperation(NEGATE);
//        testAddMoreDigits("-0.8888888888888888");
//        processor.cleanAll();
//
//        // enter double number greater than one and try to add more digits
//        enterNumber("1.888888888888888");
//        testAddMoreDigits("1.888888888888888");
//
//        // try to add one more point
//        processor.addPoint();
//        testAddMoreDigits("1.888888888888888");
//
//        // negate number and try to add more digits
//        processor.executeMathOperation(NEGATE);
//        testAddMoreDigits("-1.888888888888888");
//    }
//
//    private void testEnterDigit(String expected, String digit) throws CalculationException {
//        processor.updateCurrentNumber(digit);
//        String updatedNumber = processor.getLastNumber();
//        assertEquals(expected, updatedNumber);
//    }
//
//    private void testAddMoreDigits(String expectedNumber) throws CalculationException {
//        for (int i = 0; i < 9; i++) {
//            testEnterDigit(expectedNumber, String.valueOf(i));
//        }
//    }
//
//    @Test
//    public void testCleanOperations() throws CalculationException {
//
//        // Enter number, delete last digit and check value
//        enterNumber("1,234,567,890.123450");
//        testDeleteLastDigit("1,234,567,890.12345");
//        testDeleteLastDigit("1,234,567,890.1234");
//        testDeleteLastDigit("1,234,567,890.123");
//
//        // Reset number
//        processor.cleanCurrent();
//        testEnterDigit("0", "");
//
//        // Delete last digit in number that contains only one digit
//        testEnterDigit("5", "5");
//        testDeleteLastDigit("0");
//        testDeleteLastDigit("0");
//        processor.cleanCurrent();
//
//        // Delete last digit in number that contains minus and only one digit
//        enterNumber("-5");
//        testDeleteLastDigit("0");
//
//        // Enter new number and check reset all
//        enterNumber("1234");
//        processor.cleanAll();
//        assertEquals("0", processor.getLastNumber());
//        assertEquals("", processor.getExpression());
//    }
//
//    private void testDeleteLastDigit(String expectedNumber) throws CalculationException {
//        processor.deleteLastDigit();
//        assertEquals(expectedNumber, processor.getLastNumber());
//    }
//
//    private void enterNumber(String fullNumber) throws CalculationException {
//        int i = 0;
//        if (fullNumber.startsWith(MINUS)) { // skip minus
//            i++;
//        }
//
//        for (; i < fullNumber.length(); i++) {
//            String currentNumberPart = String.valueOf(fullNumber.charAt(i));
//            updateNumber(currentNumberPart);
//        }
//        if (fullNumber.startsWith(MINUS)) { // add minus
//            processor.executeMathOperation(NEGATE);
//        }
//    }
//
//    private void updateNumber(String currentNumberPart) throws CalculationException {
//        if (POINT.equals(currentNumberPart)) {
//            processor.addPoint();
//        } else {
//            processor.updateCurrentNumber(currentNumberPart);
//        }
//    }
}
