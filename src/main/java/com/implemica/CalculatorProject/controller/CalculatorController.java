package com.implemica.CalculatorProject.controller;

import com.implemica.CalculatorProject.model.calculation.EditOperation;
import com.implemica.CalculatorProject.model.calculation.MathOperation;
import com.implemica.CalculatorProject.model.calculation.MemoryOperation;
import com.implemica.CalculatorProject.model.calculation.StandardCalculator;
import com.implemica.CalculatorProject.model.exception.CalculationException;
import com.implemica.CalculatorProject.model.InputValueProcessor;
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
import static com.implemica.CalculatorProject.model.validation.DataValidator.isResultOverflow;
import static com.implemica.CalculatorProject.model.validation.DataValidator.isDigit;
import static com.implemica.CalculatorProject.view.formatting.OutputFormatter.*;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;

/**
 * The {@code CalculatorController} class is a chain between calculator's view and calculations. It accepts and handles
 * events generated by mouse clicks or keyboard keys' pressings.
 *
 * @author V. Kozina-Kravchenko
 */
public class CalculatorController {

    /**
     * An instance of {@link InputValueProcessor} that accepts and calculates the results for an
     * input data.
     */
    private final InputValueProcessor valueProcessor = new InputValueProcessor();

    {
        valueProcessor.setCalculator(new StandardCalculator());
    }

    /**
     * The title for group calculator views used for converting values.
     */
    private static final String CONVERTER_GROUP = "\tCONVERTER";

    /**
     * A font for the title for group calculator views used for converting values.
     */
    private static final Font CONVERTER_FONT = Font.font("Segoi UI", FontWeight.BOLD, 15);

    /**
     * The list of calculator types.
     */
    private static final String[] CALCULATOR_TYPES = new String[]{"\tStandard", "\tScientific", "\tProgrammer",
            "\tDate calculation", CONVERTER_GROUP, "\tVolume", "\tLength", "\tWeight and Mass", "\tTemperature",
            "\tEnergy", "\tArea", "\tSpeed", "\tTime", "\tPower", "\tData", "\tPressure", "\tAngle"};

    /**
     * The count of milliseconds for animation duration.
     */
    private static final int ANIMATION_DURATION = 75;

    /**
     * The value of width for panel with calculator types.
     */
    private static final double VIEW_PANEL_MAX_WIDTH = 257.0;

    /**
     * The value of the offset from the current mouse position by x used to set tooltip position.
     */
    private static final int TOOLTIP_X_OFFSET = 50;

    /**
     * The value of the offset from the current mouse position by y used to set tooltip position.
     */
    private static final int TOOLTIP_Y_OFFSET = 55;

    /**
     * A reference to the {@link TextField} with a value of current number value.
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
     * For example, digit button, math operation button, etc.
     */
    private Map<Button, Object> buttonsWithFunctions = new LinkedHashMap<>();

    /**
     * The storage of all {@link Button}s associated with the {@link KeyCodeCombination}s that activates these buttons.
     */
    private Map<KeyCodeCombination, Button> buttonsWithKeys = new LinkedHashMap<>();

    /**
     * The flag variable shows is panel with calculator types shown right now.
     */
    private boolean isViewPanelShown = false;

    /**
     * The flag variable shows is panel with stored in memory values shown right now.
     */
    private boolean isMemoryStorageShown = false;

    /**
     * The flag variable shows is error occurred while calculations.
     */
    private boolean isErrorOccurred = false;

    /**
     * An error message about number's value is too large or too small.
     */
    private static final String OVERFLOW_ERROR = "Overflow";

    /**
     * Handles all events generated by keyboard key pressing.
     *
     * @param event an event instance generated by key pressing
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
     * button and adds a button clicked visual effect for it.
     *
     * @param combination a combination of pressed keys to find appropriate button
     */
    private void fireButton(KeyCodeCombination combination) {
        Platform.runLater(() -> {
            Button button = buttonsWithKeys.get(combination);
            if (button != null) {
                addButtonClickedEffect(button);
                button.fire();
            }
        });
    }

