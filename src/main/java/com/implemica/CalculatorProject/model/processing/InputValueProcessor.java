package com.implemica.CalculatorProject.model.processing;

import com.implemica.CalculatorProject.model.calculation.Calculator;
import com.implemica.CalculatorProject.model.calculation.StandardCalculator;
import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.calculation.MemoryOperation;
import com.implemica.CalculatorProject.model.exception.CalculationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.implemica.CalculatorProject.model.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.model.util.OutputFormatter.*;
import static com.implemica.CalculatorProject.model.util.ValueTransformerUtil.getBigDecimalValues;
import static com.implemica.CalculatorProject.model.validation.DataValidator.*;

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
     * An error message about number's value is too large or too small.
     */
    private static final String OVERFLOW_ERROR = "Overflow";

    /**
     * Returns the last entered number formatted with group delimiters represented by string.
     *
     * @return the last entered number formatted with group delimiters represented by string
     * @throws CalculationException if last entered value is not a number
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
            removeLastUnaryFromHistory();
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
        if (ZERO_VALUE.equals(lastNumber) && !ZERO_VALUE.equals(digit)) {
            lastNumber = digit;

        } else if (!ZERO_VALUE.equals(lastNumber)) {
            lastNumber += digit;
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

        String operationResult;
        if (currentOperation.isBinary()) {
            operationResult = executeBinaryOperation(currentOperation);
            isNewNumber = true;
        } else {
            operationResult = executeUnaryOperation(currentOperation);
        }
        checkResultForOverflow(operationResult);
        return formatNumberForDisplaying(operationResult);
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
        if (currentOperation == PERCENT) {
            return executePercentOperation();
        }
        if (operation == null) {
            operation = currentOperation;
        }

        if (!wasUnaryBefore) {
            // if after last binary operation pressed new binary instead entering number. need to replace this operation
            if (isNewNumber && expression.size() > 1) {
                operation = currentOperation;
                replaceLastArgumentInHistory(currentOperation.symbol());

                return lastNumber;
            }
            addToHistory(formatToMathView(lastNumber));
        }
        addToHistory(currentOperation.symbol());
        updatePreviousNumber();
        operation = currentOperation;
        wasUnaryBefore = false;

        return previousNumber;
    }

    /**
     * Executes a percentage operation and returns the result of it.
     *
     * @return the result of percentage operation
     * @throws CalculationException if some error occurred while calculations
     */
    private String executePercentOperation() throws CalculationException {
        lastNumber = getResult(PERCENT, previousNumber, lastNumber);
        updateHistoryForPercentage();
        wasUnaryBefore = true; // for history expression percentage formats like unary operation

        return lastNumber;
    }

    /**
     * Updates a history expression for percentage operation by adding or replacing the last argument.
     *
     * @throws CalculationException if last entered value is not a number
     */
    private void updateHistoryForPercentage() throws CalculationException {
        String formattedLastNumber = formatToMathView(lastNumber);
        if (wasUnaryBefore && !expression.isEmpty()) { // replace last unary operation in history expression
            replaceLastArgumentInHistory(formattedLastNumber);
        } else {
            addToHistory(formattedLastNumber);
        }
    }

    /**
     * Updates the value of previous entered number.
     *
     * @throws CalculationException
     */
    private void updatePreviousNumber() throws CalculationException {
        // If was already entered more than one number and binary operation execute last binary operation
        if (expression.size() > 2) {
            previousNumber = getResult(operation, previousNumber, lastNumber);

        } else { // or store last entered number in previous to enter new number
            previousNumber = formatToMathView(lastNumber);
        }
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
        if (currentOperation == NEGATE) {
            lastNumber = getResult(NEGATE, lastNumber);
            if (wasUnaryBefore){
                updateHistoryForUnary(NEGATE);
            }
            return lastNumber;
        }
        updateHistoryForUnary(currentOperation);
        lastNumber = getResult(currentOperation, lastNumber);
        wasUnaryBefore = true;
        isNewNumber = true;
        return lastNumber;
    }

    /**
     * Updates a history expression after last unary operation by replacing the last unary argument or adding a
     * new argument.
     *
     * @param currentOperation a current unary operation to add to history
     * @throws CalculationException
     */
    private void updateHistoryForUnary(MathOperation currentOperation) throws CalculationException {
        if (wasUnaryBefore) {
            String lastUnary = expression.get(expression.size() - 1);
            String formattedCurrentUnary = formatUnaryOperation(currentOperation, lastUnary);
            replaceLastArgumentInHistory(formattedCurrentUnary);

        } else {
            String formattedLastNumber = formatToMathView(lastNumber);
            String formattedCurrentUnary = formatUnaryOperation(currentOperation, formattedLastNumber);
            addToHistory(formattedCurrentUnary);
        }
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
     * Checks the given number as result of an operations for overflow.
     *
     * @param result the number to check for overflow
     * @throws CalculationException if result is out of valid bounds
     */
    private void checkResultForOverflow(String result) throws CalculationException {
        BigDecimal resultValue = new BigDecimal(result);
        if (isResultOverflow(resultValue)) {
            throw new CalculationException(OVERFLOW_ERROR);
        }
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
            return formatNumberForDisplaying(lastNumber);
        }

        if (expression.isEmpty() && !wasUnaryBefore) { // If "=" pressed without entering new number perform last operation
            lastNumber = getResult(operation, lastNumber, tempNumber);
        } else if (operation != null) {
            // If was binary operation, remember last number and execute this operation
            tempNumber = String.valueOf(lastNumber);
            lastNumber = getResult(operation, previousNumber, lastNumber);
        }

        // If all operations were unary return result (last number) and reset all
        checkResultForOverflow(lastNumber);
        isNewNumber = true;
        expression.clear();
        wasUnaryBefore = false;
        return formatNumberForDisplaying(lastNumber);
    }

    /**
     * Adds a decimal point to the last entered number. Updates history and resets last number to zero if point
     * pressed after unary operation.
     */
    public void addPoint() {

        if (isNewNumber) { // If point pressed when expected entering new number need to replace last number by zero
            updateCurrentNumber(ZERO_VALUE);
            isNewNumber = false;
            removeLastUnaryFromHistory();
        }

        if (!lastNumber.contains(POINT)) {
            lastNumber += POINT;
        }
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
        removeLastUnaryFromHistory();
        isNewNumber = true;
    }

    /**
     * Deletes last digit in the current entered number.
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
     * @param operation a memory operation to execute
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

    /**
     * Replaces last element in history expression by the given one.
     *
     * @param replacement a string value to replace last element in history by
     */
    private void replaceLastArgumentInHistory(String replacement) {
        expression.set(expression.size() - 1, replacement);
    }

    /**
     * Adds the given string with argument to history expression.
     *
     * @param argument a string with argument to add into history
     */
    private void addToHistory(String argument) {
        expression.add(argument);
    }

    /**
     * Removes last element from history expression.
     */
    private void removeLastUnaryFromHistory() {
        if (wasUnaryBefore) {
            expression.remove(expression.size() - 1);
            wasUnaryBefore = false;
        }
    }
}
