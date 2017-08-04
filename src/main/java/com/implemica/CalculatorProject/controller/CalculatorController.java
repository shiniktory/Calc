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
import javafx.scene.text.Text;
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
    private InputValueProcessor valueProcessor = new InputValueProcessor();

    /**
     * The value of a default font for the text field with current number.
     */
    private static final Font DEFAULT_FONT = Font.font("Segoe UI Semibold", FontWeight.BOLD, 42);

    /**
     * The string value of a prefix for numpad digits.
     */
    private static final String NUMPAD_PREFIX = "Numpad ";

    /**
     * The list of calculator types.
     */
    private static final String[] CALCULATOR_TYPES = new String[]{"\tStandard", "\tScientific", "\tProgrammer", "\tDate calculation", "\tCONVERTER", "\tVolume", "\tLength",
            "\tWeight and Mass", "\tTemperature", "\tEnergy", "\tArea", "\tSpeed", "\tTime", "\tPower", "\tData", "\tPressure", "\tAngle"};

    /**
     * The count of milliseconds for animation duration.
     */
    private static final int ANIMATION_DURATION = 75;

    /**
     * The value of width for panel with calculator types.
     */
    private static final double VIEW_PANEL_WIDTH = 257.0;

    /**
     * The flag variable shows is panel with calculator types shown right now.
     */
    private boolean isViewPanelShown = false;

    private boolean isMemoryStorageShown = false;

    private static final Font CONVERTER_FONT = Font.font("Segoi UI", FontWeight.BOLD, 15);


    /**
     * An instance of text field with a value of current number value.
     */
    @FXML
    private TextField currentNumberText;

    /**
     * An instance of text field with a string containing previous number and arithmetic operation.
     */
    @FXML
    private TextField prevOperationsText;

    @FXML
    private VBox viewPanel;

    @FXML
    private ListView<Label> viewTypes;

    @FXML
    private GridPane numbersAndOperations;

    @FXML
    private GridPane memoryButtonsPane;

    @FXML
    private Button mc, mr, m, mPlus, mMinus, ms;

    @FXML
    private Pane memoryStorage;

    private boolean isErrorOccurred = false;

    private void searchAndFireButton(String code) {
        numbersAndOperations.requestFocus();
        String keyCode = code;
        if (code.contains(NUMPAD_PREFIX)) {
            keyCode = code.substring(NUMPAD_PREFIX.length());
        }
        // Search in node with numbers and operations
        boolean found = false;
        for (Node child : numbersAndOperations.getChildren()) {
            found = checkAndFire(keyCode, child);
        }
        if (!found) {
            // Search in node with memory operations
            for (Node child : memoryButtonsPane.getChildren()) {
                checkAndFire(keyCode, child);
            }
        }
    }

    private boolean checkAndFire(String buttonId, Node child) {
        Button button = (Button) child;
        if (button.getText().equals(buttonId) &&
                !button.isDisable() &&
                button.isVisible()) {
            addButtonClickedEffect(button);
            button.fire();
            return true;
        }
        return false;
    }

    private void addButtonClickedEffect(Button button) {
        button.arm();
        // add delay to view a button pressed effect
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
        hideViewPanel();
        resetAfterError();
        String buttonValue = getNumberFromEvent(event);
        valueProcessor.updateCurrentNumber(buttonValue);
        updateCurrentNumberField();
    }

    /**
     * Extracts from occurred event a value of input number represented as string.
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
            textToSet = e.getMessage();
            disableAllOperations();
            isErrorOccurred = true;
        }
        fitText(textToSet);
        currentNumberText.setText(textToSet);
    }

    /**
     * Handles all events generated by keyboard key pressing.
     *
     * @param event an event instance generated by key pressing
     */
    @FXML
    private void keyEventHandler(KeyEvent event) {
        KeyCode key = event.getCode();
        hideViewPanel();

        // Unary operations
        if (event.isShiftDown() && key == KeyCode.DIGIT2) {
            searchAndFireButton(SQUARE_ROOT.getCode());

            //Binary operations
        } else if (event.isShiftDown() && key == KeyCode.DIGIT5) {
            searchAndFireButton(PERCENT.getCode());

        } else if (key == KeyCode.MINUS || key == KeyCode.SUBTRACT) {
            searchAndFireButton(SUBTRACT.getCode());

        } else if (key == KeyCode.ADD ||
                event.isShiftDown() && key == KeyCode.EQUALS) {
            searchAndFireButton(ADD.getCode());

        } else if (key == KeyCode.MULTIPLY ||
                event.isShiftDown() && key == KeyCode.DIGIT8) {
            searchAndFireButton(MULTIPLY.getCode());

        } else if (key == KeyCode.SLASH || key == KeyCode.DIVIDE) {
            searchAndFireButton(DIVIDE.getCode());

        } else if (key == KeyCode.BACK_SPACE) {
            searchAndFireButton(LEFT_ERASE.getCode());

        } else if (key == KeyCode.EQUALS) {
            searchAndFireButton(EQUAL.getCode());
        } else if (key == KeyCode.ENTER) {
            searchAndFireButton(EQUAL.getCode());
            if (isMemoryStorageShown) {
                showOrHideMemoryPane();
            }

            // Digits and point
        } else if (key.isDigitKey()) {
            searchAndFireButton(key.getName());
        } else if (key == KeyCode.PERIOD || key == KeyCode.DECIMAL) {
            addPoint();
        } else if (key == KeyCode.SPACE || key == KeyCode.ESCAPE) {
            searchAndFireButton(CLEAN.getCode());
            if (isMemoryStorageShown) {
                showOrHideMemoryPane();
            }
        }
    }

    /**
     * Handles events generated by Mouse clicks on buttons with operations.
     *
     * @param event an event instance generated by Mouse click on button with operation
     */
    @FXML
    private void operationsHandler(Event event) {
        hideViewPanel();
        MathOperation operation = getOperationFromEvent(event);
        if (operation == null) {
            return;
        }
        String textToSet;
        try {
            textToSet = valueProcessor.executeMathOperation(operation);
        } catch (CalculationException e) {
            textToSet = e.getMessage();
            disableAllOperations();
            isErrorOccurred = true;
        }
        fitText(textToSet);
        currentNumberText.setText(textToSet);
        currentNumberText.end();
        prevOperationsText.setText(valueProcessor.getExpression());
        prevOperationsText.end();
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
        String operationSymbol = buttonClicked.getText();
        return MathOperation.getOperation(operationSymbol);
    }

    /**
     * Updates values in text fields with current number and previous operations.
     */
    private void updateTextFields() {
        prevOperationsText.setText(valueProcessor.getExpression());
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
            textToSet = e.getMessage();
            valueProcessor.cleanAll();
            disableAllOperations();
            isErrorOccurred = true;
        }
        fitText(textToSet);
        currentNumberText.setText(textToSet);
        currentNumberText.end();
        if (!isErrorOccurred) {
            prevOperationsText.clear();
        }
    }

    private void resetAfterError() {
        if (isErrorOccurred) {
            enableAllOperations();
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
            textToSet = e.getMessage();
            valueProcessor.cleanAll();
            disableAllOperations();
            isErrorOccurred = true;
        }
        fitText(textToSet);
        currentNumberText.setText(textToSet);
    }

    /**
     * Cleans all text fields and saved values.
     */
    @FXML
    private void cleanAll() {
        resetAfterError();
        valueProcessor.cleanAll();
        updateTextFields();
        enableAllOperations();
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
        enableAllOperations();
        isErrorOccurred = false;
    }

    /**
     * Deletes the last symbol in the current entered number.
     */
    @FXML
    private void deleteLastNumber() {
        resetAfterError();
        try {
            valueProcessor.deleteLastDigit();
            updateCurrentNumberField();
        } catch (CalculationException e) {
            fitText(e.getMessage());
        }

    }

    /**
     * Handles events generated by mouse click on memory buttons.
     *
     * @param event an instance of event generated by mouse click on memory buttons
     */
    @FXML
    private void memoryButtonHandler(Event event) {
        String operationCode = ((Button) event.getSource()).getText();
        MemoryOperation operation = MemoryOperation.getOperation(operationCode);
        String textToSet;
        try {
            valueProcessor.executeMemoryOperation(operation);
            textToSet = valueProcessor.getLastNumber();
            enableMemoryButtons();
        } catch (CalculationException e) {
            textToSet = e.getMessage();
        }
        if (operation == MEMORY_CLEAN) {
            disableMemoryButtons();
        }
        fitText(textToSet);
        currentNumberText.setText(textToSet);
        numbersAndOperations.requestFocus();
    }

    private void enableMemoryButtons() {
        mr.setDisable(false);
        mc.setDisable(false);
        m.setDisable(false);
        numbersAndOperations.requestFocus();
    }

    private void disableMemoryButtons() {
        mr.setDisable(true);
        mc.setDisable(true);
        m.setDisable(true);
        numbersAndOperations.requestFocus();
    }

    private void disableAllOperations() {
        disableMemoryButtons();
        mPlus.setDisable(true);
        mMinus.setDisable(true);
        ms.setDisable(true);
        for (Node children : numbersAndOperations.getChildren()) {
            Button thisButton = (Button) children;
            if (MathOperation.getOperation(thisButton.getText()) != null &&
                    !thisButton.getText().equals(EQUAL.getCode()) ||
                    POINT.equals(thisButton.getText())) {
                thisButton.setDisable(true);
            }
        }
    }

    private void enableAllOperations() {
        mPlus.setDisable(false);
        mMinus.setDisable(false);
        ms.setDisable(false);
        for (Node children : numbersAndOperations.getChildren()) {
            Button thisButton = (Button) children;
            if (MathOperation.getOperation(thisButton.getText()) != null || POINT.equals(thisButton.getText())) {
                thisButton.setDisable(false);
            }
        }
    }

    private void fitText(String currentText) {
        currentNumberText.setFont(DEFAULT_FONT);

        Text text = new Text(currentText);
        text.setFont(currentNumberText.getFont());
        currentNumberText.applyCss();

        double textWidth = text.getLayoutBounds().getWidth();
        double scale = currentNumberText.getBoundsInLocal().getWidth() / textWidth - 0.1;
        if (scale < 1.0) {
            currentNumberText.setFont(new Font(currentNumberText.getFont().getFamily(), currentNumberText.getFont().getSize() * scale));
        }
    }

    @FXML
    private void setTooltipPosition(Event event) {
        Tooltip tooltip = (Tooltip) event.getSource();
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        tooltip.setX(mouse.getX() - 50);
        tooltip.setY(mouse.getY() - 55);
        numbersAndOperations.requestFocus();
    }

    @FXML
    private void showViewPanel() {

        List<Label> labelList = new LinkedList<>();

        for (String type : CALCULATOR_TYPES) {
            Label label = new Label(type);
            if ("\tCONVERTER".equals(type)) {
                label.setFont(CONVERTER_FONT);
            }
            labelList.add(label);

        }
        viewTypes.setItems(FXCollections.observableList(labelList));
        viewTypes.getSelectionModel().select(0);

        viewPanel.setVisible(true);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(viewPanel.prefWidthProperty(), 0.0)),
                new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(viewPanel.prefWidthProperty(), VIEW_PANEL_WIDTH))
        );

        timeline.play();
        isViewPanelShown = true;
        numbersAndOperations.requestFocus();
    }


    @FXML
    private void hideViewPanel() {
        if (isViewPanelShown) {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(viewPanel.prefWidthProperty(), VIEW_PANEL_WIDTH)),
                    new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(viewPanel.prefWidthProperty(), 0.0))
            );
            timeline.play();
            PauseTransition pause = new PauseTransition(Duration.millis(ANIMATION_DURATION));
            pause.setOnFinished(event1 -> viewPanel.setVisible(false));
            pause.play();
            isViewPanelShown = false;
        }
        numbersAndOperations.requestFocus();
    }

    @FXML
    private void showOrHideMemoryPane() {
        if (isMemoryStorageShown) {
            memoryStorage.setVisible(false);
            isMemoryStorageShown = false;
            enableAllOperations();
            mc.setDisable(false);
            mr.setDisable(false);
            disableAllButtons(false);
        } else {
            memoryStorage.setVisible(true);
            disableAllOperations();
            m.setDisable(false);
            disableAllButtons(true);

            isMemoryStorageShown = true;
        }
    }

    private void disableAllButtons(boolean disable) {
        for (Node node : numbersAndOperations.getChildren() ) {
            node.setDisable(disable);
        }
    }
}