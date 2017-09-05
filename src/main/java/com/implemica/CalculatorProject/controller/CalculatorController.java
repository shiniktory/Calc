package com.implemica.CalculatorProject.controller;

import com.implemica.CalculatorProject.model.calculation.EditOperation;
import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.calculation.MemoryOperation;
import com.implemica.CalculatorProject.model.calculation.StandardCalculationExecutor;
import com.implemica.CalculatorProject.model.exception.CalculationException;
import com.implemica.CalculatorProject.model.Calculator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

import static com.implemica.CalculatorProject.model.calculation.EditOperation.*;
import static com.implemica.CalculatorProject.model.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.model.calculation.MemoryOperation.*;
import static com.implemica.CalculatorProject.model.validation.DataValidator.isEmptyString;
import static com.implemica.CalculatorProject.model.validation.DataValidator.isNumberLengthValid;
import static com.implemica.CalculatorProject.model.validation.DataValidator.isResultOverflow;
import static com.implemica.CalculatorProject.view.formatting.OutputFormatter.*;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;

/**
 * The CalculatorController class is a chain between calculator's view and calculations. It accepts and handles
 * events generated by mouse clicks or keyboard keys' pressings, extracts needed information and passes it to model
 * that processes all manipulations.
 *
 * @author V. Kozina-Kravchenko
 */
public class CalculatorController {

    /**
     * An instance of {@link Calculator} that accepts and calculates the results for an
     * input data.
     */
    private final Calculator calculator = new Calculator();

    {
        calculator.setCalculationExecutor(new StandardCalculationExecutor());
    }

    /**
     * The title for group calculator views used for converting values.
     */
    private static final String CONVERTER_GROUP = "\tCONVERTER";

    /**
     * A {@link Font} for the title for group calculator views used for converting values.
     */
    private static final Font CONVERTER_FONT = Font.font("Segoe UI", FontWeight.BOLD, 15);

    /**
     * The list of calculator types.
     */
    private static final String[] CALCULATOR_TYPES = new String[]{"\tStandard", "\tScientific", "\tProgrammer",
            "\tDate calculation", CONVERTER_GROUP, "\tVolume", "\tLength", "\tWeight and Mass", "\tTemperature",
            "\tEnergy", "\tArea", "\tSpeed", "\tTime", "\tPower", "\tData", "\tPressure", "\tAngle"};

    /**
     * The list of {@link Label}s with names of calculator types.
     */
    private static final ObservableList<Label> LIST_OF_CALCULATOR_TYPES = getListOfCalcTypes();

    /**
     * The count of milliseconds for animation duration.
     */
    private static final int ANIMATION_DURATION = 75;

    /**
     * The value of width for panel with calculator types.
     */
    private static final double VIEW_PANEL_MAX_WIDTH = 257.0;

    /**
     * The value of the offset from the current mouse position by x used to set {@link Tooltip} position.
     */
    private static final int TOOLTIP_X_OFFSET = 50;

    /**
     * The value of the offset from the current mouse position by y used to set {@link Tooltip} position.
     */
    private static final int TOOLTIP_Y_OFFSET = 55;

    /**
     * A reference to the {@link TextField} with a value of current number.
     */
    @FXML
    private TextField currentNumberText;

    /**
     * A reference to the {@link TextField} with a string containing current mathematical expression.
     */
    @FXML
    private TextField prevOperationsText;

    /**
     * A reference to the {@link VBox} contains controls for changing the calculator's type.
     */
    @FXML
    private VBox viewTypesPanel;

    /**
     * A reference to the {@link ListView} contains the list of calculator types.
     */
    @FXML
    private ListView<Label> viewTypesList;

    /**
     * A references to the {@link Button}s with digits, mathematical, memory and edit operations.
     */
    @FXML
    private Button percent, squareRoot, square, reverse, divide, multiply, subtract, add, result, negate,
            digit1, digit2, digit3, digit4, digit5, digit6, digit7, digit8, digit9, digit0, point,
            memoryClean, memoryRecall, memoryShow, memoryAdd, memorySubtract, memoryStore,
            clean, cleanCurrent, leftErase;

    /**
     * A reference to the {@link Pane} shows a memorized number.
     */
    @FXML
    private Pane memoryStorage;

