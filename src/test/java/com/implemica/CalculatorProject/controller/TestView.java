package com.implemica.CalculatorProject.controller;

import com.implemica.CalculatorProject.calculation.*;
import com.implemica.CalculatorProject.validation.DataValidator;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.calculation.MemoryOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.POINT;
import static javafx.geometry.VerticalDirection.DOWN;
import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static org.junit.Assert.*;

public class TestView {

    private static final FxRobot robot = new FxRobot();

    private static final String RESULT_IS_UNDEFINED_MESSAGE = "Result is undefined";

    private static final String ARGUMENT_DELIMITERS = "\\s+[=\\s]*";

    private static TextField currentNumberText, prevOperationsText;

    private static ListView typesList;
    private static VBox viewPanel;
    private static BorderPane memoryStorage;

    private static Map<String, Button> buttons = new LinkedHashMap<>();

    @BeforeClass
    public static void setUpInit() throws Exception {

        WaitForAsyncUtils.waitForFxEvents();

        findAndPutButton(".", "#point");

        // init math operations
        for (MathOperation operation : MathOperation.values()) {
            String operationCode = operation.getCode();
            findAndPutButton(operationCode, operationCode);
        }

        // init memory operations
        for (MemoryOperation operation : MemoryOperation.values()) {
            String operationCode = operation.getCode();
            findAndPutButton(operationCode, operationCode);
        }

        // other buttons
        findAndPutButton("mode", "#mode");
        findAndPutButton("modeClose", "#modeClose");
        findAndPutButton("infoButton", "#infoButton");
        findAndPutButton("history", "#history");

        // text fields with current number and history
        currentNumberText = find("#currentNumberText");
        prevOperationsText = find("#prevOperationsText");

        // panels with calculator's view types and memorized values
        viewPanel = find("#viewTypesPanel");
        typesList = find("#viewTypes");
        memoryStorage = find("#memoryStorage");
    }

    private static void findAndPutButton(String buttonId, String query) {
        buttons.put(buttonId, find(query));
        WaitForAsyncUtils.waitForFxEvents();
    }

    private static <T extends Node> T find(final String query) {
        T node = robot.lookup(query).query();
        assertNotNull(node);
        return node;
    }

    @Before
    public void setUp() {
        // reset everything before each test
        pushKey(KeyCode.ESCAPE);
    }

    private void fireButton(String buttonId) {
        WaitForAsyncUtils.waitForFxEvents();
        final Button button = buttons.get(buttonId);
        button.fire();
    }

    private void clickButton(String buttonId) {
        Button button = buttons.get(buttonId);
        robot.clickOn(button);
    }

    private void testKeyPressed(String expectedText, KeyCode keyCode, Modifier... modifiers) {
        pushKey(keyCode, modifiers);
        assertEquals(expectedText, currentNumberText.getText());
    }

    private void pushKey(KeyCode keyCode, Modifier... modifiers) {
        KeyCodeCombination combination = new KeyCodeCombination(keyCode, modifiers);
        robot.push(combination);
    }

