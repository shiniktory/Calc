package com.implemica.CalculatorProject.model;

import com.implemica.CalculatorProject.model.calculation.Calculator;
import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.calculation.MemoryOperation;
import com.implemica.CalculatorProject.model.calculation.StandardCalculator;
import com.implemica.CalculatorProject.model.exception.CalculationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.implemica.CalculatorProject.model.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.view.formatting.OutputFormatter.*;
import static com.implemica.CalculatorProject.model.validation.DataValidator.*;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;

/**
 * The {@code InputValueProcessor} class holds the components of the mathematical expression
 * such as number and operations. Grants functionality to input numbers, operations and calculate the
 * result for it.
 *
 * @author V. Kozina-Kravchenko
 */
public class InputValueProcessor {

    /**
     * An instance of {@link Calculator} implementation used for calculations.
     */
    private Calculator calculator;

    /**
     * The value of previous entered number represented by string. By default it is zero.
     */
    private BigDecimal previousNumber = ZERO;

    /**
     * The last requested binary operation.
     */
    private MathOperation operation;

    /**
     * The value of last entered number represented by string. By default it is zero.
     */
    private BigDecimal lastNumber = ZERO;

    /**
     * The value of last memorized value represented by string. By default it is zero.
     */
    private BigDecimal memorizedNumber = ZERO;

    /**
     * The list of expression parts (numbers and operations) used for history showing.
     */
    private final List<String> expression = new ArrayList<>();

    /**
     * The value of temporary number stores last entered number before calculation result. Used as argument
     * for calculations with multiple pressings result button. Default value is zero.
     */
    private BigDecimal tempNumber = ZERO;

    /**
     * The flag variable shows is entering a new number or continuing type previous number.
     */
    private boolean isNewNumber = true;

    /**
     * The flag variable shows was previous operation unary or binary.
     */
    private boolean wasUnaryBefore = false;

    /**
     * The flag variable shows the last symbol in current number decimal point. Will be added to the number
     * when the first fraction number will be added.
     */
    private boolean needAddPoint = false;

    private List hist2 = new ArrayList();

    private int lastNumberInHistoryIndex = 0;
    /**
     * An error message about requested operation not found.
     */
    private static final String NO_SUCH_OPERATION_FOUND = "No such operation found";

    /**
     * Constructs a new {@code InputValueProcessor} instance that uses the {@link StandardCalculator} for
     * calculations.
     */
    public InputValueProcessor() {
        this.calculator = new StandardCalculator();
    }