    /**
     * The storage of all {@link Button}s associated with its functions in application.
     * For example, digit button associated with {@link BigDecimal} value,
     * math operation button with {@link MathOperation}, etc.
     */
    private static final Map<Button, Object> BUTTONS_WITH_FUNCTIONS = new LinkedHashMap<>();

    /**
     * The storage of all {@link Button}s associated with the {@link KeyCodeCombination}s that activates these {@link Button}s.
     */
    private static final Map<KeyCodeCombination, Button> BUTTONS_WITH_KEYS = new LinkedHashMap<>();

    /**
     * The flag variable shows is {@link #viewTypesPanel} shown right now.
     */
    private boolean isViewPanelShown = false;

    /**
     * The flag variable shows is {@link #memoryStorage} shown right now.
     */
    private boolean isMemoryStorageShown = false;

    /**
     * The flag variable shows is error occurred while calculations.
     */
    private boolean isErrorOccurred = false;

    /**
     * A {@link StringBuilder} instance constructs a mathematical expression string.
     */
    private StringBuilder expression;

    /**
     * The value of separator for mathematical expression parts.
     */
    private static final String EXPRESSION_PARTS_SEPARATOR = " ";

    /**
     * The flag variable shows was a previous part of mathematical expression unary {@link MathOperation}.
     */
    private boolean wasUnaryBefore = false;

    /**
     * The string contains last formatted {@link BigDecimal} number or unary {@link MathOperation}.
     */
    private String lastUnaryArgument = "";

    /**
     * The value of duration in millis for the {@link Button} pressed animation.
     */
    private static final int CLICK_ANIMATION_DURATION = 50;

    /**
     * An error message about {@link BigDecimal} number's value is too large or too small.
     */
    private static final String OVERFLOW_ERROR = "Overflow";

    /**
     * An error message about no function provided for current {@link Button} extracted from {@link Event}.
     */
    private static final String NO_FUNCTION_PROVIDED_FOR_BUTTON = "No function provided for button with id: ";

    /**
     * Handles all {@link KeyEvent}s generated by keyboard key pressing.
     *
     * @param event an {@link KeyEvent} instance generated by key pressing
     */
    @FXML
    private void handleKeyEvent(KeyEvent event) {
        initializeButtons();

        if (isViewPanelShown) {
            showOrHideViewPanel();
        }

        KeyCode key = event.getCode();

        if (key.isModifierKey()) {
            return;
        }

        KeyCodeCombination combination;

        if (event.isShiftDown()) {
            combination = new KeyCodeCombination(key, SHIFT_DOWN);
        } else {
            combination = new KeyCodeCombination(key);
        }
        fireButton(combination);
    }

    /**
     * Searches a {@link Button} by the given combination of pressed keys. If find fires an {@link ActionEvent} on this
     * {@link Button} and adds a button pressed visual effect for it.
     *
     * @param combination a combination of pressed keys to find appropriate {@link Button}
     */
    private static void fireButton(KeyCodeCombination combination) {
        Platform.runLater(() -> {
            Button button = BUTTONS_WITH_KEYS.get(combination);

            if (button != null) {
                addButtonPressedEffect(button);
                button.fire();
            }
        });
    }

    /**
     * Adds a visual effect for {@link Button} pressing.
     *
     * @param button the {@link Button} to add a button pressed effect
     */
    private static void addButtonPressedEffect(Button button) {
        button.arm();
        // add delay to show a button pressed effect
        PauseTransition pause = new PauseTransition(Duration.millis(CLICK_ANIMATION_DURATION));
        pause.setOnFinished(event1 -> button.disarm());
        pause.play();
    }

    /**
     * Handles {@link Event} for {@link Button}s with numbers, mathematical, memory and edit operations. Updates
     * {@link TextField}s with expression and current number.
     *
     * @param event an {@link Event} occurred on {@link Button} with number or operation
     */
    @FXML
    private void handleButtonEvent(Event event) {
        initializeButtons();

        if (isViewPanelShown) {
            showOrHideViewPanel();
        }

        Button button = (Button) event.getSource();
        String textToSet;

        try {
            textToSet = handleButtonEventImpl(button);
        } catch (CalculationException e) {
            textToSet = handleException(e);
        }

        setCurrentNumber(textToSet);
        updateExpression();

        if (isErrorOccurred) {
            calculator.cleanAll();
        }
    }

