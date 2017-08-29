package com.implemica.CalculatorProject.model;

import com.implemica.CalculatorProject.model.calculation.CalculationExecutor;
import com.implemica.CalculatorProject.model.calculation.EditOperation;
import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.calculation.MemoryOperation;
import com.implemica.CalculatorProject.model.exception.CalculationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.implemica.CalculatorProject.model.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.model.validation.DataValidator.*;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;

/**
 * The class holds the components of the mathematical expression {@link BigDecimal} numbers and {@link MathOperation}s.
 * Grants functionality to input {@link BigDecimal} numbers, {@link MathOperation}s, {@link MemoryOperation}s and
 * {@link EditOperation}s and calculate the result for it.
 *
 * @author V. Kozina-Kravchenko
 */
public class Calculator {

    /**
     * An instance of {@link CalculationExecutor} implementation used for calculations.
     */
    private CalculationExecutor calculationExecutor;

    /**
     * The value of previous entered {@link BigDecimal} number or result of the last binary {@link MathOperation}.
     * By default it is zero.
     */
    private BigDecimal previousNumber = ZERO;

    /**
     * The last requested binary {@link MathOperation}.
     */
    private MathOperation operation;

    /**
     * The value of last entered {@link BigDecimal} number or result of the last unary {@link MathOperation}.
     * By default it is zero.
     */
    private BigDecimal lastNumber = ZERO;

    /**
     * The value of last memorized {@link BigDecimal} number. By default it is zero.
     */
    private BigDecimal memorizedNumber = ZERO;

    /**
     * The list of current expression parts: {@link BigDecimal} numbers and {@link MathOperation}s.
     */
    private final List<Object> expression = new ArrayList<>();

    /**
     * The value of temporary {@link BigDecimal} number stores last entered {@link BigDecimal} number before
     * calculation result. Used as argument for calculations with multiple callings of calculating result without
     * entering new numbers. Default value is zero.
     */
    private BigDecimal tempNumber = ZERO;

    /**
     * The flag variable shows is now entering a new number or continuing enter the last number.
     */
    private boolean isNewNumber = true;

    /**
     * The flag variable shows was previous {@link MathOperation} unary or binary.
     */
    private boolean wasUnaryBefore = false;

    /**
     * The flag variable shows the last symbol in current {@link BigDecimal} number is decimal point. Will be added to
     * the number with the first fraction digit.
     */
    private boolean needAddPoint = false;

    /**
     * The index of the last {@link BigDecimal} number added to the current expression.
     */
    private int indexOfLastNumberInExpression = 0;

    /**
     * An error message about requested operation not found.
     */
    private static final String NO_SUCH_OPERATION_FOUND = "No such operation found";

    /**
     * Sets the specified {@link CalculationExecutor} implementation.
     *
     * @param calculationExecutor a {@link CalculationExecutor} implementation to set
     */
    public void setCalculationExecutor(CalculationExecutor calculationExecutor) {
        this.calculationExecutor = calculationExecutor;
    }

    /**
     * Returns the last entered or modified after unary math operation number.
     *
     * @return the last entered or modified after unary math operation number
     */
    public BigDecimal getLastNumber() {
        return lastNumber;
    }

    /**
     * Returns the list of expression arguments represented by {@link BigDecimal} numbers and {@link MathOperation}s.
     *
     * @return the list of expression arguments represented by {@link BigDecimal} numbers and {@link MathOperation}s
     */
    public List<Object> getExpressionArguments() {
        return Collections.unmodifiableList(expression);
    }

    /**
     * Adds digit to the last entered {@link BigDecimal} number. Returns last entered {@link BigDecimal} number.
     *
     * @param digit to add to the last entered {@link BigDecimal} number
     * @return last entered {@link BigDecimal} number
     */
    public BigDecimal enterDigit(BigDecimal digit) {
        if (isNewNumber) {
            lastNumber = digit;
            removeLastUnaryFromExpression();
            needAddPoint = false;
        } else {
            appendDigit(digit);
        }
        isNewNumber = false;
        return lastNumber;
    }

