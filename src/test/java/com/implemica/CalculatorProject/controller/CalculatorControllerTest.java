package com.implemica.CalculatorProject.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


import java.util.concurrent.TimeoutException;

import static com.implemica.CalculatorProject.calculation.EditOperation.CLEAN;
import static com.implemica.CalculatorProject.calculation.EditOperation.CLEAN_CURRENT;
import static com.implemica.CalculatorProject.calculation.EditOperation.LEFT_ERASE;
import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.calculation.MemoryOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.EMPTY_VALUE;
import static javafx.scene.input.KeyCombination.ModifierValue.DOWN;
import static javafx.scene.input.KeyCombination.ModifierValue.UP;
import static org.junit.Assert.*;

public class CalculatorControllerTest extends ApplicationTest {

    private static final String CALCULATOR_VIEW_FILE = "/calc.fxml";

    private static final String CSS_FILE = "/winCalc.css";

    private static final String ICON_FILE = "/icon3.png";

    private static final String APPLICATION_NAME = "Calculator";

    private static final String RESULT_IS_UNDEFINED_MESSAGE = "Result is undefined";

    private FxRobot robot = new FxRobot();

    private TextField currentNumberText, prevOperationsText;
    private Button numZero, numOne, numTwo, numThree, numFour, numFive, numSix, numSeven, numEight, numNine;
    private Button point, resultButton, negate, add, subtract, multiply, divide, reverse, square, squareRoot, percent;
    private Button memoryClean, memoryRecall, memoryPlus, memoryMinus, memoryStore, memory;
    private Button cleanAll, cleanCurrent, leftErase;
    private Button mode, modeClose, infoButton, history;