    /**
     * Executes an action for the specified {@link Button} extracted from occurred {@link Event}. For example, executes
     * a {@link MathOperation} or adds digit from {@link Button} to the current number or cleans all {@link TextField}s.
     * Returns the text to set in the {@link TextField} with current number.
     *
     * @param button a {@link Button} extracted from {@link Event}
     * @return the text to set in the {@link TextField} with current number
     * @throws CalculationException if some error occurred while data processing
     */
    private String handleButtonEventImpl(Button button) throws CalculationException {
        Object buttonFunction = BUTTONS_WITH_FUNCTIONS.get(button);
        String textToSet;

        if (buttonFunction instanceof BigDecimal) {
            textToSet = addDigit((BigDecimal) buttonFunction);
        } else if (buttonFunction instanceof String && POINT.equals(buttonFunction)) {
            textToSet = addDecimalSeparator();
        } else if (buttonFunction instanceof MathOperation) {
            textToSet = executeMathOperation((MathOperation) buttonFunction);
        } else if (buttonFunction instanceof MemoryOperation) {
            textToSet = executeMemoryOperation((MemoryOperation) buttonFunction);
        } else if (buttonFunction instanceof EditOperation) {
            textToSet = executeEditOperation((EditOperation) buttonFunction);
        } else {
            throw new UnsupportedOperationException(NO_FUNCTION_PROVIDED_FOR_BUTTON + button.getId());
        }

        return textToSet;
    }

    /**
     * Returns string contains formatted {@link BigDecimal} number after entering a new digit.
     *
     * @param digit a digit represented by {@link BigDecimal} number to enter
     * @return string contains formatted {@link BigDecimal} number after entering a new digit
     */
    private String addDigit(BigDecimal digit) {
        resetAfterError();

        BigDecimal modifiedNumber = calculator.enterDigit(digit);
        boolean isLastPoint = false;

        if (!isNumberLengthValid(modifiedNumber)) {
            isLastPoint = calculator.deleteLastDigit();
            modifiedNumber = calculator.getLastNumber();
        }
        return formatEnteredNumber(modifiedNumber, isLastPoint);
    }

    /**
     * Returns string contains formatted current {@link BigDecimal} number with added decimal separator.
     *
     * @return string contains formatted current {@link BigDecimal} number with added decimal separator
     */
    private String addDecimalSeparator() {
        calculator.addPoint();
        BigDecimal currentNumber = calculator.getLastNumber();
        // if after adding decimal separator number's scale still is zero,
        // than need to format with point at the end of number
        boolean needAppendPoint = currentNumber.scale() == 0;

        return formatEnteredNumber(currentNumber, needAppendPoint);
    }

    /**
     * Executes a {@link MathOperation} appropriate to the {@link Button} extracted from the {@link Event} and
     * returns the string contains formatted result to set in the {@link TextField} with current number.
     *
     * @param operation a {@link MathOperation} to execute
     * @return the result of the specified {@link MathOperation} to set in the {@link TextField} with current number
     * @throws CalculationException if some error occurred while calculations
     */
    private String executeMathOperation(MathOperation operation) throws CalculationException {
        BigDecimal result;
        if (operation != RESULT) {
            if (isMemoryStorageShown) {
                showOrHideMemoryPane();
            }
            result = calculator.executeMathOperation(operation);
        } else {
            resetAfterError();
            result = calculator.calculateResult();
        }
        checkResultForOverflow(result);
        return formatWithGroupDelimiters(result);
    }

    /**
     * Checks the given {@link BigDecimal} number as result of an operations for overflow.
     *
     * @param result the {@link BigDecimal} number to check for overflow
     * @throws CalculationException if result is out of valid bounds
     */
    private static void checkResultForOverflow(BigDecimal result) throws CalculationException {
        if (isResultOverflow(result)) {
            throw new CalculationException(OVERFLOW_ERROR);
        }
    }

