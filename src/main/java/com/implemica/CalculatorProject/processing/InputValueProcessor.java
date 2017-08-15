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

/**
 * The {@code InputValueProcessor} class holds the components of the mathematical expression
 * such as number and operations. Grants functionality to input numbers, operations and calculate the
 * result for it.
 *
 * @author V. Kozina-Kravchenko
 */
public class InputValueProcessor {

    /**
     * The value of previous entered number represented by string. By default it is zero.
     */
    private String previousNumber = ZERO_VALUE;

    /**
     * The last requested binary operation.
     */
    private MathOperation operation;

    /**
     * The value of last entered number represented by string. By default it is zero.
     */
    private String lastNumber = ZERO_VALUE;

    /**
     * The value of last memorized value represented by string. By default it is zero.
     */
    private String memorizedNumber = ZERO_VALUE;

    /**
     * The list of expression parts (numbers and operations) used for history showing.
     */
    private final List<String> expression = new ArrayList<>();

    /**
     * The value of temporary number stores last entered number before calculation result. Used as argument
     * for calculations with multiple pressings result button. Default value is zero.
     */
    private String tempNumber = ZERO_VALUE;

    /**
     * The flag variable shows is entering a new number or continuing type previous number.
     */
    private boolean isNewNumber = true;

    /**
     * The flag variable shows was previous operation unary or binary.
     */
    private boolean wasUnaryBefore = false;

    /**
     * The string contains a zero value. Uses as default value for some fields with numbers.
     */
    public static final String ZERO_VALUE = "0";

    /**
     * An error message about requested operation not found.
     */
    private static final String NO_SUCH_OPERATION_FOUND = "No such operation found";

    /**
     * Returns the last entered number formatted with group delimiters represented by string.
     *
     * @return the last entered number formatted with group delimiters represented by string
     * @throws CalculationException if last entered number is not a number
     */
    public String getLastNumber() throws CalculationException {
        return addGroupDelimiters(lastNumber);
    }

    /**
     * Returns a string contains a mathematical expression used as history.
     *
     * @return a string contains a mathematical expression
     */
    public String getExpression() {
        StringBuilder builder = new StringBuilder();
        for (String expressionPart : expression) {
            builder.append(expressionPart).append(" ");
        }
        return builder.toString().trim().toLowerCase();
    }

    /**
     * Adds digit to the last entered number.
     *
     * @param digit to add to the last entered number
     */
    public void updateCurrentNumber(String digit) {
        if (!isDigit(digit)) {
            return;
        }
        if (isNewNumber) {
            lastNumber = digit;
            if (wasUnaryBefore) {
                expression.remove(expression.size() - 1);
                wasUnaryBefore = false;
            }
        } else {
            appendDigit(digit);
        }
        isNewNumber = false;
    }

    /**
     * Appends the given digit to the current number.
     *
     * @param digit a digit to append to the current number
     */
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

    /**
     * Executes the specified mathematical operation, writes it to history expression. Returns last entered number
     * or operation result if needed for some operations. Returning value is formatted appropriate way for displaying.
     *
     * @param currentOperation an operation to execute
     * @return last entered number or operation result if needed for some operations
     * @throws CalculationException if some error while calculations occurred
     */
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

    /**
     * Executes current called binary operation and returns last entered number if it is the first
     * binary operation in expression or result of previous binary operations. Also writes to history
     * expression.
     *
     * @param currentOperation a current binary mathematical operation to execute
     * @return last entered number if it is the first binary operation in expression or result of
     * previous binary operations
     * @throws CalculationException if some error while calculations occurred
     */
    private String executeBinaryOperation(MathOperation currentOperation) throws CalculationException {
        if (operation == null) {
            operation = currentOperation;
        }

        // If no new number entered need to change last operation to new
        if (isNewNumber && expression.size() > 1 && !wasUnaryBefore) {
            operation = currentOperation;
            expression.set(expression.size() - 1, currentOperation.symbol());
            wasUnaryBefore = false;
            return formatNumberForDisplaying(lastNumber);
        }

        if (!wasUnaryBefore) {
            expression.add(formatToMathView(lastNumber));
        }
        expression.add(currentOperation.symbol());
        if (expression.size() > 2) { // If was already added more than one number and binary operation performed
            previousNumber = getResult(operation, previousNumber, lastNumber);

        } else {
            previousNumber = formatToMathView(lastNumber);
        }
        operation = currentOperation;
        wasUnaryBefore = false;
        return formatNumberForDisplaying(previousNumber);
    }

