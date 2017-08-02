package com.implemica.CalculatorProject.processing;

import com.implemica.CalculatorProject.calculation.MathOperation;
import com.implemica.CalculatorProject.calculation.MemoryOperation;
import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.Test;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.calculation.MemoryOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.MINUS;
import static com.implemica.CalculatorProject.util.OutputFormatter.POINT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InputValueProcessorTest {

    private InputValueProcessor processor = new InputValueProcessor();

    @Test
    public void testEnteringTooLongNumbers() throws CalculationException {
        testEnterDigit("0", ""); // read initial value from text field

        // enter long number with max length and try to add more digits
        enterNumber("9999999999999999");
        testEnterDigit("9,999,999,999,999,999", "0");
        testEnterDigit("9,999,999,999,999,999", "1");
        testEnterDigit("9,999,999,999,999,999", "2");
        testEnterDigit("9,999,999,999,999,999", "3");

        // negate number and try to add more digits
        processor.executeMathOperation(NEGATE);
        testEnterDigit("-9,999,999,999,999,999", "0");
        testEnterDigit("-9,999,999,999,999,999", "1");
        testEnterDigit("-9,999,999,999,999,999", "2");
        testEnterDigit("-9,999,999,999,999,999", "3");

        processor.cleanAll();

        // enter double number less than one and try to add more digits
        enterNumber("0.8888888888888888");
        testEnterDigit("0.8888888888888888", "0");
        testEnterDigit("0.8888888888888888", "1");
        testEnterDigit("0.8888888888888888", "2");
        testEnterDigit("0.8888888888888888", "3");

        // try to add one more point
        processor.addPoint();
        testEnterDigit("0.8888888888888888", "0");
        testEnterDigit("0.8888888888888888", "1");
        testEnterDigit("0.8888888888888888", "2");
        testEnterDigit("0.8888888888888888", "3");

        // negate number and try to add more digits
        processor.executeMathOperation(NEGATE);
        testEnterDigit("-0.8888888888888888", "0");
        testEnterDigit("-0.8888888888888888", "1");
        testEnterDigit("-0.8888888888888888", "2");
        testEnterDigit("-0.8888888888888888", "3");

        processor.cleanAll();

        // enter double number greater than one and try to add more digits
        enterNumber("1.888888888888888");
        testEnterDigit("1.888888888888888", "0");
        testEnterDigit("1.888888888888888", "1");
        testEnterDigit("1.888888888888888", "2");
        testEnterDigit("1.888888888888888", "3");

        // try to add one more point
        processor.addPoint();
        testEnterDigit("1.888888888888888", "0");
        testEnterDigit("1.888888888888888", "1");
        testEnterDigit("1.888888888888888", "2");
        testEnterDigit("1.888888888888888", "3");

        // negate number and try to add more digits
        processor.executeMathOperation(NEGATE);
        testEnterDigit("-1.888888888888888", "0");
        testEnterDigit("-1.888888888888888", "1");
        testEnterDigit("-1.888888888888888", "2");
        testEnterDigit("-1.888888888888888", "3");
    }

    @Test
    public void testCleanOperations() throws CalculationException {

       enterNumber("1,234,567,890.123450");

        // Delete last digit
        processor.deleteLastDigit();
        testEnterDigit("1,234,567,890.12345", "");
        processor.deleteLastDigit();
        testEnterDigit("1,234,567,890.1234", "");
        processor.deleteLastDigit();
        testEnterDigit("1,234,567,890.123", "");

        // Reset number
        processor.cleanCurrent();
        testEnterDigit("0", "");

        // Delete last digit in number that contains only one digit
        testEnterDigit("5", "5");
        processor.deleteLastDigit();
        testEnterDigit("0", "");
        processor.deleteLastDigit();
        testEnterDigit("0", "");

        processor.cleanCurrent();

        // Delete last digit in number that contains minus and only one digit
        enterNumber("-5");
        processor.deleteLastDigit();
        testEnterDigit("0", "");

        // Reset all
        testEnterDigit("0", "");
        testEnterDigit("0", "0");
        testEnterDigit("1", "1");
        testEnterDigit("12", "2");
        testEnterDigit("123", "3");
        testEnterDigit("1,234", "4");
        processor.cleanAll();
        testEnterDigit("0", "");
    }

    private void testEnterDigit(String expected, String digit) throws CalculationException {
        processor.updateCurrentNumber(digit);
        assertEquals(expected, processor.getLastNumber());
    }

    @Test
    public void testBinaryOperations() throws CalculationException {

        // Binary operations
        // Add operation
        testBinary("5", "2", ADD, "3");
        testBinary("2.5", "2.5", ADD, "0");
        testBinary("9.999", "9", ADD, "0.999");
        testBinary("500,010", "500005", ADD, "5");
        testBinary("1.e+16", "9999999999999999", ADD, "1");
        testBinary("-1", "2", ADD, "-3");
        testBinary("-2.5", "-2.5", ADD, "0");
        testBinary("-8.001", "-9", ADD, "0.999");

        // Subtract operation
        testBinary("-1", "2", SUBTRACT, "3");
        testBinary("2.5", "2.5", SUBTRACT, "0");
        testBinary("8.001", "9", SUBTRACT, "0.999");
        testBinary("500,000", "500005", SUBTRACT, "5");
        testBinary("9,999,999,999,999,998", "9999999999999999", SUBTRACT, "1");
        testBinary("5", "2", SUBTRACT, "-3");
        testBinary("-2.5", "-2.5", SUBTRACT, "0");
        testBinary("-9.999", "-9", SUBTRACT, "0.999");

        // Multiply operation
        testBinary("6", "2", MULTIPLY, "3");
        testBinary("0", "2.5", MULTIPLY, "0");
        testBinary("8.991", "9", MULTIPLY, "0.999");
        testBinary("2,500,025", "500005", MULTIPLY, "5");
        testBinary("9,999,999,999,999,999", "9999999999999999", MULTIPLY, "1");
        testBinary("-6", "2", MULTIPLY, "-3");
        testBinary("0", "-2.5", MULTIPLY, "0");
        testBinary("-8.991", "-9", MULTIPLY, "0.999");

        // Divide operation
        testBinary("0.6666666666666667", "2", DIVIDE, "3");
        testBinary("0.25", "2.5", DIVIDE, "10");
        testBinary("9.009009009009009", "9", DIVIDE, "0.999");
        testBinary("100,001", "500005", DIVIDE, "5");
        testBinary("9,999,999,999,999,999", "9999999999999999", DIVIDE, "1");
        testBinary("-0.6666666666666667", "2", DIVIDE, "-3");
        testBinary("-0.25", "-2.5", DIVIDE, "10");
        testBinary("-9.009009009009009", "-9", DIVIDE, "0.999");

        // Percentage
        testPercentage("0", "0", ADD, "5");
        testPercentage("2.06", "2", ADD, "3");
        testPercentage("5", "10", MULTIPLY, "5");
        testPercentage("100.1001001001001", "5000", DIVIDE, "0.999");
        testPercentage("727,276.05", "1616169", SUBTRACT, "55");

        // Clean all
        processor.cleanAll();
        assertEquals("0", processor.getLastNumber());
        assertEquals("", processor.getExpression());
    }

    private void testBinary(String expected, String firstNumber, MathOperation operation, String secondNumber) throws CalculationException {
        // Enter first number
        enterNumber(firstNumber);
        // Press operation
        processor.executeMathOperation(operation);
        // Enter second number
        enterNumber(secondNumber);

        // Test expression. Expected expression is first number and operation.
        // Second number adds only if operation pressed one more time
        String expectedExpression = String.format("%s %s", firstNumber, operation.getCode()).trim();
        assertEquals(expectedExpression, processor.getExpression().trim());

        //Calculate result
        assertEquals(expected, processor.calculateResult());
    }

    private void enterNumber(String fullNumber) throws CalculationException {
        int i = 0;
        if (fullNumber.startsWith(MINUS)) { // skip minus
            i++;
        }

        for (; i < fullNumber.length(); i++) {
            String currentChar = String.valueOf(fullNumber.charAt(i));
            if (POINT.equals(currentChar)) {
                processor.addPoint();
            } else {
                processor.updateCurrentNumber(currentChar);
            }
        }
        if (fullNumber.startsWith(MINUS)) { // add minus
            processor.executeMathOperation(NEGATE);
        }
    }

    private void testPercentage(String expected, String firstNumber, MathOperation operation, String percents) throws CalculationException {
        // Enter first number
        enterNumber(firstNumber);
        // Press operation
        processor.executeMathOperation(operation);
        // Enter second number
        enterNumber(percents);
        // Press percentage
        processor.executeMathOperation(PERCENT);
        //Calculate result
        assertEquals(expected, processor.calculateResult());
    }

    @Test
    public void testUnaryOperation() throws CalculationException {
        // Negate
        testUnary("0", NEGATE, "0");
        testUnary("-0.25", NEGATE, "0.25");
        testUnary("-1", NEGATE, "1");
        testUnary("-9,999,999,999,999,999",  NEGATE,"9999999999999999");
        testUnary("-0.6666666666666666",  NEGATE,"0.6666666666666666");
        testUnary("0.25",  NEGATE,"-0.25");
        testUnary("1", NEGATE, "-1");
        testUnary("9,999,999,999,999,999", NEGATE, "-9999999999999999");

        // Square root
        testUnary("0", SQUARE_ROOT, "0");
        testUnary("0.5", SQUARE_ROOT, "0.25");
        testUnary("1", SQUARE_ROOT, "1");
        testUnary("31,622,776.60168378", SQUARE_ROOT, "999999999999999");
        testUnary("0.816496580927726", SQUARE_ROOT, "0.6666666666666666");
        testUnary("7,453.559887731499", SQUARE_ROOT, "55555555");
        testUnary("10", SQUARE_ROOT, "100");
        testUnary("500.2499375312305", SQUARE_ROOT, "250250");

        // Square
        testUnary("0", SQUARE, "0");
        testUnary("0.0625", SQUARE, "0.25");
        testUnary("1", SQUARE, "1");
        testUnary("9.999999999999998e+31", SQUARE, "9999999999999999");
        testUnary("0.4444444444444436", SQUARE, "0.666666666666666");
        testUnary("0.0625", SQUARE, "-0.25");
        testUnary("1", SQUARE, "-1");
        testUnary("9.999999999999998e+31", SQUARE, "-9999999999999999");

        // Reverse
        testUnary("10", REVERSE, "0.1");
        testUnary("4", REVERSE, "0.25");
        testUnary("1", REVERSE, "1");
        testUnary("1.000000000000001e-15", REVERSE, "999999999999999");
        testUnary("1.500000000000002", REVERSE, "0.666666666666666");
        testUnary("-4", REVERSE, "-0.25");
        testUnary("-1", REVERSE, "-1");
        testUnary("-1.000000000000001e-15", REVERSE, "-999999999999999");

        processor.cleanAll();
    }

    private void testUnary(String expected, MathOperation operation, String number) throws CalculationException {
        processor.cleanAll();
        // Enter number
        enterNumber(number);

        // Press operation and get result
        assertEquals(expected, processor.executeMathOperation(operation));
        assertEquals(expected, processor.getLastNumber());
    }

    @Test
    public void testOperationSequences() throws CalculationException {

        // test case when in expression more than one binary operation
        testBinaryOperationSequence("0", "0", ADD, "0", ADD, "0");
        testBinaryOperationSequence("10", "2", ADD, "3", ADD, "5");
        testBinaryOperationSequence("0.01", "0", ADD, "5", DIVIDE, "500");
        testBinaryOperationSequence("500,000,000,000,000", "999999999999999", ADD, "1", DIVIDE, "2");
        testBinaryOperationSequence("5,000,000,000,000,000", "9999999999999999", ADD, "1", DIVIDE, "2");

        testBinaryOperationSequence("0", "0", SUBTRACT, "0", ADD, "0");
        testBinaryOperationSequence("4", "2", SUBTRACT, "3", ADD, "5");
        testBinaryOperationSequence("-0.01", "0", SUBTRACT, "5", DIVIDE, "500");
        testBinaryOperationSequence("1,999,999,999,999,996", "999999999999999", SUBTRACT, "1", MULTIPLY, "2");
        testBinaryOperationSequence("4,999,999,999,999,999", "9999999999999999", SUBTRACT, "1", DIVIDE, "2");

        testBinaryOperationSequence("0", "0", DIVIDE, "50", SUBTRACT, "0");
        testBinaryOperationSequence("1", "1", DIVIDE, "3", MULTIPLY, "3");
        testBinaryOperationSequence("10", "10", DIVIDE, "3", MULTIPLY, "3");
        testBinaryOperationSequence("1,000", "1000", DIVIDE, "3", MULTIPLY, "3");
        testBinaryOperationSequence("-1,000", "-1000", DIVIDE, "3", MULTIPLY, "3");
        testBinaryOperationSequence("-1,000,000,000,000,000", "-1000000000000000", DIVIDE, "3", MULTIPLY, "3");
        testBinaryOperationSequence("-783.3333333333333", "5000", DIVIDE, "-6", ADD, "50");

        testBinaryOperationSequence("-1,500", "0", MULTIPLY, "0", SUBTRACT, "1500");
        testBinaryOperationSequence("-39,999.6", "90", MULTIPLY, "22222", DIVIDE, "-50");
        testBinaryOperationSequence("-46,759,935", "-859999", MULTIPLY, "66", ADD, "9999999");
        testBinaryOperationSequence("4.999999999999999e+32", "9999999999999999", MULTIPLY, "9999999999999999", MULTIPLY, "5");

        processor.cleanAll();

        // testViewPanel case when in expression more than one unary operation
        testUnaryOperationSequence("0", "0", SQUARE, SQUARE);
        testUnaryOperationSequence("0.000000000625", "0.005", SQUARE, SQUARE);
        testUnaryOperationSequence("0", "0", SQUARE, SQUARE);
        testUnaryOperationSequence("9.999999999999996e+63", "9999999999999999", SQUARE, SQUARE);
        testUnaryOperationSequence("-3,025", "55", SQUARE, NEGATE);
        testUnaryOperationSequence("0", "0", SQUARE_ROOT, SQUARE);
        testUnaryOperationSequence("-7.416198487095663", "55", SQUARE_ROOT, NEGATE);
        testUnaryOperationSequence("0.00000001", "9999999999999999", SQUARE_ROOT, REVERSE);
        testUnaryOperationSequence("5", "5", REVERSE, REVERSE);
        testUnaryOperationSequence("-5", "-5", REVERSE, REVERSE);
        testUnaryOperationSequence("0.1690308509457034", "35", REVERSE, SQUARE_ROOT);
        testUnaryOperationSequence("0.04", "-5", REVERSE, SQUARE);

        // After last unary add new number
        processor.executeMathOperation(ADD);
        enterNumber("5");
        assertEquals("5.04", processor.calculateResult());

        // Enter nothing and press "=" one more time
        assertEquals("10.04", processor.calculateResult());
        assertEquals("15.04", processor.calculateResult());

        processor.cleanAll();
        // Press "=" when nothing entered
        assertEquals("0", processor.calculateResult());

        // Enter first number, perform unary operation, press one binary operation,
        // change it to another binary operation and enter new number
        enterNumber("5");
        processor.executeMathOperation(SQUARE);
        processor.executeMathOperation(MULTIPLY);
        processor.executeMathOperation(ADD);
        enterNumber("10");
        assertEquals("35", processor.calculateResult());
    }

    private void testBinaryOperationSequence(String expected, String number1, MathOperation operation1, String number2, MathOperation operation2, String number3) throws CalculationException {
        // Enter first number
        enterNumber(number1);
        // Press operation
        processor.executeMathOperation(operation1);
        // Enter second number
        enterNumber(number2);
        processor.executeMathOperation(operation2);
        enterNumber(number3);

        // Test expression. Expected expression is first number, operation, second number operation.
        String expectedExpression = String.format("%s %s %s %s", number1, operation1.getCode(), number2, operation2.getCode()).trim();
        assertEquals(expectedExpression, processor.getExpression().trim());

        //Calculate result
        assertEquals(expected, processor.calculateResult());
    }

    private void testUnaryOperationSequence(String expected, String number, MathOperation operation1, MathOperation operation2) throws CalculationException {
        // Enter first number
        enterNumber(number);

        // Press operations
        processor.executeMathOperation(operation1);
        processor.executeMathOperation(operation2);

        //Calculate result
        assertEquals(expected, processor.calculateResult());
    }

    @Test
    public void testOperationsForExceptions() {
        // division by zero
        testForException("0", DIVIDE, "0");
        testForException("1", DIVIDE, "0");
        testForException("5", DIVIDE, "0");
        testForException("-5", DIVIDE, "0");
        testForException("0", REVERSE, "");

        // negative base for square root (second number is ignored)
        testForException("-0.999999999", SQUARE_ROOT, "");
        testForException("-1", SQUARE_ROOT, "");
        testForException("-50000000", SQUARE_ROOT, "");

        // invalid operation
        testForException("0", null, "0");
    }

    private void testForException(String firstNumber, MathOperation operation, String secondNumber) {
        try {
            processor.cleanAll();
            enterNumber(firstNumber);
            processor.executeMathOperation(operation);
            enterNumber(secondNumber);
            System.out.println(processor.calculateResult());
            fail(String.format("Expected CalculationException with wrong arguments for operations. " +
                    "Your first number = %s, operation = %s, second number = %s", firstNumber, operation, secondNumber));
        } catch (CalculationException e) {
            // expected
        }
    }

    @Test
    public void testMemoryOperations() throws CalculationException {
        // add to memorized value
        testMemoryOperation("50", "50", MEMORY_ADD);
        testMemoryOperation("-3", "-3", MEMORY_ADD);
        testMemoryOperation("0.555555", "0.555555", MEMORY_ADD);
        testMemoryOperation("-99,999,999,999", "-99999999999", MEMORY_ADD);

        // subtract from memorized value
        testMemoryOperation("-50", "50", MEMORY_SUBTRACT);
        testMemoryOperation("3", "-3", MEMORY_SUBTRACT);
        testMemoryOperation("-0.555555", "0.555555", MEMORY_SUBTRACT);
        testMemoryOperation("99,999,999,999", "-99999999999", MEMORY_SUBTRACT);

        // store value in memorized
        testMemoryOperation("50", "50", MEMORY_STORE);
        testMemoryOperation("-3", "-3", MEMORY_STORE);
        testMemoryOperation("0.555555", "0.555555", MEMORY_STORE);
        testMemoryOperation("-99,999,999,999", "-99999999999", MEMORY_STORE);

        // testViewPanel with null operation - expected no changes :
        // clean memory
        processor.executeMemoryOperation(MEMORY_CLEAN);
        // recall memorized value. expected zero as default
        processor.executeMemoryOperation(MEMORY_RECALL);
        assertEquals("0", processor.getLastNumber());
        // try to execute null operation
        processor.executeMemoryOperation(null);
        // recall memorized value. expected the same default zero
        processor.executeMemoryOperation(MEMORY_RECALL);
        assertEquals("0", processor.getLastNumber());
    }

    private void testMemoryOperation(String expected, String number, MemoryOperation operation) throws CalculationException {
        // clean memory
        processor.executeMemoryOperation(MEMORY_CLEAN);

        // enter the number
        enterNumber(number);

        // add this number to memory
        processor.executeMemoryOperation(operation);

        //reset current number and check for 0
        processor.cleanCurrent();
        assertEquals("0", processor.getLastNumber());

        // recall memorized value that must be equal 50
        processor.executeMemoryOperation(MEMORY_RECALL);
        assertEquals(expected, processor.getLastNumber());
    }
}
