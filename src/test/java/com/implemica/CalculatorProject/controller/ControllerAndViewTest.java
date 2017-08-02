package com.implemica.CalculatorProject.controller;

import com.implemica.CalculatorProject.CalcApplication;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.*;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


import static com.implemica.CalculatorProject.calculation.EditOperation.CLEAN;
import static com.implemica.CalculatorProject.calculation.EditOperation.CLEAN_CURRENT;
import static com.implemica.CalculatorProject.calculation.EditOperation.LEFT_ERASE;
import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.calculation.MemoryOperation.*;
import static com.implemica.CalculatorProject.util.OutputFormatter.EMPTY_VALUE;
import static javafx.scene.input.KeyCombination.ModifierValue.DOWN;
import static javafx.scene.input.KeyCombination.ModifierValue.UP;
import static org.junit.Assert.*;

public class ControllerAndViewTest extends ApplicationTest {

    private static final String RESULT_IS_UNDEFINED_MESSAGE = "Result is undefined";

    private FxRobot robot = new FxRobot();

    private TextField currentNumberText, prevOperationsText;
    private Button numZero, numOne, numTwo, numThree, numFour, numFive, numSix, numSeven, numEight, numNine;
    private Button point, resultButton, negate, add, subtract, multiply, divide, reverse, square, squareRoot, percent;
    private Button memoryClean, memoryRecall, memoryPlus, memoryMinus, memoryStore, memory;
    private Button cleanAll, cleanCurrent, leftErase;
    private Button mode, modeClose, infoButton, history;

    private VBox viewPanel;

    @BeforeClass
    public static void setUpInit() throws Exception {
        launch(CalcApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Stage is already launched
    }

    @Before
    public void initElements() {
        // text fields
        currentNumberText = findAndVerify("#currentNumberText", true, false);
        prevOperationsText = findAndVerify("#prevOperationsText", true, false);

        // buttons with numbers
        numZero = findAndVerify("0", true, false);
        numOne = findAndVerify("1", true, false);
        numTwo = findAndVerify("2", true, false);
        numThree = findAndVerify("3", true, false);
        numFour = findAndVerify("4", true, false);
        numFive = findAndVerify("5", true, false);
        numSix = findAndVerify("6", true, false);
        numSeven = findAndVerify("7", true, false);
        numEight = findAndVerify("8", true, false);
        numNine = findAndVerify("9", true, false);

        // buttons with math operations
        point = findAndVerify("#point", true, false);
        resultButton = findAndVerify(EQUAL.getCode(), true, false);
        negate = findAndVerify(NEGATE.getCode(), true, false);
        add = findAndVerify(ADD.getCode(), true, false);
        subtract = findAndVerify(SUBTRACT.getCode(), true, false);
        multiply = findAndVerify(MULTIPLY.getCode(), true, false);
        divide = findAndVerify(DIVIDE.getCode(), true, false);
        reverse = findAndVerify(REVERSE.getCode(), true, false);
        square = findAndVerify(SQUARE.getCode(), true, false);
        squareRoot = findAndVerify(SQUARE_ROOT.getCode(), true, false);
        percent = findAndVerify(PERCENT.getCode(), true, false);

        // buttons with memory operations
        memoryClean = findAndVerify(MEMORY_CLEAN.getCode(), true, true);
        memoryRecall = findAndVerify(MEMORY_RECALL.getCode(), true, true);
        memoryPlus = findAndVerify(MEMORY_ADD.getCode(), true, false);
        memoryMinus = findAndVerify(MEMORY_SUBTRACT.getCode(), true, false);
        memoryStore = findAndVerify(MEMORY_STORE.getCode(), true, false);
        memory = findAndVerify(MEMORY_SHOW.getCode(), true, true);

        // buttons with editing operations
        cleanAll = findAndVerify(CLEAN.getCode(), true, false);
        cleanCurrent = findAndVerify(CLEAN_CURRENT.getCode(), true, false);
        leftErase = findAndVerify(LEFT_ERASE.getCode(), true, false);

        // other buttons
        mode = findAndVerify("#mode", true, false);
        modeClose = findAndVerify("#modeClose", false, false);
        infoButton = findAndVerify("#infoButton", false, false);
        history = findAndVerify("#history", true, true);

        viewPanel = findAndVerify("#viewPanel", false, false);
    }

    private <T extends Node> T findAndVerify(final String query, boolean visible, boolean disable) {
        T node = find(query);
        testExistAndActive(node, visible, disable);
        return node;
    }

    private <T extends Node> T find(final String query) {
        return lookup(query).query();
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
        // continue typing
        testButtonClicked("1,234", numFour);
        testButtonClicked("12,345", numFive);
        testButtonClicked("123,456", numSix);
        testButtonClicked("1,234,567", numSeven);
        testButtonClicked("12,345,678", numEight);
        testButtonClicked("123,456,789", numNine);
        testButtonClicked("1,234,567,890", numZero);

        // reset
        testButtonClicked("0", cleanCurrent);
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

    @Test
    public void testFontResize() {
        double initialFontSize = currentNumberText.getFont().getSize();

        for (int i = 0; i < 15; i++) { // If text in field is too large font size must become smaller
            robot.push(KeyCode.DIGIT5);
        }
        assertNotEquals(initialFontSize, currentNumberText.getFont().getSize());
    }

    @Test
    public void testWindowResize() {
        Window windowBeforeResize = robot.targetWindow();

        // remember initial parameters
        double startX = windowBeforeResize.getX();
        double startY = windowBeforeResize.getY();
        double initWidth = windowBeforeResize.getWidth();
        double initHeight = windowBeforeResize.getHeight();

        robot.drag(startX, startY, MouseButton.PRIMARY);
        robot.moveBy(100, 100);
        robot.drop();

        Window windowAfterResize = robot.targetWindow();
        assertNotEquals(initWidth, windowAfterResize.getWidth());
        assertNotEquals(initHeight, windowAfterResize.getHeight());

        // resize one more time
        initWidth = windowAfterResize.getWidth();
        initHeight = windowAfterResize.getHeight();

        robot.drag(windowAfterResize.getX(), windowAfterResize.getY(), MouseButton.PRIMARY);
        robot.moveBy(-170, -100);
        robot.drop();

        assertNotEquals(initWidth, robot.targetWindow().getWidth());
        assertNotEquals(initHeight, robot.targetWindow().getHeight());
    }

    @Test
    public void testWindowMove() {
        Window windowBeforeMove = robot.targetWindow();
        // remember initial parameters
        double startX = windowBeforeMove.getX();
        double startY = windowBeforeMove.getY();
        double initWidth = windowBeforeMove.getWidth();
        double initHeight = windowBeforeMove.getHeight();

        robot.drag(startX + 70, startY + 10, MouseButton.PRIMARY);
        robot.moveBy(100, 100);
        robot.drop();

        Window windowAfterMove = robot.targetWindow();

        // Assert that window coordinates changed but window width and height still the same
        assertNotEquals(startX, windowAfterMove.getX());
        assertNotEquals(startY, windowAfterMove.getY());
        assertEquals(initWidth, windowAfterMove.getWidth(), 0.0001);
        assertEquals(initHeight, windowAfterMove.getHeight(), 0.0001);
    }
}