    /**
     * Returns the result of current called unary mathematical operation with the last entered number.
     * Also writes to history expression.
     *
     * @param currentOperation a current unary mathematical operation to execute
     * @return the result of current called unary mathematical operation with the last entered number
     * @throws CalculationException if some error while calculations occurred
     */
    private String executeUnaryOperation(MathOperation currentOperation) throws CalculationException {
        if (wasUnaryBefore) {
            int lastIndex = expression.size() - 1;
            String lastUnary = expression.get(lastIndex);
            expression.set(lastIndex, formatUnaryOperation(currentOperation, lastUnary));
        } else {
            expression.add(formatUnaryOperation(currentOperation, formatToMathView(lastNumber)));
        }
        lastNumber = getResult(currentOperation, lastNumber);
        wasUnaryBefore = true;
        return formatNumberForDisplaying(lastNumber);
    }

    /**
     * Returns the result of calculations for the given mathematical operation and number(s) represented
     * by string(s).
     *
     * @param operation a current mathematical operation to execute
     * @param arguments the list of numbers represented by strings
     * @return the result of calculations for the given mathematical operation and number(s) represented
     * by string(s)
     * @throws CalculationException if some error while calculations occurred
     */
    private String getResult(MathOperation operation, String... arguments) throws CalculationException {
        Calculator calculator = new StandardCalculator(operation, getBigDecimalValues(arguments));
        BigDecimal result = calculator.calculate();
        return formatToMathView(result);
    }

    /**
     * Calculates and returns the result of calculations for the current mathematical expression
     * consists of entered numbers and mathematical operations.
     *
     * @return the result of calculations for the current mathematical expression
     * consists of entered numbers and mathematical operations
     * @throws CalculationException if some error while calculations occurred
     */
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

    /**
     * Adds a decimal point to the last entered number and returns this number modified.
     *
     * @return a last entered number with decimal point
     * @throws CalculationException
     */
    public String addPoint() throws CalculationException {

        if (isNewNumber) {
            updateCurrentNumber(ZERO_VALUE);
        } else {
            if (lastNumber.contains(POINT) && !lastNumber.endsWith(POINT)) {
                return formatNumberForDisplaying(lastNumber);
            }
        }
        isNewNumber = false;

        if (!lastNumber.contains(POINT)) {
            lastNumber += POINT;
            return formatNumberForDisplaying(lastNumber) + POINT;
        } else if (lastNumber.endsWith(POINT)){
            return formatNumberForDisplaying(lastNumber) + POINT;
        }
        return formatNumberForDisplaying(lastNumber);
    }

    /**
     * Resets all numbers and operation to default values.
     */
    public void cleanAll() {
        cleanCurrent();
        previousNumber = ZERO_VALUE;
        operation = null;
        expression.clear();
        wasUnaryBefore = false;
    }

    /**
     * Resets the last entered number to default value - zero.
     */
    public void cleanCurrent() {
        lastNumber = ZERO_VALUE;
        if (!expression.isEmpty() && wasUnaryBefore) {
            expression.remove(expression.size() - 1);
            wasUnaryBefore = false;
        }
        isNewNumber = true;
    }

    /**
     * Deletes last digit in the current entered number.
     *
     */
    public void deleteLastDigit() {
        if (lastNumber.length() == 1 ||
                lastNumber.length() == 2 && lastNumber.startsWith(MINUS)) {
            lastNumber = ZERO_VALUE;
        }
        if (lastNumber.length() > 1) {
            lastNumber = lastNumber.substring(0, lastNumber.length() - 1);
        }
    }

    /**
     * Executes the given operation with memorized number.
     *
     * @param operation an operation to execute
     * @throws CalculationException if some error while calculations occurred
     */
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
