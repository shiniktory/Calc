package com.implemica.CalculatorProject.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;


import static com.implemica.CalculatorProject.calculation.EditOperation.CLEAN;
import static com.implemica.CalculatorProject.calculation.EditOperation.CLEAN_EVERYTHING;
import static com.implemica.CalculatorProject.calculation.EditOperation.LEFT_ERASE;
import static com.implemica.CalculatorProject.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.calculation.MemoryOperation.*;
import static javafx.scene.input.KeyCombination.ModifierValue.DOWN;
import static javafx.scene.input.KeyCombination.ModifierValue.UP;
import static org.junit.Assert.*;

public class CalculatorControllerTest extends ApplicationTest {

    private static final String CALCULATOR_VIEW_FILE = "/calc.fxml";

    private static final String CSS_FILE = "/winCalc.css";

    private static final String ICON_FILE = "/icon3.png";

    private static final String APPLICATION_NAME = "Calculator";

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
        stage.show();
        stage.toFront();
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
        resultButton = find(DIVIDE.getCode());
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
        cleanAll = find(CLEAN_EVERYTHING.getCode());
        cleanCurrent = find(CLEAN.getCode());
        leftErase = find(LEFT_ERASE.getCode());

        // other buttons
        mode = find("#mode");
        modeClose = find("#modeClose");
        infoButton = find("#infoButton");
        history = find("#history");

        viewPanel = find("#viewPanel");
    }

    private  <T extends Node> T find(final String query) {
        return lookup(query).query();
    }

    @Test
    public void testElementsExist() {

        // text fields
        testExistAndVisible(currentNumberText, true, false);
        testExistAndVisible(prevOperationsText, true, false);

        // buttons with numbers
        testExistAndVisible(numZero, true, false);
        testExistAndVisible(numOne, true, false);
        testExistAndVisible(numTwo, true, false);
        testExistAndVisible(numThree, true, false);
        testExistAndVisible(numFour, true, false);
        testExistAndVisible(numFive, true, false);
        testExistAndVisible(numSix, true, false);
        testExistAndVisible(numSeven, true, false);
        testExistAndVisible(numEight, true, false);
        testExistAndVisible(numNine, true, false);

        // buttons with math operations
        testExistAndVisible(negate, true, false);
        testExistAndVisible(point, true, false);
        testExistAndVisible(resultButton, true, false);
        testExistAndVisible(add, true, false);
        testExistAndVisible(subtract, true, false);
        testExistAndVisible(multiply, true, false);
        testExistAndVisible(divide, true, false);
        testExistAndVisible(reverse, true, false);
        testExistAndVisible(square, true, false);
        testExistAndVisible(squareRoot, true, false);
        testExistAndVisible(percent, true, false);

        // buttons with editing operations
        testExistAndVisible(cleanAll, true, false);
        testExistAndVisible(cleanCurrent, true, false);
        testExistAndVisible(leftErase, true, false);

        // buttons with memory operations
        testExistAndVisible(memoryClean, true, true);
        testExistAndVisible(memoryRecall, true, true);
        testExistAndVisible(memoryPlus, true, false);
        testExistAndVisible(memoryMinus, true, false);
        testExistAndVisible(memoryStore, true, false);
        testExistAndVisible(memory, true, true);

        // other buttons
        testExistAndVisible(mode, true, false);
//        testExistAndVisible(modeClose, false, false); // TODO figure out with visibility. Must me visible = false
//        testExistAndVisible(infoButton, false, false);
        testExistAndVisible(history, true, true);


        testExistAndVisible(viewPanel, false, false);
    }

    private void testExistAndVisible(Node element, boolean expectedVisible, boolean expectedDisable) {
        assertNotNull(element);
        if (expectedVisible) {
            assertTrue(element.isVisible());
        } else {
            assertFalse(element.isVisible());
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
        FxRobot robot = new FxRobot();
        robot.clickOn(mode);
        assertTrue(viewPanel.isVisible());
        robot.clickOn(modeClose);
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
    }

    private void testButtonClicked(String expectedText, Node button) {
        robot.clickOn(button);
        assertEquals(expectedText, currentNumberText.getText());
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
    }

    private void testKeyPressed(String expectedText, KeyCodeCombination combination) {
        robot.push(combination);
        assertEquals(expectedText, currentNumberText.getText());
    }

    private static final String RESULT_IS_UNDEFINED_MESSAGE = "Result is undefined";

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
    public void testWindowMove() {
        double startX = robot.targetWindow().getX();
        double startY = robot.targetWindow().getY();

        robot.moveBy(50, 50);

        assertNotEquals(startX, robot.targetWindow().getX());
        assertNotEquals(startY, robot.targetWindow().getY());
    }

}
