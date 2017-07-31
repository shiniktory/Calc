package com.implemica.CalculatorProject.processing;

import com.implemica.CalculatorProject.calculation.Calculator;
import com.implemica.CalculatorProject.calculation.StandardCalculator;
import com.implemica.CalculatorProject.calculation.MathOperation;
import com.implemica.CalculatorProject.calculation.MemoryOperation;
import com.implemica.CalculatorProject.exception.CalculationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.*;
import static com.implemica.CalculatorProject.util.ValueTransformerUtil.getBigDecimalValues;
import static com.implemica.CalculatorProject.validation.DataValidator.*;

public class InputValueProcessor {

    private String previousNumber = ZERO_VALUE;
    private MathOperation operation;
    private String lastNumber = ZERO_VALUE;

    private String memorizedNumber = ZERO_VALUE;

    private List<String> expression = new ArrayList<>();

    private String tempNumber = ZERO_VALUE;

    private boolean isNewNumber = true;

    private boolean wasUnaryBefore = false;

    public static final String ZERO_VALUE = "0";

    private static final BigDecimal MAX_NUMBER = new BigDecimal("1.e+10000");
    private static final String OVERFLOW_ERROR = "Overflow";
    private static final String NO_SUCH_OPERATION_FOUND = "No such operation found";

    private Calculator calculator;


    public String getLastNumber() throws CalculationException {
        return addGroupDelimiters(lastNumber);
    }

    public String getExpression() {
        StringBuilder builder = new StringBuilder();
        for (String expressionPart : expression) {
            builder.append(expressionPart).append(" ");
        }

        return builder.toString().trim().toLowerCase();
    }

    public void updateCurrentNumber(String digit) {
        if (!isDigit(digit)) {
            return;
        }
        if (isNewNumber) {
            lastNumber = digit;
        } else {
            appendDigit(digit);
        }
        isNewNumber = false;
    }

    private void appendDigit(String digit) {
        if (!isNumberLengthValid(lastNumber + digit)) {
            return;
        }
        if (ZERO_VALUE.equals(lastNumber) && ZERO_VALUE.equals(digit)) {
            return;
        }
        if (!ZERO_VALUE.equals(lastNumber)) {
            lastNumber += digit;
        } else if (ZERO_VALUE.equals(lastNumber)) {
            lastNumber = digit;
        }
    }

    public String executeMathOperation(MathOperation currentOperation) throws CalculationException {
        if (currentOperation == null) {
            throw new CalculationException(NO_SUCH_OPERATION_FOUND);
        }
        if (currentOperation == PERCENT) {
            lastNumber = getResult(PERCENT, previousNumber, lastNumber);
            if (wasUnaryBefore && !expression.isEmpty()) {
                expression.set(expression.size() - 1, formatToMathView(lastNumber));
            } else {
                expression.add(formatToMathView(lastNumber));
            }
            wasUnaryBefore = true;
            return formatNumberForDisplaying(lastNumber);
        }
        if (currentOperation == NEGATE) {
            lastNumber = getResult(NEGATE, lastNumber);
            return formatNumberForDisplaying(lastNumber);
        }

        String operationResult;
        if (currentOperation.isBinary()) {
            operationResult = executeBinaryOperation(currentOperation);
        } else {
            operationResult = executeUnaryOperation(currentOperation);
        }
        isNewNumber = true;
        return operationResult;
    }

    private String executeBinaryOperation(MathOperation currentOperation) throws CalculationException {
        if (operation == null) {
            operation = currentOperation;
        }

        // If no new number entered need to change last operation to new
        if (isNewNumber && expression.size() > 1) {
            operation = currentOperation;
            expression.set(expression.size() - 1, currentOperation.getCode());
            wasUnaryBefore = false;
            return formatNumberForDisplaying(lastNumber);
        }

        if (!wasUnaryBefore) {
            expression.add(formatToMathView(lastNumber));
        }
        expression.add(currentOperation.getCode());
        if (expression.size() > 2) { // If was already added more than one number and binary operation performed
            previousNumber = getResult(operation, previousNumber, lastNumber);

        } else {
            previousNumber = formatToMathView(lastNumber);
        }
        operation = currentOperation;
        wasUnaryBefore = false;
        return formatNumberForDisplaying(previousNumber);
    }