    /**
     * Adds a visual effect for button pressing.
     *
     * @param button the button to add a button pressed effect
     */
    private void addButtonClickedEffect(Button button) {
        button.arm();
        // add delay to show a button pressed effect
        PauseTransition pause = new PauseTransition(Duration.millis(50));
        pause.setOnFinished(event1 -> button.disarm());
        pause.play();
    }

    /**
     * Handles {@link ActionEvent} and {@link MouseEvent} for buttons with numbers, mathematical,
     * memory and edit operations. Updates text fields with history and current number.
     *
     * @param event an {@link ActionEvent} and {@link MouseEvent} occurred on button with number or operation
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
        updateHistoryExpression();
        if (isErrorOccurred) {
            valueProcessor.cleanAll();
        }
    }

    /**
     * Executes an action for the specified button extracted from occurred event. For example, executes a mathematical
     * operation or adds digit from button to the current number or cleans all text fields. Returns the text to set
     * in the field with current number.
     *
     * @param button a button extracted from event
     * @return the text to set in the field with current number
     * @throws CalculationException if some error occurred while data processing
     */
    private String handleButtonEventImpl(Button button) throws CalculationException {
        String textToSet = currentNumberText.getText();

        Object buttonFunction = buttonsWithFunctions.get(button);

        // if it is digit button
        if (buttonFunction instanceof Number) {
            resetAfterError();

            boolean isDigitAppended = valueProcessor.enterDigit((BigDecimal) buttonFunction);

            if (isDigitAppended) {
                textToSet = formatEnteredNumber(valueProcessor.getLastNumber(), false);

            }

        } else if (buttonFunction instanceof String && POINT.equals(buttonFunction)) {

            valueProcessor.addPoint();
            BigDecimal currentNumber = valueProcessor.getLastNumber();
            boolean needAppendPoint = currentNumber.scale() == 0;

            textToSet = formatEnteredNumber(currentNumber, needAppendPoint);

        } else if (buttonFunction instanceof MathOperation) {
            textToSet = executeMathOperation((MathOperation) buttonFunction);

        } else if (buttonFunction instanceof MemoryOperation) {
            textToSet = executeMemoryOperation((MemoryOperation) buttonFunction);

        } else if (buttonFunction instanceof EditOperation) {
            textToSet = executeEditOperation((EditOperation) buttonFunction);
        }

        return textToSet;
    }

    /**
     * Executes a mathematical operation appropriate to the button extracted from event and returns the result to set
     * in the text field with current number.
     *
     * @param operation a mathematical operation to execute
     * @return the result of the specified operation to set in the text field with current number
     * @throws CalculationException if some error occurred while calculations
     */
    private String executeMathOperation(MathOperation operation) throws CalculationException {
        BigDecimal result;
        if (operation != RESULT) {
            if (isMemoryStorageShown) {
                showOrHideMemoryPane();
            }
                result = valueProcessor.executeMathOperation(operation);
        } else {
            resetAfterError();
            result = valueProcessor.calculateResult();
        }
        checkResultForOverflow(result);
        return formatNumberWithGroupDelimiters(result);
    }

    /**
     * Checks the given number as result of an operations for overflow.
     *
     * @param result the number to check for overflow
     * @throws CalculationException if result is out of valid bounds
     */
    private void checkResultForOverflow(BigDecimal result) throws CalculationException {
        if (isResultOverflow(result)) {
            throw new CalculationException(OVERFLOW_ERROR);
        }
    }

    /**
     * Executes an operation with memorized value appropriate to the button extracted from event. Returns the last
     * entered number or memorized value if operation is memory recall. Also enables or disables some memory buttons
     * depends on what memory operation it is.
     *
     * @param operation an operation with memorized value appropriate to the button extracted from event
     * @return the last entered number or memorized value of operation is memory recall
     * @throws CalculationException if some error occurred while calculations
     */
    private String executeMemoryOperation(MemoryOperation operation) throws CalculationException {
        valueProcessor.executeMemoryOperation(operation);

        boolean needEnable = !(operation == MEMORY_CLEAN);
        enableMemoryStateButtons(needEnable);
        return formatNumberWithGroupDelimiters(valueProcessor.getLastNumber());
    }

