package com.implemica.CalculatorProject.controller;

import com.implemica.CalculatorProject.CalcApplication;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.junit.*;
import org.loadui.testfx.utils.FXTestUtils;
import org.testfx.api.FxRobot;
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
import static org.testfx.framework.junit.ApplicationTest.launch;

public class ControllerAndViewTest {

    private static final String RESULT_IS_UNDEFINED_MESSAGE = "Result is undefined";

    private FxRobot robot = new FxRobot();

    private TextField currentNumberText, prevOperationsText;
    private Button numZero, numOne, numTwo, numThree, numFour, numFive, numSix, numSeven, numEight, numNine;
    private Button point, resultButton, negate, add, subtract, multiply, divide, reverse, square, squareRoot, percent;
    private Button memoryClean, memoryRecall, memoryPlus, memoryMinus, memoryStore, memory;
    private Button cleanAll, cleanCurrent, leftErase;
    private Button mode, modeClose, infoButton, history;
    private ListView typesList;
    private VBox viewPanel;
    private BorderPane memoryStorage;

    @BeforeClass
    public static void setUpInit() throws Exception {
        launch(CalcApplication.class);
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
        resultButton = findAndVerify(RESULT.getCode(), true, false);
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

        viewPanel = findAndVerify("#viewTypesPanel", false, false);
        typesList = findAndVerify("#viewTypes", false, false);
        memoryStorage = findAndVerify("#memoryStorage", false, false);
    }

    private <T extends Node> T findAndVerify(final String query, boolean visible, boolean disable) {
        T node = robot.lookup(query).query();
        testExistAndActive(node, visible, disable);
        return node;
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

        // Verify is selected item changed
        Label firstSelected = (Label) typesList.getSelectionModel().getSelectedItem();
        robot.clickOn(typesList);
        Label secondSelected = (Label) typesList.getSelectionModel().getSelectedItem();
        assertNotEquals(firstSelected, secondSelected);

        robot.scroll(VerticalDirection.DOWN);
        robot.clickOn(infoButton);

        // hide calculator view panel
        robot.clickOn(modeClose);
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(viewPanel.isVisible());
    }