    private VBox viewPanel;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new FXMLLoader().load(getClass().getResourceAsStream(CALCULATOR_VIEW_FILE));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(CSS_FILE);
        stage.setScene(scene);
        stage.setTitle(APPLICATION_NAME);
        stage.getIcons().add(new Image(ICON_FILE));
        stage.setResizable(true);
        stage.toFront();
        FxToolkit.registerPrimaryStage();
        stage.show();
    }

    @Before
    public void setUp() {
        // text fields
        currentNumberText = find("#currentNumberText");
        prevOperationsText = find("#prevOperationsText");

        // buttons with numbers
        numZero = find("0");
        numOne = find("1");
        numTwo = find("2");
        numThree = find("3");
        numFour = find("4");
        numFive = find("5");
        numSix = find("6");
        numSeven = find("7");
        numEight = find("8");
        numNine = find("9");

        // buttons with math operations
        point = find("#point");
        resultButton = find(EQUAL.getCode());
        negate = find(NEGATE.getCode());
        add = find(ADD.getCode());
        subtract = find(SUBTRACT.getCode());
        multiply = find(MULTIPLY.getCode());
        divide = find(DIVIDE.getCode());
        reverse = find(REVERSE.getCode());
        square = find(SQUARE.getCode());
        squareRoot = find(SQUARE_ROOT.getCode());
        percent = find(PERCENT.getCode());

        // buttons with memory operations
        memoryClean = find(MEMORY_CLEAN.getCode());
        memoryRecall = find(MEMORY_RECALL.getCode());
        memoryPlus = find(MEMORY_ADD.getCode());
        memoryMinus = find(MEMORY_SUBTRACT.getCode());
        memoryStore = find(MEMORY_STORE.getCode());
        memory = find(MEMORY_SHOW.getCode());

        // buttons with editing operations
        cleanAll = find(CLEAN.getCode());
        cleanCurrent = find(CLEAN_CURRENT.getCode());
        leftErase = find(LEFT_ERASE.getCode());

        // other buttons
        mode = find("#mode");
        modeClose = find("#modeClose");
        infoButton = find("#infoButton");
        history = find("#history");

        viewPanel = find("#viewPanel");
    }

    private <T extends Node> T find(final String query) {
        return lookup(query).query();
    }

    @Test
    public void testElementsExist() {

        // text fields
        testExistAndActive(currentNumberText, true, false);
        testExistAndActive(prevOperationsText, true, false);

        // buttons with numbers
        testExistAndActive(numZero, true, false);
        testExistAndActive(numOne, true, false);
        testExistAndActive(numTwo, true, false);
        testExistAndActive(numThree, true, false);
        testExistAndActive(numFour, true, false);
        testExistAndActive(numFive, true, false);
        testExistAndActive(numSix, true, false);
        testExistAndActive(numSeven, true, false);
        testExistAndActive(numEight, true, false);
        testExistAndActive(numNine, true, false);

        // buttons with math operations
        testExistAndActive(negate, true, false);
        testExistAndActive(point, true, false);
        testExistAndActive(resultButton, true, false);
        testExistAndActive(add, true, false);
        testExistAndActive(subtract, true, false);
        testExistAndActive(multiply, true, false);
        testExistAndActive(divide, true, false);
        testExistAndActive(reverse, true, false);
        testExistAndActive(square, true, false);
        testExistAndActive(squareRoot, true, false);
        testExistAndActive(percent, true, false);

        // buttons with editing operations
        testExistAndActive(cleanAll, true, false);
        testExistAndActive(cleanCurrent, true, false);
        testExistAndActive(leftErase, true, false);

        // buttons with memory operations
        testExistAndActive(memoryClean, true, true);
        testExistAndActive(memoryRecall, true, true);
        testExistAndActive(memoryPlus, true, false);
        testExistAndActive(memoryMinus, true, false);
        testExistAndActive(memoryStore, true, false);
        testExistAndActive(memory, true, true);

        // other elements
        testExistAndActive(mode, true, false);
        testExistAndActive(modeClose, false, false);
        testExistAndActive(infoButton, false, false);
        testExistAndActive(history, true, true);
        testExistAndActive(viewPanel, false, false);
    }

    private void testExistAndActive(Node element, boolean expectedVisible, boolean expectedDisable) {
        assertNotNull(element);
        if (expectedVisible) {
            assertTrue(FXTestUtils.isNodeVisible(element));
        } else {
            assertFalse(FXTestUtils.isNodeVisible(element));
        }
        if (expectedDisable) {
            assertTrue(element.isDisable());
        } else {
            assertFalse(element.isDisable());
        }
    }

    @Test
    public void testViewPanel() {
        assertFalse(viewPanel.isVisible());
        robot.clickOn(mode);
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(viewPanel.isVisible());

        final ListView typesList = find("#viewTypes");
        assertNotNull(typesList);
        robot.clickOn(typesList);
        robot.scroll(VerticalDirection.DOWN);
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn(infoButton);

        robot.clickOn(modeClose);
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(viewPanel.isVisible());
    }

    @Test
    public void testButtonClicked() {
        // testViewPanel clicking for visible buttons
        testButtonClicked("0", cleanAll);
        testButtonClicked("1", numOne);
        testButtonClicked("12", numTwo);
        testButtonClicked("123", numThree);

        // try to click invisible button
        numOne.setVisible(false);
        testButtonClicked("123", numOne);
        testButtonClicked("1,233", numThree);
        testButtonClicked("0", cleanAll);
        numOne.setVisible(true);

        // test subtract operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", subtract);
        testExpression("5 " + SUBTRACT.getCode());
        testButtonClicked("9", numNine);
        testButtonClicked("-4", resultButton);
        testExpression(EMPTY_VALUE);


        // test multiply operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", multiply);
        testExpression("5 " + MULTIPLY.getCode());
        testButtonClicked("9", numNine);
        testButtonClicked("45", resultButton);
        testExpression(EMPTY_VALUE);

        // test divide operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", divide);
        testExpression("5 " + DIVIDE.getCode());
        testButtonClicked("9", numNine);
        testButtonClicked("0.5555555555555556", resultButton);
        testExpression(EMPTY_VALUE);

        // test percentage
        testButtonClicked("5", numFive);
        testButtonClicked("5", add);
        testExpression("5 " + ADD.getCode());
        testButtonClicked("9", numNine);
        testButtonClicked("0.45", percent);
        testButtonClicked("5.45", resultButton);
        testExpression(EMPTY_VALUE);

        // test square root operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", add);
        testExpression("5 " + ADD.getCode());
        testButtonClicked("9", numNine);
        testButtonClicked("3", squareRoot);
        testButtonClicked("8", resultButton);
        testExpression(EMPTY_VALUE);

        // test square operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", add);
        testExpression("5 " + ADD.getCode());
        testButtonClicked("9", numNine);
        testButtonClicked("81", square);
        testButtonClicked("86", resultButton);
        testExpression(EMPTY_VALUE);

        // test reverse operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", add);
        testExpression("5 " + ADD.getCode());
        testButtonClicked("9", numNine);
        testButtonClicked("0.1111111111111111", reverse);
        testButtonClicked("5.111111111111111", resultButton);
        testExpression(EMPTY_VALUE);

        // test negate operation
        testButtonClicked("1", numOne);
        testButtonClicked("-1", negate);
        testButtonClicked("0", leftErase);
    }

    private void testButtonClicked(String expectedText, Node button) {
        robot.clickOn(button);
        assertEquals(expectedText, currentNumberText.getText());
    }

    @Test
    public void testMemoryOperations() {
        // with empty memorized value these buttons must be disabled
        assertTrue(memoryClean.isDisabled());
        assertTrue(memoryRecall.isDisabled());
        assertTrue(memory.isDisabled());

        // enter the number and add it to memory
        testButtonClicked("9", numNine);
        testButtonClicked("9", memoryPlus);

        // check buttons became enable
        assertFalse(memoryClean.isDisabled());
        assertFalse(memoryRecall.isDisabled());
        assertFalse(memory.isDisabled());

        // enter new number and recall memorized value
        testButtonClicked("5", numFive);
        testButtonClicked("9", memoryRecall);

        // clean memory and check buttons became disable
        testButtonClicked("9", memoryClean);
        assertTrue(memoryClean.isDisabled());
        assertTrue(memoryRecall.isDisabled());
        assertTrue(memory.isDisabled());
    }

    @Test
    public void testKeyPressed() {
        // enter the number
        testKeyPressed("0", new KeyCodeCombination(KeyCode.ESCAPE));
        testKeyPressed("1", new KeyCodeCombination(KeyCode.DIGIT1));
        testKeyPressed("12", new KeyCodeCombination(KeyCode.DIGIT2));
        testKeyPressed("123", new KeyCodeCombination(KeyCode.DIGIT3));
        testKeyPressed("1,234", new KeyCodeCombination(KeyCode.DIGIT4));
        testKeyPressed("12,345", new KeyCodeCombination(KeyCode.DIGIT5));
        testKeyPressed("123,456", new KeyCodeCombination(KeyCode.DIGIT6));
        testKeyPressed("1,234,567", new KeyCodeCombination(KeyCode.DIGIT7));
        testKeyPressed("12,345,678", new KeyCodeCombination(KeyCode.DIGIT8));
        testKeyPressed("123,456,789", new KeyCodeCombination(KeyCode.DIGIT9));
        testKeyPressed("1,234,567,890", new KeyCodeCombination(KeyCode.DIGIT0));

        // press an operation
        testKeyPressed("1,234,567,890", new KeyCodeCombination(KeyCode.EQUALS, DOWN,
                UP, UP, UP, UP));
        testExpression("1234567890 " + ADD.getCode());

        // enter new number
        testKeyPressed("9", new KeyCodeCombination(KeyCode.DIGIT9));
        testKeyPressed("98", new KeyCodeCombination(KeyCode.DIGIT8));
        testKeyPressed("987", new KeyCodeCombination(KeyCode.DIGIT7));
        testKeyPressed("9,876", new KeyCodeCombination(KeyCode.DIGIT6));
        testKeyPressed("98,765", new KeyCodeCombination(KeyCode.DIGIT5));
        testKeyPressed("987,654", new KeyCodeCombination(KeyCode.DIGIT4));
        testKeyPressed("9,876,543", new KeyCodeCombination(KeyCode.DIGIT3));
        testKeyPressed("98,765,432", new KeyCodeCombination(KeyCode.DIGIT2));
        testKeyPressed("987,654,321", new KeyCodeCombination(KeyCode.DIGIT1));
        testKeyPressed("987,654,321.", new KeyCodeCombination(KeyCode.PERIOD));
        testKeyPressed("987,654,321.0", new KeyCodeCombination(KeyCode.DIGIT0));
        testKeyPressed("987,654,321.09", new KeyCodeCombination(KeyCode.DIGIT9));

        // calculate result few times
        testKeyPressed("2,222,222,211.09", new KeyCodeCombination(KeyCode.ENTER));
        testKeyPressed("3,209,876,532.18", new KeyCodeCombination(KeyCode.ENTER));
        testKeyPressed("4,197,530,853.27", new KeyCodeCombination(KeyCode.ENTER));
        testExpression(EMPTY_VALUE);

        // test subtract operation
        testKeyPressed("5", new KeyCodeCombination(KeyCode.DIGIT5));
        testKeyPressed("5", new KeyCodeCombination(KeyCode.MINUS));
        testExpression("5 " + SUBTRACT.getCode());
        testKeyPressed("9", new KeyCodeCombination(KeyCode.DIGIT9));
        testKeyPressed("-4", new KeyCodeCombination(KeyCode.ENTER));
        testExpression(EMPTY_VALUE);
        testKeyPressed("0", new KeyCodeCombination(KeyCode.BACK_SPACE));

        // test multiply operation
        testKeyPressed("5", new KeyCodeCombination(KeyCode.DIGIT5));
        testKeyPressed("5", new KeyCodeCombination(KeyCode.MULTIPLY));
        testExpression("5 " + MULTIPLY.getCode());
        testKeyPressed("9", new KeyCodeCombination(KeyCode.DIGIT9));
        testKeyPressed("45", new KeyCodeCombination(KeyCode.ENTER));
        testExpression(EMPTY_VALUE);
        testKeyPressed("4", new KeyCodeCombination(KeyCode.BACK_SPACE));

        // test divide operation
        testKeyPressed("5", new KeyCodeCombination(KeyCode.DIGIT5));
        testKeyPressed("5", new KeyCodeCombination(KeyCode.DIVIDE));
        testExpression("5 " + DIVIDE.getCode());
        testKeyPressed("9", new KeyCodeCombination(KeyCode.DIGIT9));
        testKeyPressed("0.5555555555555556", new KeyCodeCombination(KeyCode.ENTER));
        testExpression(EMPTY_VALUE);

        // test percentage
        testKeyPressed("5", new KeyCodeCombination(KeyCode.DIGIT5));
        testKeyPressed("5", new KeyCodeCombination(KeyCode.ADD));
        testExpression("5 " + ADD.getCode());
        testKeyPressed("9", new KeyCodeCombination(KeyCode.DIGIT9));
        testKeyPressed("0.45", new KeyCodeCombination(KeyCode.DIGIT5, DOWN, UP, UP, UP, UP));
        testKeyPressed("5.45", new KeyCodeCombination(KeyCode.ENTER));
        testExpression(EMPTY_VALUE);

        // test square root operation
        testKeyPressed("5", new KeyCodeCombination(KeyCode.NUMPAD5));
        testKeyPressed("5", new KeyCodeCombination(KeyCode.ADD));
        testExpression("5 " + ADD.getCode());
        testKeyPressed("9", new KeyCodeCombination(KeyCode.DIGIT9));
        testKeyPressed("3", new KeyCodeCombination(KeyCode.DIGIT2, DOWN, UP, UP, UP, UP));
        testKeyPressed("8", new KeyCodeCombination(KeyCode.ENTER));
        testExpression(EMPTY_VALUE);
        testKeyPressed("0", new KeyCodeCombination(KeyCode.BACK_SPACE));
    }

    private void testKeyPressed(String expectedText, KeyCodeCombination combination) {
        robot.push(combination);
        assertEquals(expectedText, currentNumberText.getText());
    }

    private void testExpression(String expected) {
        assertEquals(expected, prevOperationsText.getText());
    }

    @Test
    public void testPressDisabledButtons() {
        // cause an exception by dividing zero by zero to disable buttons with operations
        testKeyPressed("0", new KeyCodeCombination(KeyCode.ESCAPE));
        testKeyPressed("0", new KeyCodeCombination(KeyCode.DIGIT0));
        testKeyPressed("0", new KeyCodeCombination(KeyCode.DIVIDE));
        testKeyPressed("0", new KeyCodeCombination(KeyCode.DIGIT0));
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, new KeyCodeCombination(KeyCode.EQUALS));

        // try to press disabled operations from keyboard. Expected no changes in text field
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, new KeyCodeCombination(KeyCode.EQUALS, DOWN,
                UP, UP, UP, UP));
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, new KeyCodeCombination(KeyCode.MINUS));
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, new KeyCodeCombination(KeyCode.DIVIDE));
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, new KeyCodeCombination(KeyCode.DIGIT8, DOWN,
                UP, UP, UP, UP));
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, new KeyCodeCombination(KeyCode.DIGIT2, DOWN,
                UP, UP, UP, UP));
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, new KeyCodeCombination(KeyCode.DIGIT5, DOWN,
                UP, UP, UP, UP));

        // try to press disabled operations by mouse button. Expected no changes in text field
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, negate);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, point);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, add);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, subtract);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, multiply);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, divide);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, reverse);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, square);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, squareRoot);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, percent);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, memoryClean);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, memoryRecall);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, memoryPlus);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, memoryMinus);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, memoryStore);
        testButtonClicked(RESULT_IS_UNDEFINED_MESSAGE, memory);

        // clean error message and try to press some buttons with operations
        testButtonClicked("0", cleanAll);
        testButtonClicked("0.", point);
        testKeyPressed("0.5", new KeyCodeCombination(KeyCode.DIGIT5));
        testKeyPressed("0.7071067811865475", new KeyCodeCombination(KeyCode.DIGIT2, DOWN,
                UP, UP, UP, UP));
    }

    @After
    public void tearDown() throws TimeoutException {
        Platform.runLater(() -> FxToolkit.toolkitContext().getRegisteredStage().close());
    }
}
