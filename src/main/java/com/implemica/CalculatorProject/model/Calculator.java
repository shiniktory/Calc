package com.implemica.CalculatorProject.model;

import com.implemica.CalculatorProject.model.calculation.CalculationExecutor;
import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.calculation.MemoryOperation;
import com.implemica.CalculatorProject.model.calculation.StandardCalculationExecutor;
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
 * The {@code InputValueProcessor} class holds the components of the mathematical expression
 * such as number and operations. Grants functionality to input numbers, operations and calculate the
 * result for it.
 *
 * @author V. Kozina-Kravchenko
 */
public class Calculator {

    /**
     * An instance of {@link CalculationExecutor} implementation used for calculations.
     */
    private CalculationExecutor calculationExecutor;

    /**
     * The value of previous entered number or result of the last binary operation. By default it is zero.
     */
    private BigDecimal previousNumber = ZERO;

    /**
     * The last requested binary operation.
     */
    private MathOperation operation;

    /**
     * The value of last entered number or result of the last unary operation. By default it is zero.
     */
    private BigDecimal lastNumber = ZERO;

    /**
     * The value of last memorized number. By default it is zero.
     */
    private BigDecimal memorizedNumber = ZERO;

    /**
     * The list of current expression parts (numbers and math operations).
     */
    private final List expression = new ArrayList<>();

    /**
     * The value of temporary number stores last entered number before calculation result. Used as argument
     * for calculations with multiple callings of calculating result without entering new numbers. Default value is zero.
     */
    private BigDecimal tempNumber = ZERO;

    /**
     * The flag variable shows is entering a new number or continuing type the last number.
     */
    private boolean isNewNumber = true;

    /**
     * The flag variable shows was previous operation unary or binary.
     */
    private boolean wasUnaryBefore = false;

    /**
     * The flag variable shows the last symbol in current number is decimal point. Will be added to the number
     * with the first fraction number.
     */
    private boolean needAddPoint = false;

    /**
     * The index of the last number added to the current expression.
     */
    private int indexOfLastNumberInExpression = 0;

    /**
     * An error message about requested operation not found.
     */
    private static final String NO_SUCH_OPERATION_FOUND = "No such operation found";

    /**
     * Constructs a new {@code InputValueProcessor} instance that uses the {@link StandardCalculationExecutor} for
     * calculations.
     */
    public Calculator() {
        this.calculationExecutor = new StandardCalculationExecutor();
    }

    /**
     * Constructs a new {@code InputValueProcessor} instance that uses the specified {@link CalculationExecutor} implementation
     * for calculations.
     */
    public Calculator(CalculationExecutor calculationExecutor) {
        this.calculationExecutor = calculationExecutor;
    }

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
    public List getExpressionArguments() {
        return Collections.unmodifiableList(expression);
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
            removeLastUnaryFromExpression();
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
        if (isZero(lastNumber) && !isZero(digit) &&
                lastNumber.scale() == 0 && !needAddPoint) {
            // if current number is zero, it has no fraction part and adding decimal separator didn't called
            // than replace current number by specified non-zero digit
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
     * Executes the specified mathematical operation, writes it to the current expression. Returns last entered number
     * or operation result if needed for some operations.
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
     * binary operation in expression or result of previous binary operations. Also writes to the current expression.
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
     * Returns the last result of binary operation or the last entered number if there is only one binary operation
     * in expression.
     *
     * @return the last result of binary operation or the last entered number if there is only one binary operation
     * in expression
     */
    private BigDecimal getPreviousResult() {
        if (expression.isEmpty() || expression.size() == 2) {
            return lastNumber;
        } else {
            lastNumber = previousNumber;
            return previousNumber;
        }
    }

    /**
     * Executes a percentage operation and returns the result of it.
     *
     * @return the result of percentage operation
     * @throws CalculationException if some error occurred while calculations
     */
    private BigDecimal executePercentOperation() throws CalculationException {
        lastNumber = getResult(PERCENT, previousNumber, lastNumber);
        updateExpressionAfterPercentage();
        wasUnaryBefore = true; // for expression percentage acts like unary operation

        return lastNumber;
    }

    /**
     * Updates an expression for percentage operation by adding or replacing the last unary argument.
     */
    private void updateExpressionAfterPercentage() {
        if (wasUnaryBefore) { // replace last unary operation in expression
            removeLastUnaryFromExpression();
        }
        addToExpression(lastNumber);
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
     * Also writes to expression.
     *
     * @param currentOperation a current unary mathematical operation to execute
     * @return the result of current called unary mathematical operation with the last entered number
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

    private void updateLastNumberAfterUnary(MathOperation currentOperation) throws CalculationException {
        if (wasUnaryBefore || operation == null || !isNewNumber) {
            lastNumber = getResult(currentOperation, lastNumber);
        } else {
            lastNumber = getResult(currentOperation, previousNumber);
        }
    }

    /**
     * Updates an expression after last unary operation by adding the given unary operation. If this unary operation is
     * the first after binary adds last number before.
     *
     * @param currentOperation a current unary operation to add to expression
     */
    private void updateExpressionForUnary(MathOperation currentOperation) {
        if (!wasUnaryBefore) { // if was binary operation before add last number that is the base for current unary operation
            addNumberToExpression();
        }
        addToExpression(currentOperation);
    }

    /**
     * Adds last entered number or last result of operations to the expression.
     */
    private void addNumberToExpression() {
        if (operation != null && expression.size() > 4) {
            addToExpression(previousNumber);
        } else {
            addToExpression(lastNumber);
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
     * Calculates the result for the current mathematical expression consists of entered numbers and mathematical
     * operations. The result of calculations assigns to last entered number value.
     *
     * @throws CalculationException if some error while calculations occurred or result is out of valid bounds
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
     * Adds a decimal point to the last entered number. Updates expression and resets last number to zero if point
     * added after unary operation without entering any digit.
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
        removeLastUnaryFromExpression();
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

            lastNumber = ZERO;
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
     * Replaces last binary operation in expression by the last specified one.
     */
    private void replaceLastOperationInExpression() {
        if (expression.size() != 0) {
            expression.set(expression.size() - 1, operation);
        }
    }

    /**
     * Adds the given number to the current math expression. Also updates index of the last added number to the expression.
     *
     * @param number a number to add into expression
     */
    private void addToExpression(BigDecimal number) {
        expression.add(number);
        indexOfLastNumberInExpression = expression.size() - 1;
    }

    /**
     * Adds the given {@link MathOperation} operation to the current expression.
     *
     * @param operation a given operation to add into expression
     */
    private void addToExpression(MathOperation operation) {
        expression.add(operation);
    }

    /**
     * Removes last unary operation from expression. This operation starts with the last added number and up to the last
     * argument.
     */
    private void removeLastUnaryFromExpression() {
        if (wasUnaryBefore && indexOfLastNumberInExpression < expression.size()) {
            for (int i = expression.size() - 1; i >= indexOfLastNumberInExpression; i--) {

                expression.remove(i);
            }
            wasUnaryBefore = false;
        }
    }
}