    /**
     * Constructs a new {@code InputValueProcessor} instance that uses the specified {@link Calculator} implementation
     * for calculations.
     */
    public InputValueProcessor(Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Sets the specified {@link Calculator} implementation.
     *
     * @param calculator a {@link Calculator} implementation to set
     */
    public void setCalculator(Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Returns the last entered number formatted with group delimiters represented by string.
     *
     * @return the last entered number formatted with group delimiters represented by string
     */
    public BigDecimal getLastNumber() {
        return lastNumber;
    }

    /**
     * Returns a string contains a mathematical expression used as history.
     *
     * @return a string contains a mathematical expression
     */
    public String getHistoryExpression() {
        StringBuilder builder = new StringBuilder();
        for (String expressionPart : expression) {
            builder.append(expressionPart).append(" ");
        }

        StringBuilder builder2 = new StringBuilder();
        for (Object expressionPart : hist2) {
            builder2.append(expressionPart).append(" ");
        }
        System.out.println(builder2.toString().trim());
        return builder.toString().trim().toLowerCase();
    }

    /**
     * Adds digit to the last entered number. Returns true is digit appended successfully.
     *
     * @param digit to add to the last entered number
     * @return true is digit appended successfully
     */
    public boolean enterDigit(BigDecimal digit) {
        boolean isDigitAdded;
        if (isNewNumber) {
            lastNumber = digit;
            removeLastUnaryFromHistory();
            needAddPoint = false;
            isDigitAdded = true;
        } else {
            isDigitAdded = appendDigit(digit);
        }
        isNewNumber = false;
        return isDigitAdded;
    }

    /**
     * Appends the given digit to the current number. Returns true is digit appended successfully.
     *
     * @param digit a digit to append to the current number
     * @return true is digit appended successfully
     */
    private boolean appendDigit(BigDecimal digit) {
        if (!isNumberLengthValid(lastNumber.toPlainString() + digit)) {
            return false;
        }
        if (isZero(lastNumber) && lastNumber.scale() == 0 && !isZero(digit) && !needAddPoint) { // TODO write comments
            lastNumber = digit;
        } else {
            appendDigitImpl(digit);
            needAddPoint = false;
        }
        return true;
    }

    /**
     * Appends the given digit to the last entered number.
     *
     * @param digit a digit to append
     */
    private void appendDigitImpl(BigDecimal digit) {
        int sign = lastNumber.signum();
        if (sign == -1) {
            digit = digit.multiply(BigDecimal.valueOf(sign));
        }
        if (needAddPoint || lastNumber.scale() != 0) {

            int newScale = lastNumber.scale() + 1;
            BigDecimal tailToAdd = digit.divide(TEN.pow(newScale), newScale, RoundingMode.HALF_DOWN);

            lastNumber = lastNumber.add(tailToAdd);

        } else {
            lastNumber = lastNumber.multiply(TEN).add(digit);
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
    public BigDecimal executeMathOperation(MathOperation currentOperation) throws CalculationException {
        if (currentOperation == null) {
            throw new CalculationException(NO_SUCH_OPERATION_FOUND);
        }

        BigDecimal operationResult;
        if (currentOperation.isBinary()) {
            operationResult = executeBinaryOperation(currentOperation);
            isNewNumber = true;
        } else {
            operationResult = executeUnaryOperation(currentOperation);
        }
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
    private BigDecimal executeBinaryOperation(MathOperation currentOperation) throws CalculationException {
        if (currentOperation == PERCENT) {
            return executePercentOperation();
        }
        if (operation == null) {
            operation = currentOperation;
        }

        if (isNewNumber && expression.size() > 1 && !wasUnaryBefore) {
            // if after last binary operation pressed new binary instead entering number.
            // need to replace this operation

            operation = currentOperation;
            replaceLastArgumentInHistory(currentOperation.symbol());
            hist2.remove(hist2.size() - 1);
            addToHistory(operation);
            if (expression.size() == 2) {
                return lastNumber;
            } else {
                lastNumber = previousNumber;
                return previousNumber;
            }
        }

        if (!wasUnaryBefore) {
            addToHistory(formatToMathView(lastNumber));
            addToHistory(lastNumber);
            lastNumberInHistoryIndex = hist2.size() -1;
        }
        addToHistory(currentOperation.symbol());
        addToHistory(operation);
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
    private BigDecimal executePercentOperation() throws CalculationException {
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
            removeLastUnaryFromHistory();
            addToHistory(lastNumber);
            lastNumberInHistoryIndex = hist2.size() -1;
        } else {
            addToHistory(formattedLastNumber);
            addToHistory(lastNumber);
            lastNumberInHistoryIndex = hist2.size() -1;
        }
    }

    /**
     * Updates the value of previous entered number.
     *
     * @throws CalculationException if some error while calculations occurred
     */
    private void updatePreviousNumber() throws CalculationException {
        // If was already entered more than one number and binary operation execute last binary operation
        if (expression.size() > 2) {
            previousNumber = getResult(operation, previousNumber, lastNumber);

        } else { // or store last entered number in previous to enter new number
            previousNumber = lastNumber;
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
    private BigDecimal executeUnaryOperation(MathOperation currentOperation) throws CalculationException {
        if (currentOperation == NEGATE) {
            if (wasUnaryBefore || operation == null || !isNewNumber) {
                lastNumber = getResult(currentOperation, lastNumber);
            } else {
                lastNumber = getResult(currentOperation, previousNumber);
            }
            if (wasUnaryBefore) {
                updateHistoryForUnary(NEGATE);
                addToHistory(NEGATE);
                wasUnaryBefore = true;
            }

        } else {
            updateHistoryForUnary(currentOperation);
            if (!wasUnaryBefore) {
                addToHistory(lastNumber);
                lastNumberInHistoryIndex = hist2.size() -1;
            }
            addToHistory(currentOperation);
            if (wasUnaryBefore || operation == null || !isNewNumber) {
                lastNumber = getResult(currentOperation, lastNumber);
            } else {
                lastNumber = getResult(currentOperation, previousNumber);
            }
            wasUnaryBefore = true;
            isNewNumber = true;
        }
        return lastNumber;
    }

    /**
     * Updates a history expression after last unary operation by replacing the last unary argument or adding a
     * new argument.
     *
     * @param currentOperation a current unary operation to add to history
     */
    private void updateHistoryForUnary(MathOperation currentOperation) {
        if (wasUnaryBefore) {
            String lastUnary = expression.get(expression.size() - 1);
            String formattedCurrentUnary = formatUnaryOperation(currentOperation, lastUnary);
            replaceLastArgumentInHistory(formattedCurrentUnary);

        } else if (operation == null || !isNewNumber) {
            String formattedLastNumber = formatToMathView(lastNumber);
            String formattedCurrentUnary = formatUnaryOperation(currentOperation, formattedLastNumber);
            addToHistory(formattedCurrentUnary);

        } else {
            String formattedLastNumber = formatToMathView(previousNumber);
            String formattedCurrentUnary = formatUnaryOperation(currentOperation, formattedLastNumber);
            addToHistory(formattedCurrentUnary);
        }
    }

    /**
     * Returns the result of calculations for the given mathematical operation and number(s).
     *
     * @param operation a current mathematical operation to execute
     * @param arguments the list of numbers
     * @return the result of calculations for the given mathematical operation and number(s)
     * @throws CalculationException if some error while calculations occurred
     */
    private BigDecimal getResult(MathOperation operation, BigDecimal... arguments) throws CalculationException {
        BigDecimal result = calculator.calculate(operation, arguments);

        if (isZero(result)) {
            result = ZERO.setScale(0, RoundingMode.HALF_UP);
        }
        return result;
    }

    /**
     * Calculates and returns the result of calculations for the current mathematical expression
     * consists of entered numbers and mathematical operations.
     *
     * @return the result of calculations for the current mathematical expression
     * consists of entered numbers and mathematical operations
     * @throws CalculationException if some error while calculations occurred
     */
    public BigDecimal calculateResult() throws CalculationException {

        if (wasUnaryBefore || operation != null) {
            calculateResultImpl();
        } // If nothing entered nothing to calculate
        return lastNumber;
    }

    /**
     * Calculates the result for the current mathematical expression consists of entered numbers and mathematical
     * operations. The result of calculations assigns to last entered number value.
     *
     * @throws CalculationException if some error while calculations occurred or result is out of valid bounds
     */
    private void calculateResultImpl() throws CalculationException {
        if (expression.isEmpty() && !wasUnaryBefore) { // If "=" pressed without entering new number perform last operation
            lastNumber = getResult(operation, lastNumber, tempNumber);

        } else if (operation != null) {
            // If was binary operation, remember last number and execute this operation
            tempNumber = lastNumber;
            lastNumber = getResult(operation, previousNumber, lastNumber);
        }

        // If all operations were unary return result (last number) and reset all
        isNewNumber = true;
        expression.clear();
        hist2.clear();
        wasUnaryBefore = false;
        needAddPoint = false;
    }

    /**
     * Adds a decimal point to the last entered number. Updates history and resets last number to zero if point
     * pressed after unary operation.
     */
    public void addPoint() {

        if (isNewNumber) { // If point pressed when expected entering new number need to replace last number by zero
            lastNumber = ZERO;
            isNewNumber = false;
            removeLastUnaryFromHistory();
        }

        BigDecimal reminder = lastNumber.remainder(BigDecimal.ONE);
        if (isZero(reminder) && lastNumber.scale() == 0) { // if number has no fraction part
            needAddPoint = true;
        }
    }

    /**
     * Resets all numbers and operation to default values.
     */
    public void cleanAll() {
        cleanCurrent();
        previousNumber = ZERO;
        operation = null;
        expression.clear();
        wasUnaryBefore = false;
    }

    /**
     * Resets the last entered number to default value - zero.
     */
    public void cleanCurrent() {
        lastNumber = ZERO;
        removeLastUnaryFromHistory();
        isNewNumber = true;
        needAddPoint = false;

    }

    /**
     * Deletes last digit in the current entered number. Returns true if the last symbol in current number is
     * decimal point.
     *
     * @return true if the last symbol in current number is decimal point
     */
    public boolean deleteLastDigit() {
        String lastNumberStr = lastNumber.toPlainString();
        boolean isLastSymbolPoint = false;

        if (needAddPoint) { // if the last symbol in number is decimal separator
            isLastSymbolPoint = false;
            needAddPoint = false;

        } else if (lastNumberStr.length() == 1 ||
                lastNumberStr.length() == 2 && lastNumber.signum() == -1) {
            // if number consists of only one digit with or without minus sign

            lastNumber = ZERO.setScale(0, RoundingMode.HALF_UP);
            needAddPoint = false;

        } else {
            isLastSymbolPoint = deleteLastDigitImpl();
        }

        return isLastSymbolPoint;
    }

    /**
     * Deletes last digit in the current entered number. Returns true if the last symbol in current number is
     * decimal point.
     *
     * @return true if the last symbol in current number is decimal point
     */
    private boolean deleteLastDigitImpl() {
        boolean isLastSymbolPoint = false;
        int currentNumberScale = lastNumber.scale();
        if (currentNumberScale > 0) { // if number has fraction part

            lastNumber = lastNumber.setScale(currentNumberScale - 1, RoundingMode.HALF_DOWN);
            isLastSymbolPoint = checkIsLastSymbolPoint();
        } else {
            lastNumber = lastNumber.divide(TEN, 0, RoundingMode.HALF_DOWN);
        }
        return isLastSymbolPoint;

    }

    /**
     * Checks the current number is the last symbol after deleting last digit becomes decimal separator.
     *
     * @return true if the last symbol after deleting last digit becomes decimal separator
     */
    private boolean checkIsLastSymbolPoint() {
        boolean isLastSymbolPoint = false;
        if (lastNumber.scale() == 0) {

            isLastSymbolPoint = true;
            needAddPoint = true;
        }
        return isLastSymbolPoint;
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
                memorizedNumber = ZERO;
                break;
            case MEMORY_RECALL:
                lastNumber = memorizedNumber;
                removeLastUnaryFromHistory();
                break;
            case MEMORY_ADD:
                memorizedNumber = getResult(ADD, memorizedNumber, lastNumber);
                break;
            case MEMORY_SUBTRACT:
                memorizedNumber = getResult(SUBTRACT, memorizedNumber, lastNumber);
                break;
            case MEMORY_STORE:
                memorizedNumber = lastNumber;
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
     * Adds the given string with argument to history expression.
     *
     * @param argument a string with argument to add into history
     */
    private void addToHistory(Number argument) {
        hist2.add(argument);
    }
    private void addToHistory(MathOperation argument) {
        hist2.add(argument);
    }

    /**
     * Removes last element from history expression.
     */
    private void removeLastUnaryFromHistory() {
        if (wasUnaryBefore) {
            expression.remove(expression.size() - 1);

            for (int i = lastNumberInHistoryIndex; i < hist2.size(); i++) {
                hist2.remove(i);
            }

            wasUnaryBefore = false;
        }
    }
}