    /**
     * Executes an {@link MemoryOperation} appropriate to the {@link Button} extracted from the {@link Event}. Returns
     * the string contains formatted last entered number or memorized value if operation is {@link MemoryOperation#MEMORY_RECALL}.
     * Also enables or disables some memory {@link Button}s depends on what {@link MemoryOperation} it is.
     *
     * @param operation an {@link MemoryOperation} appropriate to the {@link Button} extracted from the {@link Event}
     * @return the last entered number or memorized value if operation is {@link MemoryOperation#MEMORY_RECALL}
     * @throws CalculationException if some error occurred while calculations
     */
    private String executeMemoryOperation(MemoryOperation operation) throws CalculationException {
        calculator.executeMemoryOperation(operation);

        boolean needEnable = operation != MEMORY_CLEAN;
        enableMemoryStateButtons(needEnable);
        return formatWithGroupDelimiters(calculator.getLastNumber());
    }

    /**
     * Executes an {@link EditOperation} appropriate to the {@link Button} extracted from {@link Event}. Returns the
     * value of the modified last entered {@link BigDecimal} number. Also resets {@link TextField}s values and enables
     * operation {@link Button}s after previous error.
     *
     * @param operation an {@link EditOperation} to execute
     * @return the value of the modified last entered {@link BigDecimal} number
     */
    private String executeEditOperation(EditOperation operation) {
        resetAfterError();
        boolean isLastSymbolPoint = false;

        if (operation == CLEAN) {
            calculator.cleanAll();
            isErrorOccurred = false;

            if (isMemoryStorageShown) {
                showOrHideMemoryPane();
            }

        } else if (operation == CLEAN_CURRENT) {
            calculator.cleanCurrent();
            isErrorOccurred = false;

        } else if (operation == LEFT_ERASE) {
            isLastSymbolPoint = calculator.deleteLastDigit();
        }
        return formatEnteredNumber(calculator.getLastNumber(), isLastSymbolPoint);
    }

    /**
     * Resets after error content of {@link TextField}s to default values and enables all {@link Button}s that must be
     * active by default.
     */
    private void resetAfterError() {
        if (isErrorOccurred) {
            enableAllOperations(true);
            String numberToReset = formatWithGroupDelimiters(calculator.getLastNumber());
            setCurrentNumber(numberToReset);
            updateExpression();
            isErrorOccurred = false;
        }
    }

    /**
     * Returns a message with information about given {@link Exception} and disables all {@link Button}s with mathematical
     * operations, including memory add, subtract and store {@link Button}s.
     *
     * @param e an occurred {@link Exception} instance
     * @return a message with information about given {@link Exception}
     */
    private String handleException(Exception e) {
        enableMemoryStateButtons(false);
        enableAllOperations(false);
        isErrorOccurred = true;
        return e.getMessage();
    }

    /**
     * Sets the given string contains formatted {@link BigDecimal} number to the {@link TextField} that contains
     * current number value.
     *
     * @param formattedNumber the string contains formatted {@link BigDecimal} number to set
     */
    private void setCurrentNumber(String formattedNumber) {
        if (!isEmptyString(formattedNumber)) {
            Platform.runLater(() -> {
                currentNumberText.setText(formattedNumber);
                currentNumberText.end();
                prevOperationsText.end();
            });
        }
    }

    /**
     * Updates {@link TextField} with mathematical expression.
     */
    private void updateExpression() {
        String expressionText = getExpression();
        Platform.runLater(() -> {
            prevOperationsText.setText(expressionText);
            prevOperationsText.end();
        });
    }

    /**
     * Returns string value contains formatted current mathematical expression.
     *
     * @return string value contains formatted current mathematical expression
     */
    private String getExpression() {
        List<Object> arguments = calculator.getExpressionArguments();
        expression = new StringBuilder();
        lastUnaryArgument = "";
        int lastArgumentIndex = arguments.size() - 1;

        for (int i = 0; i < arguments.size(); i++) {
            Object argument = arguments.get(i);

            formatAndAppendCurrentArgument(argument, i == lastArgumentIndex);
        }
        return expression.toString().trim();
    }

    /**
     * Formats the given argument and appends it to the current formatted expression.
     *
     * @param argument          an argument to format
     * @param isTheLastArgument a flag shows is this argument the last in expression
     */
    private void formatAndAppendCurrentArgument(Object argument, boolean isTheLastArgument) {
        if (argument instanceof BigDecimal) {
            appendNumber((BigDecimal) argument, isTheLastArgument);

        } else if (argument instanceof MathOperation) {
            MathOperation operation = (MathOperation) argument;

            appendOperation(operation, isTheLastArgument);
            wasUnaryBefore = !operation.isBinary();
        }
    }