    /**
     * Appends the given digit to the current {@link BigDecimal} number.
     *
     * @param digit a digit to append to the current {@link BigDecimal} number
     */
    private void appendDigit(BigDecimal digit) {
        if (isZero(lastNumber) && !isZero(digit) &&
                lastNumber.scale() == 0 && !needAddPoint) {
            // if current number is zero, it has no fraction part and adding decimal separator didn't called
            // than replace current number by specified non-zero digit
            lastNumber = digit;

        } else {
            appendDigitImpl(digit);
            needAddPoint = false;
        }
    }

    /**
     * Appends the given digit represented by {@link BigDecimal} number to the last entered {@link BigDecimal} number.
     *
     * @param digit a digit to append
     */
    private void appendDigitImpl(BigDecimal digit) {
        if (lastNumber.signum() == -1) {
            digit = digit.negate();
        }
        if (needAddPoint || lastNumber.scale() != 0) {
            // if called adding the decimal separator or current number already has fraction part
            int newScale = lastNumber.scale() + 1;
            BigDecimal tailToAdd = digit.divide(TEN.pow(newScale), newScale, RoundingMode.HALF_DOWN);
            lastNumber = lastNumber.add(tailToAdd);
        } else {
            lastNumber = lastNumber.multiply(TEN).add(digit);
        }
    }

    /**
     * Executes the specified {@link MathOperation}, writes it to the current expression. Returns last entered
     * {@link BigDecimal} number or result if needed for some {@link MathOperation}.
     *
     * @param currentOperation a {@link MathOperation} to execute
     * @return last entered {@link BigDecimal} number or result if needed for some {@link MathOperation}
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
     * Executes current called binary {@link MathOperation} and returns last entered {@link BigDecimal} number if it is the first
     * binary {@link MathOperation} in expression or result of previous binary {@link MathOperation}s. Also writes
     * to the current expression.
     *
     * @param currentOperation a current binary {@link MathOperation} to execute
     * @return last entered {@link BigDecimal} number if it is the first binary {@link MathOperation} in expression or result of
     * previous binary {@link MathOperation}s
     * @throws CalculationException if some error while calculations occurred
     */
    private BigDecimal executeBinaryOperation(MathOperation currentOperation) throws CalculationException {
        if (currentOperation == PERCENT) {
            return executePercentOperation();
        }

        if (operation != null && !wasUnaryBefore && isNewNumber && !expression.isEmpty()) {
            // if after last binary operation called new binary instead of entering number.
            // need to replace last operation and return previous result
            operation = currentOperation;
            replaceLastOperationInExpression();

            return getPreviousResult();
        }
        if (operation == null) {
            operation = currentOperation;
        }
        if (!wasUnaryBefore) {
            addToExpression(lastNumber);
        }
        addToExpression(currentOperation);
        updatePreviousNumber();
        operation = currentOperation;
        wasUnaryBefore = false;

        return previousNumber;
    }

    /**
     * Returns the last result of binary {@link MathOperation} or the last entered {@link BigDecimal} number if there
     * is only one binary {@link MathOperation} in expression.
     *
     * @return the last result of binary {@link MathOperation} or the last entered {@link BigDecimal} number if there
     * is only one binary {@link MathOperation} in expression
     */
    private BigDecimal getPreviousResult() {
        if (expression.isEmpty() || expression.size() == 2) {
            // if wasn't any calculations or added one number and one any operation
            return lastNumber;
        } else {
            lastNumber = previousNumber;
            return previousNumber;
        }
    }

    /**
     * Executes a {@link MathOperation#PERCENT} and returns the result of it.
     *
     * @return the result of {@link MathOperation#PERCENT}
     * @throws CalculationException if some error occurred while calculations
     */
    private BigDecimal executePercentOperation() throws CalculationException {
        lastNumber = getResult(PERCENT, previousNumber, lastNumber);
        updateExpressionAfterPercentage();
        wasUnaryBefore = true; // for expression percentage acts like unary operation

        return lastNumber;
    }

    /**
     * Updates an expression for {@link MathOperation#PERCENT} by adding or replacing the last unary argument.
     */
    private void updateExpressionAfterPercentage() {
        if (wasUnaryBefore) { // replace last unary operation in expression
            removeLastUnaryFromExpression();
        }
        addToExpression(lastNumber);
    }