    @Test
    public void testViewMemoryPanel() {
        assertFalse(memoryStorage.isVisible());
        robot.clickOn(cleanAll);
        robot.clickOn(memoryStore);

        // show memory panel
        robot.clickOn(memory);
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(memoryStorage.isVisible());

        // Test key press on buttons hidden by memory storage panel. Expected no changes - default zero
        testKeyPressed("0", KeyCode.DIGIT1, false);
        testKeyPressed("0", KeyCode.DIGIT2, false);
        testKeyPressed("0", KeyCode.DIGIT3, false);
        testKeyPressed("0", KeyCode.DIGIT4, false);
        testKeyPressed("0", KeyCode.DIGIT5, false);
        testKeyPressed("0", KeyCode.DIGIT6, false);
        testKeyPressed("0", KeyCode.DIGIT7, false);
        testKeyPressed("0", KeyCode.DIGIT8, false);
        testKeyPressed("0", KeyCode.DIGIT9, false);
        testKeyPressed("0", KeyCode.DIGIT0, false);
        testKeyPressed("0", KeyCode.ADD, false);
        testKeyPressed("0", KeyCode.SUBTRACT, false);
        testKeyPressed("0", KeyCode.MULTIPLY, false);
        testKeyPressed("0", KeyCode.DIVIDE, false);
        testKeyPressed("0", KeyCode.BACK_SPACE, false);

        // hide panel
        robot.clickOn(memory);
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
        testButtonClicked("9", numNine);
        testButtonClicked("-4", resultButton);

        // test multiply operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", multiply);
        testButtonClicked("9", numNine);
        testButtonClicked("45", resultButton);

        // test divide operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", divide);
        testButtonClicked("9", numNine);
        testButtonClicked("0.5555555555555556", resultButton);

        // test percentage
        testButtonClicked("5", numFive);
        testButtonClicked("5", add);
        testButtonClicked("9", numNine);
        testButtonClicked("0.45", percent);
        testButtonClicked("5.45", resultButton);

        // test square root operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", add);
        testButtonClicked("9", numNine);
        testButtonClicked("3", squareRoot);
        testButtonClicked("8", resultButton);

        // test square operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", add);
        testButtonClicked("9", numNine);
        testButtonClicked("81", square);
        testButtonClicked("86", resultButton);

        // test reverse operation
        testButtonClicked("5", numFive);
        testButtonClicked("5", add);
        testButtonClicked("9", numNine);
        testButtonClicked("0.1111111111111111", reverse);
        testButtonClicked("5.111111111111111", resultButton);

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
        testKeyPressed("0", KeyCode.ESCAPE, false);
        testKeyPressed("1", KeyCode.DIGIT1, false);
        testKeyPressed("12", KeyCode.DIGIT2, false);
        testKeyPressed("123", KeyCode.DIGIT3, false);
        testKeyPressed("1,234", KeyCode.DIGIT4, false);
        testKeyPressed("12,345", KeyCode.DIGIT5, false);
        testKeyPressed("123,456", KeyCode.DIGIT6, false);
        testKeyPressed("1,234,567", KeyCode.DIGIT7, false);
        testKeyPressed("12,345,678", KeyCode.DIGIT8, false);
        testKeyPressed("123,456,789", KeyCode.DIGIT9, false);
        testKeyPressed("1,234,567,890", KeyCode.DIGIT0, false);

        // press an operation
        testKeyPressed("1,234,567,890", KeyCode.EQUALS, true);

        // enter new number
        testKeyPressed("9", KeyCode.DIGIT9, false);
        testKeyPressed("98", KeyCode.DIGIT8, false);
        testKeyPressed("987", KeyCode.DIGIT7, false);
        testKeyPressed("9,876", KeyCode.DIGIT6, false);
        testKeyPressed("98,765", KeyCode.DIGIT5, false);
        testKeyPressed("987,654", KeyCode.DIGIT4, false);
        testKeyPressed("9,876,543", KeyCode.DIGIT3, false);
        testKeyPressed("98,765,432", KeyCode.DIGIT2, false);
        testKeyPressed("987,654,321", KeyCode.DIGIT1, false);
        testKeyPressed("987,654,321.", KeyCode.PERIOD, false);
        testKeyPressed("987,654,321.0", KeyCode.DIGIT0, false);
        testKeyPressed("987,654,321.09", KeyCode.DIGIT9, false);

        // calculate result few times
        testKeyPressed("2,222,222,211.09", KeyCode.ENTER, false);
        testKeyPressed("3,209,876,532.18", KeyCode.ENTER, false);
        testKeyPressed("4,197,530,853.27", KeyCode.ENTER, false);

        // test subtract operation
        testKeyPressed("5", KeyCode.DIGIT5, false);
        testKeyPressed("5", KeyCode.MINUS, false);
        testKeyPressed("9", KeyCode.DIGIT9, false);
        testKeyPressed("-4", KeyCode.ENTER, false);
        testKeyPressed("0", KeyCode.BACK_SPACE, false);

        // test multiply operation
        testKeyPressed("5", KeyCode.DIGIT5, false);
        testKeyPressed("5", KeyCode.MULTIPLY, false);
        testKeyPressed("9", KeyCode.DIGIT9, false);
        testKeyPressed("45", KeyCode.ENTER, false);
        testKeyPressed("4", KeyCode.BACK_SPACE, false);

        // test divide operation
        testKeyPressed("5", KeyCode.DIGIT5, false);
        testKeyPressed("5", KeyCode.DIVIDE, false);
        testKeyPressed("9", KeyCode.DIGIT9, false);
        testKeyPressed("0.5555555555555556", KeyCode.ENTER, false);

        // test percentage
        testKeyPressed("5", KeyCode.DIGIT5, false);
        testKeyPressed("5", KeyCode.ADD, false);
        testKeyPressed("9", KeyCode.DIGIT9, false);
        testKeyPressed("0.45", KeyCode.DIGIT5, true);
        testKeyPressed("5.45", KeyCode.ENTER, false);

        // test square root operation
        testKeyPressed("5", KeyCode.NUMPAD5, false);
        testKeyPressed("5", KeyCode.ADD, false);
        testKeyPressed("9", KeyCode.DIGIT9, false);
        testKeyPressed("3", KeyCode.DIGIT2, true);
        testKeyPressed("8", KeyCode.ENTER, false);

        testKeyPressed("0", KeyCode.BACK_SPACE, false);
    }

    private void testKeyPressed(String expectedText, KeyCode keyCode, boolean isShiftDown) {

        KeyCodeCombination combination;
        if (isShiftDown) {
        combination = new KeyCodeCombination(keyCode, KeyCombination.SHIFT_DOWN);
        } else {
            combination = new KeyCodeCombination(keyCode);
        }
        robot.push(combination);
        assertEquals(expectedText, currentNumberText.getText());
    }

    @Test
    public void testPressDisabledButtons() {
        // cause an exception by dividing zero by zero to disable buttons with operations
        testKeyPressed("0", KeyCode.ESCAPE, false);
        testKeyPressed("0", KeyCode.DIGIT0, false);
        testKeyPressed("0", KeyCode.DIVIDE, false);
        testKeyPressed("0", KeyCode.DIGIT0, false);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.EQUALS, false);

        // try to press disabled operations from keyboard. Expected no changes in text field
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.EQUALS, true);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.MINUS, false);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIVIDE, false);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIGIT8, true);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIGIT2, true);
        testKeyPressed(RESULT_IS_UNDEFINED_MESSAGE, KeyCode.DIGIT5, true);

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
        testKeyPressed("0.5", KeyCode.DIGIT5, false);
        testKeyPressed("0.7071067811865475", KeyCode.DIGIT2, true);
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