    /**
     * Formats and appends the given {@link BigDecimal} number to the current formatted expression.
     *
     * @param number            a {@link BigDecimal} number to append
     * @param isTheLastArgument a flag shows is this argument the last in expression
     */
    private void appendNumber(BigDecimal number, boolean isTheLastArgument) {
        lastUnaryArgument = formatToMathView(number);

        if (isTheLastArgument) {
            appendToExpression(lastUnaryArgument);
        }
        wasUnaryBefore = !isTheLastArgument; // number in expression acts like an unary operation
    }

    /**
     * Formats and appends the given {@link MathOperation} to the current formatted expression.
     *
     * @param operation         a {@link MathOperation} to append
     * @param isTheLastArgument a flag shows is this argument the last in expression
     */
    private void appendOperation(MathOperation operation, boolean isTheLastArgument) {
        if (operation.isBinary()) {
            if (wasUnaryBefore) {
                appendToExpression(lastUnaryArgument);
            }
            appendToExpression(operation.symbol());

        } else {
            lastUnaryArgument = formatUnaryOperation(operation, lastUnaryArgument);

            if (isTheLastArgument) {
                appendToExpression(lastUnaryArgument);
            }
        }
    }

    /**
     * Appends separator and the specified formatted value to the current expression.
     *
     * @param argument a formatted value to append to the expression
     */
    private void appendToExpression(String argument) {
        expression.append(EXPRESSION_PARTS_SEPARATOR).append(argument);
    }

    /**
     * Handles an {@link Event} generated by showing a {@link Tooltip}. Sets the location of {@link Tooltip} relies on current
     * mouse position.
     *
     * @param event an {@link Event} generated by showing a {@link Tooltip}
     */
    @FXML
    private void setTooltipPosition(Event event) {
        Tooltip tooltip = (Tooltip) event.getSource();
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        tooltip.setX(mouse.getX() - TOOLTIP_X_OFFSET);
        tooltip.setY(mouse.getY() - TOOLTIP_Y_OFFSET);
    }

    /**
     * Shows or hides a {@link #viewTypesPanel}.
     */
    @FXML
    private void showOrHideViewPanel() {
        if (!isViewPanelShown) { // add list of calculator's view types
            viewTypesList.setItems(LIST_OF_CALCULATOR_TYPES);
            viewTypesList.getSelectionModel().select(0);
            viewTypesPanel.setVisible(true);
        }

        // play show/hide animation
        Timeline timeline = getTimeline();
        timeline.play();

        // add delay for animation complete
        PauseTransition pause = new PauseTransition(Duration.millis(ANIMATION_DURATION));
        pause.setOnFinished(event1 -> viewTypesPanel.setVisible(isViewPanelShown));
        pause.play();
        isViewPanelShown = !isViewPanelShown;
    }

    /**
     * Returns a {@link Timeline} instance configured for showing or hiding a {@link #viewTypesPanel}.
     *
     * @return a {@link Timeline} instance configured for showing or hiding a {@link #viewTypesPanel}
     */
    private Timeline getTimeline() {
        double startWidth;
        double endWidth;

        if (isViewPanelShown) {
            startWidth = VIEW_PANEL_MAX_WIDTH;
            endWidth = 0;
        } else {
            startWidth = 0;
            endWidth = VIEW_PANEL_MAX_WIDTH;
        }

        return new Timeline(getKeyFrame(0, startWidth),
                getKeyFrame(ANIMATION_DURATION, endWidth));
    }

    /**
     * Returns a {@link KeyFrame} instance configured with the given animation duration in millis and
     * end width value.
     *
     * @param animationDurationMS a value of animation duration in millis
     * @param endWidthValue       a value of an end width
     * @return a {@link KeyFrame} instance configured with the given parameters
     */
    private KeyFrame getKeyFrame(int animationDurationMS, double endWidthValue) {
        return new KeyFrame(Duration.millis(animationDurationMS), getKeyValue(endWidthValue));
    }

    /**
     * Returns a {@link KeyValue} instance configured with the given end value for the preferred width parameter.
     *
     * @param endValue the end value of width
     * @return a {@link KeyValue} instance configured with the given end value for the preferred width parameter
     */
    private KeyValue getKeyValue(double endValue) {
        return new KeyValue(viewTypesPanel.prefWidthProperty(), endValue);
    }