    @Test
    public void testPressDisabledButtons() {
        // cause an exception by dividing zero by zero to disable buttons with operations
        causeAnException();

        for (MathOperation operation : MathOperation.values()) {
            if (operation != RESULT) {
                testPressButtonAfterError(operation.getCode());
            }
        }
        for (MemoryOperation operation : MemoryOperation.values()) {
            testPressButtonAfterError(operation.getCode());
        }
        testPressButtonAfterError(POINT);
        testPressButtonAfterError("history");

        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.EQUALS, SHIFT_DOWN);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.MINUS);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIVIDE);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIGIT8, SHIFT_DOWN);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIGIT2, SHIFT_DOWN);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIGIT5, SHIFT_DOWN);

        // clean error message
        pushKey(KeyCode.ESCAPE);
        assertEquals("0", currentNumberText.getText());
    }

    private void causeAnException() {
        pushKey(KeyCode.ESCAPE);
        String[] expressionParts = "0 ÷ 0".trim().split(ARGUMENT_DELIMITERS);

        for (int i = 0; i < expressionParts.length; i++) {
            if (DataValidator.isDigit(expressionParts[i])) {
                pushKey(getKeyCode(expressionParts[i]));
            } else {
                fireButton(expressionParts[i]);
            }
        }
        pushKey(KeyCode.ENTER);

        assertEquals(RESULT_IS_UNDEFINED_MESSAGE, currentNumberText.getText());
    }

    private void testPressButtonAfterError(String buttonId) {
        fireButton(buttonId);
        clickButton(buttonId);
        assertEquals(RESULT_IS_UNDEFINED_MESSAGE, currentNumberText.getText());
    }

    @Test
    public void testViewPanel() {
        assertFalse(viewPanel.isVisible());
        clickButton("mode");
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(viewPanel.isVisible());

        // Verify is selected item changed
        Label firstSelected = (Label) typesList.getSelectionModel().getSelectedItem();
        robot.clickOn(typesList);
        Label secondSelected = (Label) typesList.getSelectionModel().getSelectedItem();
        assertNotEquals(firstSelected, secondSelected);

        robot.scroll(DOWN);
        clickButton("infoButton");

        // hide calculator view panel
        clickButton("modeClose");
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(viewPanel.isVisible());
    }

    @Test
    public void testMemoryPanel() {
        // by default memory panel must be invisible
        assertFalse(memoryStorage.isVisible());

        // show memory panel
        clickButton(MEMORY_STORE.getCode());
        clickButton(MEMORY_SHOW.getCode());
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(memoryStorage.isVisible());

        // try to press any digit or operation button
        for (int i = 0; i <= 9; i++) {
            KeyCode digitKey = getKeyCode(String.valueOf(i));
            testKeyPressed("0", digitKey);
        }
        for (MathOperation operation : MathOperation.values()) {
            fireButton(operation.getCode());
            assertEquals("0", currentNumberText.getText());
        }
        testKeyPressed("0", KeyCode.BACK_SPACE);

        // hide panel
        clickButton(MEMORY_SHOW.getCode());
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(viewPanel.isVisible());
    }


    @Test
    public void testFontResize() {
        double initialFontSize = currentNumberText.getFont().getSize();

        for (int i = 0; i < 15; i++) { // If text in field is too large font size must become smaller
            pushKey(KeyCode.DIGIT5);
        }
        double currentFontSize = currentNumberText.getFont().getSize();
        assertNotEquals(initialFontSize, currentFontSize);
    }

    @Test
    public void testWindowResize() {
        testWindowResize(100, 100);
        testWindowResize(-170, -100);
    }

    private void testWindowResize(double byX, double byY) {
        // remember initial parameters
        Window windowBeforeResize = robot.targetWindow();
        double startX = windowBeforeResize.getX();
        double startY = windowBeforeResize.getY();
        double initWidth = windowBeforeResize.getWidth();
        double initHeight = windowBeforeResize.getHeight();

        dragAndMove(startX, startY, byX, byY);
        testWindowParams(initWidth, initHeight);
    }

    private void dragAndMove(double startX, double startY, double byX, double byY) {
        robot.drag(startX, startY, MouseButton.PRIMARY);
        robot.moveBy(byX, byY);
        robot.drop();
    }

    private void testWindowParams(double startWidth, double startHeight) {
        Window window = robot.targetWindow();
        assertNotEquals(startWidth, window.getWidth());
        assertNotEquals(startHeight, window.getHeight());
    }

    @Test
    public void testWindowMove() {
        // remember initial parameters
        Window windowBeforeMove = robot.targetWindow();
        double startX = windowBeforeMove.getX();
        double startY = windowBeforeMove.getY();
        double initWidth = windowBeforeMove.getWidth();
        double initHeight = windowBeforeMove.getHeight();

        dragAndMove(startX + 70, startY + 10, 100, 100);

        // Assert that window coordinates changed but window width and height still the same
        Window windowAfterMove = robot.targetWindow();
        assertNotEquals(startX, windowAfterMove.getX());
        assertNotEquals(startY, windowAfterMove.getY());
        assertEquals(initWidth, windowAfterMove.getWidth(), 0.0001);
        assertEquals(initHeight, windowAfterMove.getHeight(), 0.0001);
    }
}
