package com.implemica.CalculatorProject.processing;

import com.implemica.CalculatorProject.calculation.MathOperation;
import com.implemica.CalculatorProject.calculation.MemoryOperation;
import com.implemica.CalculatorProject.exception.CalculationException;
import org.junit.After;
import org.junit.Test;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.calculation.MemoryOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.MINUS;
import static com.implemica.CalculatorProject.util.OutputFormatter.POINT;
import static com.implemica.CalculatorProject.util.OutputFormatter.formatUnaryOperation;
import static com.implemica.CalculatorProject.validation.DataValidator.isEmptyString;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InputValueProcessorTest {

    private InputValueProcessor processor = new InputValueProcessor();
    private static final String ARGUMENT_DELIMITERS = "\\s+[=%\\s]*";

    @After
    public void tearDown() {
        processor.cleanAll();
    }

    @Test
    public void testEnteringTooLongNumbers() throws CalculationException {
        // read initial value from text field
        testEnterDigit("0", "");

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

        // Enter number, delete last digit and check value
        enterNumber("1,234,567,890.123450");
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

        // Enter new number and check reset all
        testEnterDigit("0", "");
        testEnterDigit("0", "0");
        testEnterDigit("1", "1");
        testEnterDigit("12", "2");
        testEnterDigit("123", "3");
        testEnterDigit("1,234", "4");

        // Clean all
        processor.cleanAll();
        assertEquals("0", processor.getLastNumber());
        assertEquals("", processor.getExpression());
    }

    private void testEnterDigit(String expected, String digit) throws CalculationException {
        processor.updateCurrentNumber(digit);
        String updatedNumber = processor.getLastNumber();
        assertEquals(expected, updatedNumber);
    }

    @Test
    public void testBinaryOperations() throws CalculationException {

        // Binary operations
        // Add operation
        testBinaryOperation("2 + 3 = 5");
        testBinaryOperation("2.5 + 0 = 2.5");
        testBinaryOperation("9 + 0.999 = 9.999");
        testBinaryOperation("500005 + 5 = 500,010");
        testBinaryOperation("9999999999999999 + 1 = 1.e+16");
        testBinaryOperation("2 + -3 = -1");
        testBinaryOperation("-2.5 + 0 = -2.5");
        testBinaryOperation("-9 + 0.999 = -8.001");

        // Subtract operation
        testBinaryOperation("2 − 3 = -1");
        testBinaryOperation("2.5 − 0 = 2.5");
        testBinaryOperation("9 − 0.999 = 8.001");
        testBinaryOperation("500005 − 5 = 500,000");
        testBinaryOperation("9999999999999999 − 1 = 9,999,999,999,999,998");
        testBinaryOperation("2 − -3 = 5");
        testBinaryOperation("-2.5 − 0 = -2.5");
        testBinaryOperation("-9 − 0.999 = -9.999");

        // Multiply operation
        testBinaryOperation("2 ☓ 3 = 6");
        testBinaryOperation("2.5 ☓ 0 = 0");
        testBinaryOperation("9 ☓ 0.999 = 8.991");
        testBinaryOperation("500005 ☓ 5 = 2,500,025");
        testBinaryOperation("9999999999999999 ☓ 1 = 9,999,999,999,999,999");
        testBinaryOperation("2 ☓ -3 = -6");
        testBinaryOperation("-2.5 ☓ 0 = 0");
        testBinaryOperation("-9 ☓ 0.999 = -8.991");

        // Divide operation
        testBinaryOperation("2 ÷ 3 = 0.6666666666666667");
        testBinaryOperation("2.5 ÷ 10 = 0.25");
        testBinaryOperation("9 ÷ 0.999 = 9.009009009009009");
        testBinaryOperation("500005 ÷ 5 = 100,001");
        testBinaryOperation("9999999999999999 ÷ 1 = 9,999,999,999,999,999");
        testBinaryOperation("2 ÷ -3 = -0.6666666666666667");
        testBinaryOperation("-2.5 ÷ 10 = -0.25");
        testBinaryOperation("-9 ÷ 0.999 = -9.009009009009009");
    }

    private void testBinaryOperation(String expression) throws CalculationException {
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int index = 0;

        // Enter first number
        String firstNumber = expressionParts[index++];
        enterNumber(firstNumber);
        String historyExpression = firstNumber + " ";

        // Press operation
        MathOperation operation = MathOperation.getOperation(expressionParts[index++]);
        processor.executeMathOperation(operation);
        historyExpression += operation.getCode();
        // Test history expression. Expected expression is first number and operation.
        // Second number adds only if operation pressed one more time
        testHistoryExpression(historyExpression);

        // Enter second number, calculate and verify result
        enterNumber(expressionParts[index++]);
        assertEquals(expressionParts[index], processor.calculateResult());
    }

    private void enterNumber(String fullNumber) throws CalculationException {
        int i = 0;
        if (fullNumber.startsWith(MINUS)) { // skip minus
            i++;
        }

        for (; i < fullNumber.length(); i++) {
            String currentNumberPart = String.valueOf(fullNumber.charAt(i));
            updateNumber(currentNumberPart);
        }
        if (fullNumber.startsWith(MINUS)) { // add minus
            processor.executeMathOperation(NEGATE);
        }
    }

    private void updateNumber(String currentNumberPart) throws CalculationException {
        if (POINT.equals(currentNumberPart)) {
            processor.addPoint();
        } else {
            processor.updateCurrentNumber(currentNumberPart);
        }
    }

    private void testHistoryExpression(String expected) {
        assertEquals(expected, processor.getExpression().trim());
    }

    @Test
    public void testPercentage() throws CalculationException {
        // Expression format: firstNumber operation secondNumber % = expected result of all expression
        // add to base
        testPercentage("0 + 5 % = 0");
        testPercentage("5 + 0 % = 5");
        testPercentage("2 + 3 % = 2.06");
        testPercentage("2 + -3 % = 1.94");

        // subtract from base
        testPercentage("0 − 5 % = 0");
        testPercentage("5 − 0 % = 5");
        testPercentage("1616169 − 55 % = 727,276.05");
        testPercentage("1616169 − -55 % = 2,505,061.95");

        // multiply to base
        testPercentage("0 ☓ 5 % = 0");
        testPercentage("5 ☓ 0 % = 0");
        testPercentage("10 ☓ 5 % = 5");
        testPercentage("10 ☓ -5 % = -5");

        // divide to base
        testPercentage("10 ÷ 5 % = 20");
        testPercentage("10 ÷ -5 % = -20");
        testPercentage("5000 ÷ 0.999 % = 100.1001001001001");
        testPercentage("5000 ÷ -0.999 % = -100.1001001001001");
    }

    private void testPercentage(String expression) throws CalculationException {
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int index = 0;

        // Enter first number and press operation
        enterNumber(expressionParts[index++]);
        MathOperation operation = MathOperation.getOperation(expressionParts[index++]);
        processor.executeMathOperation(operation);

        // Enter second number, press percentage, calculate and verify result
        enterNumber(expressionParts[index++]);
        processor.executeMathOperation(PERCENT);
        assertEquals(expressionParts[index], processor.calculateResult());
    }

    @Test
    public void testUnaryOperation() throws CalculationException {

        // Expression format: enteredNumber = expected formatted result of unary operation
        // Negate
        testUnaryOperations("± 0 = 0");
        testUnaryOperations("± 0.25 = -0.25");
        testUnaryOperations("± 1 = -1");
        testUnaryOperations("± 9999999999999999 = -9,999,999,999,999,999");
        testUnaryOperations("± 0.6666666666666666 = -0.6666666666666666");
        testUnaryOperations("± -0.25 = 0.25");
        testUnaryOperations("± -1 = 1");
        testUnaryOperations("± -9999999999999999 = 9,999,999,999,999,999");

        testUnaryOperations("± √ 0 = 0");
        testUnaryOperations("± sqr 700 = 490,000");
        testUnaryOperations("± sqr 50 = 2,500");
        testUnaryOperations("± ± 55 = 55");
        testUnaryOperations("± 1/ 9999999999999999 = -0.00000001");

        // Square root
        testUnaryOperations("√ 0 = 0"); // TODO what's the difference between this tests and tests in StandardCalculatorTest? Answer - formatting resulted number and history expression
        testUnaryOperations("√ 0.25 = 0.5");
        testUnaryOperations("√ 1 = 1");
        testUnaryOperations("√ 999999999999999 = 31,622,776.60168378");
        testUnaryOperations("√ 0.6666666666666666 = 0.816496580927726");
        testUnaryOperations("√ 55555555 = 7,453.559887731499");
        testUnaryOperations("√ 100 = 10");
        testUnaryOperations("√ 250250 = 500.2499375312305");

        testUnaryOperations("√ √ 0 = 0");
        testUnaryOperations("√ sqr 700 = 700");
        testUnaryOperations("√ √ 50 = 2.659147948472494");
        testUnaryOperations("√ ± 55 = -7.416198487095663");
        testUnaryOperations("√ 1/ 9999999999999999 = 0.00000001");

        // Square
        testUnaryOperations("sqr 0 = 0");
        testUnaryOperations("sqr 0.25 = 0.0625");
        testUnaryOperations("sqr 1 = 1");
        testUnaryOperations("sqr 9999999999999999 = 9.999999999999998e+31");
        testUnaryOperations("sqr 0.666666666666666 = 0.4444444444444436");
        testUnaryOperations("sqr -0.25 = 0.0625");
        testUnaryOperations("sqr -1 = 1");
        testUnaryOperations("sqr -9999999999999999 = 9.999999999999998e+31");

        testUnaryOperations("sqr sqr 0 = 0");
        testUnaryOperations("sqr sqr 0.005 = 0.000000000625");
        testUnaryOperations("sqr √ 700 = 700");
        testUnaryOperations("sqr 1/ 55 = 3.305785123966942e-4");
        testUnaryOperations("sqr sqr 9999999999999999 = 9.999999999999996e+63");
        testUnaryOperations("sqr ± 55 = -3,025");


        // Reverse
        testUnaryOperations("1/ 0.1 = 10");
        testUnaryOperations("1/ 0.25 = 4");
        testUnaryOperations("1/ 1 = 1");
        testUnaryOperations("1/ 999999999999999 = 1.000000000000001e-15");
        testUnaryOperations("1/ 0.666666666666666 = 1.500000000000002");
        testUnaryOperations("1/ -0.25 = -4");
        testUnaryOperations("1/ -1 = -1");
        testUnaryOperations("1/ -999999999999999 = -1.000000000000001e-15");

        testUnaryOperations("1/ 1/ 5 = 5");
        testUnaryOperations("1/ 1/ -5 = -5");
        testUnaryOperations("1/ ± -5 = 0.2");
        testUnaryOperations("1/ √ 35 = 0.1690308509457034");
        testUnaryOperations("1/ sqr -5 = 0.04");
    }

    private void testUnaryOperations(String expression) throws CalculationException {
        processor.cleanAll();
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int index = expressionParts.length - 1;
        // last element is expected result and previous is base number for unary operations
        String expectedResult = expressionParts[index--];
        String base = expressionParts[index--];
        enterNumber(base);

        String history = "";
        // execute all unary operations
        for (int i = 0; i <= index; i++) {
            MathOperation operation = extractOperation(expressionParts[i]);

            if (operation != NEGATE && isEmptyString(history)) {
                history = base;
            }
            if (operation != NEGATE) {
                history = formatUnaryOperation(operation, history);
            }
            processor.executeMathOperation(operation);
        }
        testHistoryExpression(history);
        assertEquals(expectedResult, processor.calculateResult());
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

    @Test
    public void testBinaryOperationSequences() throws CalculationException {

        // Test case when in expression more than one binary operation
        // Expression format: sequence of entered numbers and operations = expected result
        testBinaryOperationSequence("-2 + 3 + 5 = 6");
        testBinaryOperationSequence("0 + 0 + 0 = 0");
        testBinaryOperationSequence("0 + 5 ÷ 500 = 0.01");
        testBinaryOperationSequence("2 + 3 + 5 = 10");
        testBinaryOperationSequence("5000 + -6 + 50 = 5,044");
        testBinaryOperationSequence("999999999999999 + 1 ÷ 2 = 500,000,000,000,000");
        testBinaryOperationSequence("9999999999999999 + 1 ÷ 2 = 5,000,000,000,000,000");

        testBinaryOperationSequence("-2 − 3 + 5 = 0");
        testBinaryOperationSequence("0 − 0 + 0 = 0");
        testBinaryOperationSequence("0 − 5 ÷ 500 = -0.01");
        testBinaryOperationSequence("2 − 3 + 5 = 4");
        testBinaryOperationSequence("5000 − -6 + 50 = 5,056");
        testBinaryOperationSequence("999999999999999 − 1 ☓ 2 = 1,999,999,999,999,996");
        testBinaryOperationSequence("9999999999999999 − 1 ÷ 2 = 4,999,999,999,999,999");

        testBinaryOperationSequence("-1000000000000000 ÷ 3 ☓ 3 = -1,000,000,000,000,000");
        testBinaryOperationSequence("-1000 ÷ 3 ☓ 3 = -1,000");
        testBinaryOperationSequence("0 ÷ 50 − 0 = 0");
        testBinaryOperationSequence("1 ÷ 3 ☓ 3 = 1");
        testBinaryOperationSequence("10 ÷ 3 ☓ 3 = 10");
        testBinaryOperationSequence("1000 ÷ 3 ☓ 3 = 1,000");
        testBinaryOperationSequence("5000 ÷ -6 + 50 = -783.3333333333333");

        testBinaryOperationSequence("-859999 ☓ 66 + 9999999 = -46,759,935");
        testBinaryOperationSequence("-1000 ☓ 3 ÷ 3 = -1,000");
        testBinaryOperationSequence("-2 ☓ 3 + 5 = -1");
        testBinaryOperationSequence("0 ☓ 0 − 1500 = -1,500");
        testBinaryOperationSequence("2 ☓ 3 + 5 = 11");
        testBinaryOperationSequence("90 ☓ 22222 ÷ -50 = -39,999.6");
        testBinaryOperationSequence("9999999999999999 ☓ 9999999999999999 ☓ 5 = 4.999999999999999e+32");
    }

    private void testBinaryOperationSequence(String expression) throws CalculationException {
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int index = 0;

        // Enter first number and press first operation
        enterNumber(expressionParts[index++]);
        MathOperation operation1 = MathOperation.getOperation(expressionParts[index++]);
        processor.executeMathOperation(operation1);

        // Enter second number, press second operation and enter next number
        enterNumber(expressionParts[index++]);
        MathOperation operation2 = MathOperation.getOperation(expressionParts[index++]);
        processor.executeMathOperation(operation2);
        enterNumber(expressionParts[index++]);

        // Test expression. Expected expression is first number, operation, second number and operation.
        testHistoryExpression(format("%s %s %s %s", expressionParts[0], expressionParts[1], expressionParts[2], expressionParts[3]).trim());

        //Calculate result
        assertEquals(expressionParts[index], processor.calculateResult());
    }

    @Test
    public void testUnaryOperationSequences() throws CalculationException {

        // When in expression more than one unary operation
        // Expression format: enteredNumber = expected result after executing all unary operations
        testUnaryOperations("sqr sqr 0 = 0");
        testUnaryOperations("sqr sqr 0.005 = 0.000000000625");
        testUnaryOperations("sqr √ 700 = 700");
        testUnaryOperations("sqr 1/ 55 = 3.305785123966942e-4");
        testUnaryOperations("sqr sqr 9999999999999999 = 9.999999999999996e+63");
        testUnaryOperations("sqr ± 55 = -3,025");

        testUnaryOperations("√ √ 0 = 0");
        testUnaryOperations("√ sqr 700 = 700");
        testUnaryOperations("√ √ 50 = 2.659147948472494");
        testUnaryOperations("√ ± 55 = -7.416198487095663");
        testUnaryOperations("√ 1/ 9999999999999999 = 0.00000001");

        testUnaryOperations("1/ 1/ 5 = 5");
        testUnaryOperations("1/ 1/ -5 = -5");
        testUnaryOperations("1/ ± -5 = 0.2");
        testUnaryOperations("1/ √ 35 = 0.1690308509457034");
        testUnaryOperations("1/ sqr -5 = 0.04");
    }

    @Test
    public void testPressingResultButton() throws CalculationException {
        // execute binary operation
        enterNumber("0.04");
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
    }

    private void testForException(String firstNumber, MathOperation operation, String secondNumber) {
        try {
            processor.cleanAll();
            enterNumber(firstNumber);
            processor.executeMathOperation(operation);
            enterNumber(secondNumber);
            processor.calculateResult();

            fail(format("Expected CalculationException with wrong arguments for operations. " +
                    "Your first number = %s, operation = %s, second number = %s", firstNumber, operation, secondNumber));
        } catch (CalculationException e) {
            // expected
        }
    }

    @Test
    public void testMemoryOperations() throws CalculationException {
        // add to memorized value, expression format: enteredNumber = expected number in memory after operation
        testMemoryOperation(MEMORY_ADD, "50 = 50");
        testMemoryOperation(MEMORY_ADD, "-3 = -3");
        testMemoryOperation(MEMORY_ADD, "0.555555 = 0.555555");
        testMemoryOperation(MEMORY_ADD, "-99999999999 = -99,999,999,999");

        // subtract from memorized value, expression format: enteredNumber = expected number in memory after operation
        testMemoryOperation(MEMORY_SUBTRACT, "50 = -50");
        testMemoryOperation(MEMORY_SUBTRACT, "-3 = 3");
        testMemoryOperation(MEMORY_SUBTRACT, "0.555555 = -0.555555");
        testMemoryOperation(MEMORY_SUBTRACT, "-99999999999 = 99,999,999,999");

        // store value in memorized, expression format: enteredNumber = expected number in memory after operation
        testMemoryOperation(MEMORY_STORE, "50 = 50");
        testMemoryOperation(MEMORY_STORE, "-3 = -3");
        testMemoryOperation(MEMORY_STORE, "0.555555 = 0.555555");
        testMemoryOperation(MEMORY_STORE, "-99999999999 = -99,999,999,999");
    }

    private void testMemoryOperation(MemoryOperation operation, String expression) throws CalculationException {
        String[] expressionParts = expression.trim().split(ARGUMENT_DELIMITERS);
        int index = 0;

        // clean memory, enter new number and execute memory operation
        processor.executeMemoryOperation(MEMORY_CLEAN);
        enterNumber(expressionParts[index++]);
        processor.executeMemoryOperation(operation);

        //reset entered number and check for 0 (default value of new number)
        processor.cleanCurrent();
        assertEquals("0", processor.getLastNumber());

        // recall memorized value that must be equal first entered number
        processor.executeMemoryOperation(MEMORY_RECALL);
        assertEquals(expressionParts[index], processor.getLastNumber());
    }
}