    /**
     * Returns the list of calculator view types.
     *
     * @return the list of calculator view types
     */
    private static ObservableList<Label> getListOfCalcTypes() {
        List<Label> labelList = new LinkedList<>();

        for (String type : CALCULATOR_TYPES) {
            Label label = new Label(type);

            if (CONVERTER_GROUP.equals(type)) {
                label.setFont(CONVERTER_FONT);
            }

            labelList.add(label);
        }
        return FXCollections.observableList(labelList);
    }

    /**
     * Shows or hides a {@link #memoryStorage} panel with memorized value.
     */
    @FXML
    private void showOrHideMemoryPane() {
        enableMemoryStateButtons(isMemoryStorageShown);
        enableAllButtons(isMemoryStorageShown);
        isMemoryStorageShown = !isMemoryStorageShown;  // if pane is already shown - hide it
        memoryStorage.setVisible(isMemoryStorageShown);

        Platform.runLater(() -> memoryShow.setDisable(false));
    }

    /**
     * Enables or disables all mathematical operations, including memory add, subtract and store {@link Button}s depends
     * on the specified boolean value.
     *
     * @param enable a boolean value shows to enable or disable {@link Button}s
     */
    private void enableAllOperations(boolean enable) {
        enableMemoryEditButtons(enable);
        Platform.runLater(() -> {
            for (Button button : BUTTONS_WITH_FUNCTIONS.keySet()) {
                enableOperationButton(button, enable);
            }
        });
    }

    /**
     * Enables or disables memory store, add and subtract {@link Button}s depends on the specified boolean value.
     *
     * @param enable a boolean value shows to enable or disable {@link Button}s
     */
    private void enableMemoryEditButtons(boolean enable) {
        memoryAdd.setDisable(!enable);
        memorySubtract.setDisable(!enable);
        memoryStore.setDisable(!enable);
    }

    /**
     * Disables or enables memory recall, clean and store {@link Button}s depends on the specified boolean value.
     *
     * @param enable a boolean value shows to enable or disable {@link Button}s
     */
    private void enableMemoryStateButtons(boolean enable) {
        memoryRecall.setDisable(!enable);
        memoryClean.setDisable(!enable);
        memoryShow.setDisable(!enable);
    }

    /**
     * Disables or enables the given {@link Button} with {@link MathOperation} or decimal separator.
     *
     * @param button a {@link Button} with {@link MathOperation} or decimal separator to disable or enable
     * @param enable a boolean value shows to enable or disable {@link Button}
     */
    private static void enableOperationButton(Button button, boolean enable) {
        Object buttonFunction = BUTTONS_WITH_FUNCTIONS.get(button);

        boolean isMathOperation = (buttonFunction instanceof MathOperation) &&
                (buttonFunction != RESULT);

        if (isMathOperation || POINT.equals(buttonFunction)) {
            button.setDisable(!enable);
        }
    }

    /**
     * Disables or enables all {@link Button}s with numbers and math operations.
     *
     * @param enable a boolean value shows to enable or disable {@link Button}s
     */
    private void enableAllButtons(boolean enable) {
        enableMemoryEditButtons(enable);
        Platform.runLater(() -> {
            for (Button button : BUTTONS_WITH_FUNCTIONS.keySet()) {
                button.setDisable(!enable);
            }
        });
    }