    private String executeUnaryOperation(MathOperation currentOperation) throws CalculationException {
        if (wasUnaryBefore) {
            int lastIndex = expression.size() - 1;
            String lastUnary = expression.get(lastIndex);
            expression.set(lastIndex, formatUnaryOperation(currentOperation, lastUnary));
        } else {
            expression.add(formatUnaryOperation(currentOperation, lastNumber));
        }
        lastNumber = getResult(currentOperation, lastNumber);
        wasUnaryBefore = true;
        return formatNumberForDisplaying(lastNumber);
    }

    private String getResult(MathOperation operation, String... arguments) throws CalculationException {
        calculator = new StandardCalculator(operation, getBigDecimalValues(arguments));
        BigDecimal result = calculator.calculate();
        if (MAX_NUMBER.compareTo(result) <= 0) {
            throw new CalculationException(OVERFLOW_ERROR);
        }
        return formatToMathView(result);
    }

    public String calculateResult() throws CalculationException {
        if (!wasUnaryBefore && operation == null) { // If nothing entered nothing to calculate
            if (isEmptyString(lastNumber)) {
                return ZERO_VALUE;
            } else {
                return formatNumberForDisplaying(lastNumber);
            }
        }
        if (expression.isEmpty() && !wasUnaryBefore) { // If "=" pressed without entering new number perform last operation
            lastNumber = getResult(operation, lastNumber, tempNumber);
        } else if (operation != null) {
            // If binary operation pressed, remember last number
            tempNumber = String.valueOf(lastNumber);
            lastNumber = getResult(operation, previousNumber, lastNumber);
        } // If all operations were unary return result (last number) and reset all
        isNewNumber = true;
        expression.clear();
        wasUnaryBefore = false;
        return formatNumberForDisplaying(lastNumber);
    }

    public void addPoint() {
        if (lastNumber.contains(POINT)) {
            return;
        }
        lastNumber += POINT;
        isNewNumber = false;
    }

    public void cleanAll() {
        cleanCurrent();
        previousNumber = ZERO_VALUE;
        operation = null;
        expression.clear();
        wasUnaryBefore = false;
    }

    public void cleanCurrent() {
        lastNumber = ZERO_VALUE;
        isNewNumber = true;
    }

    public String deleteLastDigit() throws CalculationException {
        if (lastNumber.length() == 1) {
            lastNumber = ZERO_VALUE;
        }
        if (lastNumber.length() == 2 && lastNumber.startsWith(MINUS)) {
            lastNumber = ZERO_VALUE;
        }
        if (lastNumber.length() > 1) {
            lastNumber = lastNumber.substring(0, lastNumber.length() - 1);
        }
        return formatNumberForDisplaying(lastNumber);
    }


    public void executeMemoryOperation(MemoryOperation operation) throws CalculationException {
        if (operation == null) {
            return;
        }
        switch (operation) {
            case MEMORY_CLEAN:
                memorizedNumber = ZERO_VALUE;
                break;
            case MEMORY_RECALL:
                lastNumber = String.valueOf(memorizedNumber);
                break;
            case MEMORY_ADD:
                memorizedNumber = getResult(ADD, memorizedNumber, lastNumber);
                break;
            case MEMORY_SUBTRACT:
                memorizedNumber = getResult(SUBTRACT, memorizedNumber, lastNumber);
                break;
            case MEMORY_STORE:
                memorizedNumber = formatToMathView(lastNumber);
        }
        isNewNumber = true;
    }
}