    /**
     * Updates the value of previous entered {@link BigDecimal} number.
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
     * Returns the result of current called unary {@link MathOperation} with the last entered {@link BigDecimal} number.
     * Also writes to expression.
     *
     * @param currentOperation a current unary {@link MathOperation} to execute
     * @return the result of current called unary {@link MathOperation} with the last entered {@link BigDecimal} number
     * @throws CalculationException if some error while calculations occurred
     */
    private BigDecimal executeUnaryOperation(MathOperation currentOperation) throws CalculationException {
        if (currentOperation == NEGATE) {

            updateLastNumberAfterUnary(currentOperation);
            if (wasUnaryBefore) {
                updateExpressionForUnary(NEGATE);
                wasUnaryBefore = true;
            }
        } else {
            updateExpressionForUnary(currentOperation);
            updateLastNumberAfterUnary(currentOperation);

            wasUnaryBefore = true;
            isNewNumber = true;
        }
        return lastNumber;
    }

    /**
     * Updates the value of last entered {@link BigDecimal} number by replacing it with result of current unary
     * {@link MathOperation}.
     *
     * @param currentOperation an unary {@link MathOperation} to execute
     * @throws CalculationException if some error occurred while calculations
     */
    private void updateLastNumberAfterUnary(MathOperation currentOperation) throws CalculationException {
        if (wasUnaryBefore || operation == null || !isNewNumber) {
            // if wasn't binary operation before current unary operation applies to the last entered number
            lastNumber = getResult(currentOperation, lastNumber);
        } else { // if was binary operation before current unary operation applies to previous number that stores last binary result
            lastNumber = getResult(currentOperation, previousNumber);
        }
    }

    /**
     * Updates an expression after last unary {@link MathOperation} by adding the given unary {@link MathOperation}.
     * If this unary {@link MathOperation} is the first after binary adds last entered {@link BigDecimal} number before.
     *
     * @param currentOperation a current unary {@link MathOperation} to add to expression
     */
    private void updateExpressionForUnary(MathOperation currentOperation) {
        if (!wasUnaryBefore) { // if was binary operation before add last number that is the base for current unary operation
            addNumberToExpression();
        }
        addToExpression(currentOperation);
    }

    /**
     * Adds last entered {@link BigDecimal} number or last result of {@link MathOperation}s to the expression.
     */
    private void addNumberToExpression() {
        if (operation != null && expression.size() > 4) {
            // if binary operation called more than once. It means that result of the last binary operation was stored
            // in previous number variable
            addToExpression(previousNumber);
        } else {
            addToExpression(lastNumber);
        }
    }