    /**
     * Adds all {@link Button}s with its functions and {@link KeyCodeCombination}s that activate these {@link Button}s
     * to the {@link Button} storage. The initialization executes only if storage is empty.
     */
    private void initializeButtons() {
        if (!BUTTONS_WITH_FUNCTIONS.isEmpty() || !BUTTONS_WITH_KEYS.isEmpty()) {
            return;
        }
        // buttons with digits
        addButton(digit0, BigDecimal.ZERO, KeyCode.DIGIT0);
        addButton(digit0, BigDecimal.ZERO, KeyCode.NUMPAD0);

        addButton(digit1, BigDecimal.ONE, KeyCode.DIGIT1);
        addButton(digit1, BigDecimal.ONE, KeyCode.NUMPAD1);

        addButton(digit2, BigDecimal.valueOf(2), KeyCode.DIGIT2);
        addButton(digit2, BigDecimal.valueOf(2), KeyCode.NUMPAD2);

        addButton(digit3, BigDecimal.valueOf(3), KeyCode.DIGIT3);
        addButton(digit3, BigDecimal.valueOf(3), KeyCode.NUMPAD3);

        addButton(digit4, BigDecimal.valueOf(4), KeyCode.DIGIT4);
        addButton(digit4, BigDecimal.valueOf(4), KeyCode.NUMPAD4);

        addButton(digit5, BigDecimal.valueOf(5), KeyCode.DIGIT5);
        addButton(digit5, BigDecimal.valueOf(5), KeyCode.NUMPAD5);

        addButton(digit6, BigDecimal.valueOf(6), KeyCode.DIGIT6);
        addButton(digit6, BigDecimal.valueOf(6), KeyCode.NUMPAD6);

        addButton(digit7, BigDecimal.valueOf(7), KeyCode.DIGIT7);
        addButton(digit7, BigDecimal.valueOf(7), KeyCode.NUMPAD7);

        addButton(digit8, BigDecimal.valueOf(8), KeyCode.DIGIT8);
        addButton(digit8, BigDecimal.valueOf(8), KeyCode.NUMPAD8);

        addButton(digit9, BigDecimal.valueOf(9), KeyCode.DIGIT9);
        addButton(digit9, BigDecimal.valueOf(9), KeyCode.NUMPAD9);

        // button with point
        addButton(point, POINT, KeyCode.PERIOD);
        addButton(point, POINT, KeyCode.DECIMAL);

        // buttons with math operations
        addButton(percent, PERCENT, KeyCode.DIGIT5, SHIFT_DOWN);
        addButton(squareRoot, SQUARE_ROOT, KeyCode.DIGIT2, SHIFT_DOWN);
        addButton(square, SQUARE, KeyCode.Q);
        addButton(reverse, REVERSE, KeyCode.R);
        addButton(divide, DIVIDE, KeyCode.DIVIDE);
        addButton(divide, DIVIDE, KeyCode.SLASH);
        addButton(multiply, MULTIPLY, KeyCode.DIGIT8, SHIFT_DOWN);
        addButton(multiply, MULTIPLY, KeyCode.MULTIPLY);
        addButton(subtract, SUBTRACT, KeyCode.SUBTRACT);
        addButton(subtract, SUBTRACT, KeyCode.MINUS);
        addButton(add, ADD, KeyCode.ADD);
        addButton(add, ADD, KeyCode.EQUALS, SHIFT_DOWN);
        addButton(result, RESULT, KeyCode.ENTER);
        addButton(result, RESULT, KeyCode.EQUALS);
        addButton(negate, NEGATE, null);

        // buttons with memory operations
        addButton(memoryClean, MEMORY_CLEAN, null);
        addButton(memoryRecall, MEMORY_RECALL, null);
        addButton(memoryAdd, MEMORY_ADD, null);
        addButton(memorySubtract, MEMORY_SUBTRACT, null);
        addButton(memoryStore, MEMORY_STORE, null);
        addButton(memoryShow, MEMORY_SHOW, null);

        // buttons with edit operations
        addButton(clean, CLEAN, KeyCode.ESCAPE);
        addButton(clean, CLEAN, KeyCode.SPACE);
        addButton(cleanCurrent, CLEAN_CURRENT, null);
        addButton(leftErase, LEFT_ERASE, KeyCode.BACK_SPACE);
    }

    /**
     * Adds the given {@link Button} with its function and {@link KeyCode} with {@link Modifier}s that actives this
     * {@link Button} to the {@link Button} storage. {@link Button}'s function means what role this {@link Button} acts.
     * For example, {@link Button} is digit or some operation.
     *
     * @param button         a {@link Button} to add to storage
     * @param buttonFunction an object encapsulating {@link Button}'s role in application
     * @param keyCode        a {@link KeyCode} that actives current button
     * @param modifiers      a {@link Modifier}s to {@link KeyCode} that actives current {@link Button}
     */
    private static void addButton(Button button, Object buttonFunction, KeyCode keyCode, Modifier... modifiers) {
        BUTTONS_WITH_FUNCTIONS.putIfAbsent(button, buttonFunction);

        if (keyCode != null) {
            BUTTONS_WITH_KEYS.put(new KeyCodeCombination(keyCode, modifiers), button);
        }
    }
}