package com.implemica.CalculatorProject.controller;

import com.implemica.CalculatorProject.calculation.MathOperation;
import com.implemica.CalculatorProject.calculation.MemoryOperation;
import com.implemica.CalculatorProject.exception.CalculationException;
import com.implemica.CalculatorProject.processing.InputValueProcessor;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.implemica.CalculatorProject.calculation.EditOperation.*;
import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.calculation.MemoryOperation.MEMORY_CLEAN;
import static com.implemica.CalculatorProject.util.OutputFormatter.EMPTY_VALUE;
import static com.implemica.CalculatorProject.util.OutputFormatter.POINT;

/**
 * The {@code CalculatorController} class is a chain between calculator's view and calculations. It accepts and handles
 * events generated by mouse clicks or keyboard keys' pressings.
 *
 * @author V. Kozina-Kravchenko
 */
public class CalculatorController {

    /**
     * An instance of {@link InputValueProcessor} that accepts, transforms and calculates the results for an
     * input data.
     */
    private final InputValueProcessor valueProcessor = new InputValueProcessor();

    /**
     * The value of a default font for the text field with current number.
     */
    private static final Font DEFAULT_FONT = Font.font("Segoe UI Semibold", FontWeight.BOLD, 42);

    /**
     * The string value of a prefix for numpad digits.
     */
    private static final String NUMPAD_PREFIX = "Numpad ";

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
     * The flag variable shows is panel with calculator types shown right now.
     */
    private boolean isViewPanelShown = false;

    /**
     * The flag variable shows is panel with stored in memory values shown right now.
     */
    private boolean isMemoryStorageShown = false;

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
    private ListView<Label> viewTypes;

    /**
     * A reference to the {@link GridPane} contains {@link Button}s with digits, mathematical operations and
     * some operations to edit entered values.
     */
    @FXML
    private GridPane numbersAndOperations;

    /**
     * A references to the buttons allows to work with memorized numbers.
     */
    @FXML
    private Button mc, mr, m, mPlus, mMinus, ms;

    /**
     * A reference to the {@link Pane} shows a memorized number.
     */
    @FXML
    private Pane memoryStorage;

    /**
     * The flag variable shows is error occurred while calculations.
     */
    private boolean isErrorOccurred = false;

    /**
     * Sets the given number represented by string to the textfield contains current number.
     *
     * @param number the number to set
     */
    private void setCurrentNumber(String number) {
        currentNumberText.setText(number);
        currentNumberText.end();
        prevOperationsText.end();
    }

    /**
     * Sets the given string containing mathematical expression to the appropriate textfield.
     *
     * @param mathExpression a string with expression to set
     */
    private void setMathExpression(String mathExpression) {
        prevOperationsText.setText(mathExpression);
        prevOperationsText.end();
    }

    /**
     * Handles all events generated by keyboard key pressing.
     *
     * @param event an event instance generated by key pressing
     */
    @FXML
    private void keyEventHandler(KeyEvent event) {
        if (isViewPanelShown) {
            showOrHideViewPanel();
        }
        KeyCode key = event.getCode();

        // Unary operations
        if (event.isShiftDown() && key == KeyCode.DIGIT2) {
            fireButton(SQUARE_ROOT.name());

        } else if (key == KeyCode.Q) {
            fireButton(SQUARE.name());

        } else if (key == KeyCode.R) {
            fireButton(REVERSE.name());

            //Binary operations
        } else if (event.isShiftDown() && key == KeyCode.DIGIT5) {
            fireButton(PERCENT.name());

        } else if (key == KeyCode.MINUS || key == KeyCode.SUBTRACT) {
            fireButton(SUBTRACT.name());

        } else if (key == KeyCode.ADD ||
                event.isShiftDown() && key == KeyCode.EQUALS) {
            fireButton(ADD.name());

        } else if (key == KeyCode.MULTIPLY ||
                event.isShiftDown() && key == KeyCode.DIGIT8) {
            fireButton(MULTIPLY.name());

        } else if (key == KeyCode.SLASH || key == KeyCode.DIVIDE) {
            fireButton(DIVIDE.name());

        } else if (key == KeyCode.BACK_SPACE) {
            fireButton(LEFT_ERASE.name());

        } else if (key == KeyCode.EQUALS) {
            fireButton(RESULT.name());

        } else if (key == KeyCode.ENTER) {
            fireButton(RESULT.name());
            if (isMemoryStorageShown) {
                showOrHideMemoryPane();
            }

            // Digits and point
        } else if (key.isDigitKey()) {
            fireButton(key.getName());

        } else if (key == KeyCode.PERIOD || key == KeyCode.DECIMAL) {
            fireButton("point");

        } else if (key == KeyCode.SPACE || key == KeyCode.ESCAPE) {
            fireButton(CLEAN.name());
            if (isMemoryStorageShown) {
                showOrHideMemoryPane();
            }
        }
    }