    /**
     * Executes an edit operation appropriate to the button extracted from event. Returns the value of the modified
     * last entered number. Also resets text fields values and enables operation buttons after previous error.
     *
     * @param operation an edit operation to execute
     * @return the value of the modified last entered number
     */
    private String executeEditOperation(EditOperation operation) {
        resetAfterError();

        if (operation == CLEAN) {
            valueProcessor.cleanAll();
            isErrorOccurred = false;
            if (isMemoryStorageShown) {
                showOrHideMemoryPane();
            }

        } else if (operation == CLEAN_CURRENT) {
            valueProcessor.cleanCurrent();
            isErrorOccurred = false;
        }

        String lastEnteredNumber = formatEnteredNumber(valueProcessor.getLastNumber(), false);
        if (operation == LEFT_ERASE) {

            boolean isLastSymbolPoint = valueProcessor.deleteLastDigit();
            lastEnteredNumber = formatEnteredNumber(valueProcessor.getLastNumber(), isLastSymbolPoint);
        }

        return lastEnteredNumber;
    }

    /**
     * Resets after error content of text fields to default values and enables all buttons that must be active by default.
     */
    private void resetAfterError() {
        if (isErrorOccurred) {
            enableAllOperations(true);
            String numberToReset = formatNumberWithGroupDelimiters(valueProcessor.getLastNumber());
            setCurrentNumber(numberToReset);
            updateHistoryExpression();
            isErrorOccurred = false;
        }
    }

    /**
     * Returns a message with information about exception and disables all buttons with mathematical
     * operations, including memory add, subtract and store buttons.
     *
     * @param e an occurred exception instance
     * @return a message with information about exception
     */
    private String handleException(Exception e) {
        enableMemoryStateButtons(false);
        enableAllOperations(false);
        isErrorOccurred = true;
        return e.getMessage();
    }

    /**
     * Sets the given number represented by string to the textfield contains current number.
     *
     * @param number the number to set
     */
    private void setCurrentNumber(String number) {
        Platform.runLater(() -> {
            currentNumberText.setText(number);
            currentNumberText.end();
            prevOperationsText.end();
        });
    }

    /**
     * Sets the given string containing mathematical expression to the appropriate textfield.
     */
    private void updateHistoryExpression() {
        String history = valueProcessor.getHistoryExpression();
//        String history = getHistoryExpression();
        Platform.runLater(() -> {
            prevOperationsText.setText(history);
            prevOperationsText.end();
        });
    }

    /**
     * Handles an event generated by showing a tooltip. Sets the location of tooltip relies on current mouse position.
     *
     * @param event an event generated by showing a tooltip
     */
    @FXML
    private void setTooltipPosition(Event event) {
        Tooltip tooltip = (Tooltip) event.getSource();
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        tooltip.setX(mouse.getX() - TOOLTIP_X_OFFSET);
        tooltip.setY(mouse.getY() - TOOLTIP_Y_OFFSET);
    }

    /**
     * Shows or hides a panel with calculator view types.
     */
    @FXML
    private void showOrHideViewPanel() {
        Timeline timeline;
        if (isViewPanelShown) { // if panel is already shown - hide it

            timeline = getTimeline(0, VIEW_PANEL_MAX_WIDTH, ANIMATION_DURATION, 0);

        } else { // if panel is invisible - show it
            // add list of calculator's view types
            viewTypesList.setItems(getListOfCalcTypes());
            viewTypesList.getSelectionModel().select(0);
            viewTypesPanel.setVisible(true);

            timeline = getTimeline(0, 0, ANIMATION_DURATION, VIEW_PANEL_MAX_WIDTH);
        }
        timeline.play();
        // add delay for animation complete
        PauseTransition pause = new PauseTransition(Duration.millis(ANIMATION_DURATION));
        pause.setOnFinished(event1 -> viewTypesPanel.setVisible(isViewPanelShown));
        pause.play();
        isViewPanelShown = !isViewPanelShown;
    }