    /**
     * Returns the result of calculations for the given {@link MathOperation} and {@link BigDecimal} number(s).
     *
     * @param operation a current {@link MathOperation} to execute
     * @param arguments the list of {@link BigDecimal} numbers
     * @return the result of calculations for the given {@link MathOperation} and {@link BigDecimal} number(s)
     * @throws CalculationException if some error while calculations occurred
     */
    private BigDecimal getResult(MathOperation operation, BigDecimal... arguments) throws CalculationException {

        BigDecimal result = calculationExecutor.calculate(operation, arguments);
        if (isZero(result)) {
            result = ZERO;
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

        if (wasUnaryBefore || operation != null) { // calculate result if any math operation executes
            calculateResultImpl();
        }
        return lastNumber;
    }

    /**
     * Calculates the result for the current {@link MathOperation} and entered {@link BigDecimal} numbers. The result
     * of calculations assigns to last entered {@link BigDecimal} number value.
     *
     * @throws CalculationException if some error while calculations occurred
     */
    private void calculateResultImpl() throws CalculationException {
        if (expression.isEmpty() && !wasUnaryBefore) { // If calculate result called without entering new number execute last operation
            lastNumber = getResult(operation, lastNumber, tempNumber);

        } else if (operation != null) {
            // If was binary operation, remember last number and execute this operation
            tempNumber = lastNumber;
            lastNumber = getResult(operation, previousNumber, lastNumber);
        }

        // If all operations were unary return result (last number) and reset all
        isNewNumber = true;
        expression.clear();
        wasUnaryBefore = false;
        needAddPoint = false;
    }

    /**
     * Adds a decimal point to the last entered {@link BigDecimal} number. Updates expression and resets last number
     * to {@link BigDecimal#ZERO} if point added after unary {@link MathOperation} without entering any digit.
     */
    public void addPoint() {
        if (isNewNumber) { // If point adds when expected entering new number need to replace last number by zero
            lastNumber = ZERO;
            isNewNumber = false;
            removeLastUnaryFromExpression();
        }

        BigDecimal reminder = lastNumber.remainder(BigDecimal.ONE);
        if (isZero(reminder) && lastNumber.scale() == 0) { // if number has no fraction part
            needAddPoint = true;
        }
    }

    /**
     * Resets all {@link BigDecimal}s and {@link MathOperation} fields to default values.
     */
    public void cleanAll() {
        cleanCurrent();
        previousNumber = ZERO;
        operation = null;
        expression.clear();
        wasUnaryBefore = false;
    }

    /**
     * Resets the last entered {@link BigDecimal} number to default value - zero.
     */
    public void cleanCurrent() {
        lastNumber = ZERO;
        removeLastUnaryFromExpression();
        isNewNumber = true;
        needAddPoint = false;
    }

    /**
     * Deletes last digit in the current entered {@link BigDecimal} number. Returns true if the last symbol in current
     * number is decimal point.
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
            lastNumber = ZERO;
            needAddPoint = false;
        } else {
            isLastSymbolPoint = deleteLastDigitImpl();
        }
        return isLastSymbolPoint;
    }

    /**
     * Deletes last digit in the current entered {@link BigDecimal} number. Returns true if the last symbol in current
     * number is decimal point.
     *
     * @return true if the last symbol in current number is decimal point
     */
    private boolean deleteLastDigitImpl() {
        boolean isLastSymbolPoint = false;
        int currentNumberScale = lastNumber.scale();
        if (currentNumberScale > 0) { // if number has fraction part

            lastNumber = lastNumber.setScale(currentNumberScale - 1, RoundingMode.DOWN);
            isLastSymbolPoint = checkIsLastSymbolPoint();
        } else {
            lastNumber = lastNumber.divide(TEN, 0, RoundingMode.DOWN);
        }
        return isLastSymbolPoint;
    }

    /**
     * Checks the current {@link MathOperation} number is the last symbol after deleting last digit becomes decimal separator.
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
     * Executes the given {@link MemoryOperation} with memorized number.
     *
     * @param operation a {@link MemoryOperation} to execute
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
                removeLastUnaryFromExpression();
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
     * Replaces last binary {@link MathOperation} in expression by the last specified one.
     */
    private void replaceLastOperationInExpression() {
        if (!expression.isEmpty()) {
            expression.set(expression.size() - 1, operation);
        }
    }

    /**
     * Adds the given {@link BigDecimal} number to the current mathematical expression. Also updates index of the
     * last added {@link BigDecimal} number to the expression.
     *
     * @param number a {@link BigDecimal} number to add into expression
     */
    private void addToExpression(BigDecimal number) {
        expression.add(number);
        indexOfLastNumberInExpression = expression.size() - 1;
    }

    /**
     * Adds the given {@link MathOperation} operation to the current expression.
     *
     * @param operation a given {@link MathOperation} to add into expression
     */
    private void addToExpression(MathOperation operation) {
        expression.add(operation);
    }

    /**
     * Removes last unary {@link MathOperation} from expression. This {@link MathOperation} starts with the last added
     * {@link BigDecimal} number and up to the last argument.
     */
    private void removeLastUnaryFromExpression() {
        if (wasUnaryBefore && indexOfLastNumberInExpression < expression.size()) {
            // if last math operation was unary and it wasn't removed yet,
            // because number added before this operation is still in expression
            for (int i = expression.size() - 1; i >= indexOfLastNumberInExpression; i--) {

                expression.remove(i);
            }
            wasUnaryBefore = false;
        }
    }
}