    /**
     * Searches a {@link Button} by the given code. If find fires an {@link ActionEvent}
     * and adds a button clicked visual effect for it.
     *
     * @param code a string value of a code to find button
     */
    private void fireButton(String code) {
        String keyCode = code;
        if (code.contains(NUMPAD_PREFIX)) {
            keyCode = code.substring(NUMPAD_PREFIX.length());
        }
        for (Node child : numbersAndOperations.getChildren()) {
            fireButtonImpl(keyCode, child);
        }
    }

    /**
     * Checks the given button for an equivalence it's text and specified code. If they are
     * equal and this button is active and visible fires an {@link ActionEvent}.
     *
     * @param buttonId the required button's code
     * @param child    the button to check and fire
     */
    private void fireButtonImpl(String buttonId, Node child) {
        Button button = (Button) child;
        String currentButtonId = button.getId();
        if (currentButtonId.equalsIgnoreCase(buttonId) &&
                !button.isDisable() &&
                button.isVisible()) {

            addButtonClickedEffect(button);
            button.fire();
        }
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
     * Reads a value of the current number from calculator's text field.
     *
     * @param event an instance of event generated after button with number pressed.
     */
    @FXML
    private void readCurrentNumber(Event event) {
        if (isViewPanelShown) {
            showOrHideViewPanel();
        }
        resetAfterError();
        String buttonValue = getNumberFromEvent(event);
        valueProcessor.updateCurrentNumber(buttonValue);
        updateTextFields();
    }

    /**
     * Extracts from generated event a value of input number represented by string.
     *
     * @param event an instance of occurred event
     * @return a value of input number represented as string
     */
    private String getNumberFromEvent(Event event) {
        if (event instanceof MouseEvent || event instanceof ActionEvent) {
            return ((Button) event.getSource()).getText();
        }
        return EMPTY_VALUE;
    }

    /**
     * Updates a value of current number in the appropriate text field.
     */
    private void updateCurrentNumberField() {
        String textToSet;
        try {
            textToSet = valueProcessor.getLastNumber();
        } catch (CalculationException e) {
            textToSet = handleException(e);
        }
        setCurrentNumber(textToSet);
    }

    /**
     * Returns a message with information about exception and disables all buttons with mathematical
     * operations.
     *
     * @param e an occurred exception instance
     * @return a message with information about exception
     */
    private String handleException(Exception e) {
        disableAllOperations(true);
        isErrorOccurred = true;
        return e.getMessage();
    }

    /**
     * Handles events generated by Mouse clicks on buttons with operations.
     *
     * @param event an event instance generated by Mouse click on button with operation
     */
    @FXML
    private void operationsHandler(Event event) {
        if (isViewPanelShown) {
            showOrHideViewPanel();
        }
        MathOperation operation = getOperationFromEvent(event);
        if (operation == null) {
            return;
        }
        String textToSet;
        try {
            textToSet = valueProcessor.executeMathOperation(operation);
        } catch (CalculationException e) {
            textToSet = handleException(e);
        }
        setCurrentNumber(textToSet);
        String mathExpression = valueProcessor.getExpression();
        setMathExpression(mathExpression);
        if (isErrorOccurred) {
            valueProcessor.cleanAll();
        }
    }

    /**
     * Extracts an {@link MathOperation} from the given event.
     *
     * @param event an instance of generated event
     * @return an {@link MathOperation} extracted from the given event
     */
    private MathOperation getOperationFromEvent(Event event) {
        Button buttonClicked = (Button) event.getSource();

        String operationSymbol = buttonClicked.getId();
        return MathOperation.getOperation(operationSymbol);
    }

    /**
     * Updates values in text fields with current number and previous operations.
     */
    private void updateTextFields() {
        String currentMathExpression = valueProcessor.getExpression();
        setMathExpression(currentMathExpression);
        updateCurrentNumberField();
    }

    /**
     * Handles an event generated by pressing the Calculate button. Calculates the result and shows it
     * in an appropriate text field.
     */
    @FXML
    private void calculateResult() {
        resetAfterError();
        String textToSet;
        try {
            textToSet = valueProcessor.calculateResult();
        } catch (CalculationException e) {
            textToSet = handleException(e);
            valueProcessor.cleanAll();
        }
        setCurrentNumber(textToSet);
        if (!isErrorOccurred) {
            prevOperationsText.clear();
        }
    }

    /**
     * Resets after error content of text fields to default values and enables all buttons that must be active by default.
     */
    private void resetAfterError() {
        if (isErrorOccurred) {
            disableAllOperations(false);
            updateTextFields();
            isErrorOccurred = false;
        }
    }

    /**
     * Adds a point symbol to the current number.
     */
    @FXML
    private void addPoint() {
        String textToSet;
        try {
            textToSet = valueProcessor.addPoint();
        } catch (CalculationException e) {
            textToSet = handleException(e);
            valueProcessor.cleanAll();
        }
        setCurrentNumber(textToSet);
        prevOperationsText.setText(valueProcessor.getExpression());
    }

    /**
     * Cleans all text fields and saved values.
     */
    @FXML
    private void cleanAll() {
        resetAfterError();
        valueProcessor.cleanAll();
        updateTextFields();
        disableAllOperations(false);
        isErrorOccurred = false;
    }

    /**
     * Cleans text field with the current entered number.
     */
    @FXML
    private void cleanCurrent() {
        resetAfterError();
        valueProcessor.cleanCurrent();
        updateCurrentNumberField();
        prevOperationsText.setText(valueProcessor.getExpression());
        isErrorOccurred = false;
    }

    /**
     * Deletes the last symbol in the current entered number.
     */
    @FXML
    private void deleteLastNumber() {
        resetAfterError();
        valueProcessor.deleteLastDigit();
        updateCurrentNumberField();
    }

    /**
     * Handles events generated by mouse click on memory buttons.
     *
     * @param event an instance of event generated by mouse click on memory buttons
     */
    @FXML
    private void memoryButtonHandler(Event event) {
        String buttonId = ((Button) event.getSource()).getId();
        MemoryOperation operation = MemoryOperation.getOperation(buttonId);
        String textToSet;
        try {
            valueProcessor.executeMemoryOperation(operation);
            textToSet = valueProcessor.getLastNumber();
            disableMemoryButtons(false);
        } catch (CalculationException e) {
            textToSet = handleException(e);
        }
        if (operation == MEMORY_CLEAN) {
            disableMemoryButtons(true);
        }
        setCurrentNumber(textToSet);
    }

    /**
     * Disables or enables memory recall, clean and store buttons depends on the specified boolean value.
     *
     * @param disable a boolean value shows disable or enable buttons
     */
    private void disableMemoryButtons(boolean disable) {
        mr.setDisable(disable);
        mc.setDisable(disable);
        m.setDisable(disable);
    }

    /**
     * Disables all memory operations or enables memory add, subtract and store buttons depends on the specified boolean
     * value.
     *
     * @param disable a boolean value shows disable or enable buttons
     */
    private void disableAllOperations(boolean disable) {
//        disableMemoryButtons(true); // TODO incorrect
        mPlus.setDisable(disable);
        mMinus.setDisable(disable);
        ms.setDisable(disable);
        for (Node children : numbersAndOperations.getChildren()) {
            Button button = (Button) children;
            disableOperationButton(button, disable);
        }
    }

    /**
     * Disables or enables the given button.
     *
     * @param button  a button to disable or enable
     * @param disable a boolean value shows disable or enable button
     */
    private void disableOperationButton(Button button, boolean disable) {
        String buttonId = button.getId();
        MathOperation operation = MathOperation.getOperation(buttonId);
        String buttonText = button.getText();
        if (operation != null && operation != RESULT ||
                POINT.equals(buttonText)) {
            button.setDisable(disable);
        }
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
        tooltip.setX(mouse.getX() - 50);
        tooltip.setY(mouse.getY() - 55);
    }

    /**
     * Shows or hides a panel with calculator view types.
     */
    @FXML
    private void showOrHideViewPanel() {
        Timeline timeline;
        if (!isViewPanelShown) {
            viewTypes.setItems(getListOfCalcTypes());
            viewTypes.getSelectionModel().select(0);

            viewTypesPanel.setVisible(true);
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(viewTypesPanel.prefWidthProperty(), 0.0)),
                    new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(viewTypesPanel.prefWidthProperty(),
                            VIEW_PANEL_MAX_WIDTH))
            );
            isViewPanelShown = true;
        } else {
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(viewTypesPanel.prefWidthProperty(),
                            VIEW_PANEL_MAX_WIDTH)),
                    new KeyFrame(Duration.millis(ANIMATION_DURATION),
                            new KeyValue(viewTypesPanel.prefWidthProperty(), 0.0))
            );
            isViewPanelShown = false;
        }
        timeline.play();
        PauseTransition pause = new PauseTransition(Duration.millis(ANIMATION_DURATION));
        pause.setOnFinished(event1 -> viewTypesPanel.setVisible(isViewPanelShown));
        pause.play();
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
        if (isMemoryStorageShown) {
            memoryStorage.setVisible(false);
            isMemoryStorageShown = false;
            disableAllOperations(false);
            disableAllButtons(false);
            mr.setDisable(false);
            mc.setDisable(false);
            m.setDisable(false);
        } else {
            memoryStorage.setVisible(true);
            isMemoryStorageShown = true;
            disableAllOperations(true);
            m.setDisable(false);
            disableAllButtons(true);
        }
    }

    /**
     * Disables or enables all buttons with numbers and math operations.
     *
     * @param disable a boolean value shows disable or enable buttons
     */
    private void disableAllButtons(boolean disable) {
        for (Node node : numbersAndOperations.getChildren()) {
            node.setDisable(disable);
        }
    }
}