    /**
     * Returns a {@link Timeline} instance configured with the specified parameters of start end end
     * animation duration and panel width.
     *
     * @param startDuration a start animation duration value in millis
     * @param startWidth    a start value of panel width
     * @param endDuration   an end animation duration value in millis
     * @param endWidth      an end value of panel width
     * @return a {@link Timeline} instance configured with the specified parameters of start end end
     * animation duration and panel width
     */
    private Timeline getTimeline(int startDuration, double startWidth, int endDuration, double endWidth) {
        return new Timeline(
                getKeyFrame(startDuration, startWidth),
                getKeyFrame(endDuration, endWidth));
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
    private ObservableList<Label> getListOfCalcTypes() {
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
     * Shows or hides a panel with memorized value.
     */
    @FXML
    private void showOrHideMemoryPane() {
        if (isMemoryStorageShown) { // if pane is already shown - hide it
            memoryStorage.setVisible(false);
            isMemoryStorageShown = false;
            enableMemoryStateButtons(true);
            enableAllButtons(true);
        } else { // if pane is invisible - show it
            memoryStorage.setVisible(true);
            isMemoryStorageShown = true;
            enableMemoryStateButtons(false);
            enableAllButtons(false);
        }
        Platform.runLater(() -> memoryShow.setDisable(false));

    }

    /**
     * Enables or disables all mathematical operations, including memory add, subtract and store buttons depends
     * on the specified boolean value.
     *
     * @param enable a boolean value shows to enable or disable buttons
     */
    private void enableAllOperations(boolean enable) {
        enableMemoryEditButtons(enable);
        Platform.runLater(() -> {
            for (Button button : buttonsWithFunctions.keySet()) {
                enableOperationButton(button, enable);
            }
        });
    }

    /**
     * Enables or disables memory store, add and subtract buttons depends on the specified boolean value.
     *
     * @param enable a boolean value shows to enable or disable buttons
     */
    private void enableMemoryEditButtons(boolean enable) {
        memoryAdd.setDisable(!enable);
        memorySubtract.setDisable(!enable);
        memoryStore.setDisable(!enable);
    }

    /**
     * Disables or enables memory recall, clean and store buttons depends on the specified boolean value.
     *
     * @param enable a boolean value shows to enable or disable buttons
     */
    private void enableMemoryStateButtons(boolean enable) {
        memoryRecall.setDisable(!enable);
        memoryClean.setDisable(!enable);
        memoryShow.setDisable(!enable);
    }

    /**
     * Disables or enables the given button.
     *
     * @param button a button to disable or enable
     * @param enable a boolean value shows to enable or disable buttons
     */
    private void enableOperationButton(Button button, boolean enable) {
        Object buttonFunction = buttonsWithFunctions.get(button);

        if (buttonFunction instanceof MathOperation && buttonFunction != RESULT ||
                POINT.equals(buttonFunction)) {

            button.setDisable(!enable);
        }
    }

    /**
     * Disables or enables all buttons with numbers and math operations.
     *
     * @param enable a boolean value shows to enable or disable buttons
     */
    private void enableAllButtons(boolean enable) {
        enableMemoryEditButtons(enable);
        Platform.runLater(() -> {
            for (Button button : buttonsWithFunctions.keySet()) {
                button.setDisable(!enable);
            }
        });
    }

    /**
     * Adds all buttons with its functions and key combinations that activate these buttons to the buttons storage.
     * The initialization executes only is storage is empty.
     */
    private void initializeButtons() {
        if (buttonsWithFunctions.size() != 0 || buttonsWithKeys.size() != 0) {
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
     * button to the buttons storage. Button's function means what role this button acts. For example, button is digit
     * or some operation.
     *
     * @param button         a button to add to storage
     * @param buttonFunction an object encapsulating button's role in application
     * @param keyCode        a key code that actives current button
     * @param modifiers      a modifiers to key code that actives current button
     */
    private void addButton(Button button, Object buttonFunction, KeyCode keyCode, Modifier... modifiers) {
        buttonsWithFunctions.put(button, buttonFunction);
        if (keyCode != null) {
            buttonsWithKeys.put(new KeyCodeCombination(keyCode, modifiers), button);
        }
    }
}