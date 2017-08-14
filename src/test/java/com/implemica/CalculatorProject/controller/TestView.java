package com.implemica.CalculatorProject.controller;

import com.implemica.CalculatorProject.calculation.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.implemica.CalculatorProject.calculation.MemoryOperation.*;
import static javafx.geometry.VerticalDirection.DOWN;
import static javafx.scene.input.KeyCode.*;
import static org.junit.Assert.*;

public class TestView {

    private static final FxRobot robot = new FxRobot();

    private static final double MIN_FONT_SIZE = 10.0;
    private static final double MAX_FONT_SIZE = 66.0;

    private static TextField currentNumberText;

    private static ListView typesList;
    private static VBox viewPanel;
    private static BorderPane memoryStorage;

    private static Map<String, Button> buttons = new LinkedHashMap<>();

    @BeforeClass
    public static void setUpInit() {
        WaitForAsyncUtils.waitForFxEvents();

        // init numbers
        for (int i = 0; i <= 9; i++) {
            String number = String.valueOf(i);
            addButton(number, number);
        }

        // init math operations
        for (MathOperation operation : MathOperation.values()) {
            String operationCode = operation.id();
            addButton(operationCode, operationCode);
        }
        addButton(".", "point");

        // init memory operations
        for (MemoryOperation operation : MemoryOperation.values()) {
            String operationCode = operation.id();
            addButton(operationCode, operationCode);
        }

        // other buttons
        addButton("mode", "mode");
        addButton("modeClose", "modeClose");
        addButton("infoButton", "infoButton");
        addButton("history", "history");

        // text fields with current number and history
        currentNumberText = find("#currentNumberText");

        // panels with calculator's view types and memorized values
        viewPanel = find("#viewTypesPanel");
        typesList = find("#viewTypes");
        memoryStorage = find("#memoryStorage");
    }

    private static void addButton(String buttonId, String query) {
        buttons.put(buttonId, find("#" + query));
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

    @Test
    public void testViewPanel() {
        testPaneVisible(false, viewPanel);

        // show view panel
        clickButton("mode");
        WaitForAsyncUtils.waitForFxEvents();
        testPaneVisible(true, viewPanel);

        // Verify is selected item changed and panel is still visible
        Label firstSelected = (Label) typesList.getSelectionModel().getSelectedItem();
        robot.clickOn(typesList);
        testPaneVisible(true, viewPanel);
        Label secondSelected = (Label) typesList.getSelectionModel().getSelectedItem();
        assertNotEquals(firstSelected, secondSelected);
        testPaneVisible(true, viewPanel);

        // scroll and click button and verify that panel is still visible
        robot.scroll(DOWN);
        testPaneVisible(true, viewPanel);
        clickButton("infoButton");
        testPaneVisible(true, viewPanel);

        // hide calculator view panel
        clickButton("modeClose");
        WaitForAsyncUtils.waitForFxEvents();
        testPaneVisible(false, viewPanel);
    }

    @Test
    public void testMemoryPanel() {
        // by default memory panel must be invisible
        testPaneVisible(false, memoryStorage);

        // show memory panel
        clickButton(MEMORY_STORE.id());
        clickButton(MEMORY_SHOW.id());
        WaitForAsyncUtils.waitForFxEvents();
        testPaneVisible(true, memoryStorage);

        // try to press any digit or operation button
        for (int i = 0; i <= 9; i++) {
            String digit = String.valueOf(i);
            testButtonDisable(true, digit);

            KeyCode digitKey = getKeyCode(digit);
            pushKey(digitKey);
            testCurrentText("0");
        }
        testPaneVisible(true, memoryStorage);

        for (MathOperation operation : MathOperation.values()) {
            testButtonDisable(true, operation.id());
        }
        testPaneVisible(true, memoryStorage);

        pushKey(BACK_SPACE);
        testCurrentText("0");
        testPaneVisible(true, memoryStorage);
        WaitForAsyncUtils.waitForFxEvents();
        // hide panel
        clickButton(MEMORY_SHOW.id());
        WaitForAsyncUtils.waitForFxEvents();
        testPaneVisible(false, memoryStorage);
    }

    @Test
    public void testFontResize() {
        double initialFontSize = currentNumberText.getFont().getSize();

        for (int i = 0; i < 15; i++) { // If text in field is too large font size must become smaller
            pushKey(KeyCode.DIGIT5);
        }
        double currentFontSize = currentNumberText.getFont().getSize();

        assertTrue(initialFontSize > currentFontSize);
        assertTrue(currentFontSize > MIN_FONT_SIZE);
        assertTrue(currentFontSize < MAX_FONT_SIZE);

        // test font size after reset. expected returned to initial size
        pushKey(ESCAPE);
        currentFontSize = currentNumberText.getFont().getSize();
        assertEquals(initialFontSize, currentFontSize, 1);
    }

    @Test
    public void testWindowResize() {
        testWindowResize(100, 100);
        testWindowResize(-170, -100);
        testWindowResize(-10, -10);
        testWindowResize(170, 100);
        testWindowResize(70, 50);
        testWindowResize(-110, -190);
        testWindowResize(40, 30);
        testWindowResize(-40, -30);
        testWindowResize(-50, -60);
        testWindowResize(50, 60);
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
        assertEquals(initWidth, windowAfterMove.getWidth(), 1);
        assertEquals(initHeight, windowAfterMove.getHeight(), 1);
    }

    private void clickButton(String buttonId) {
        Button button = buttons.get(buttonId);
        robot.clickOn(button);
    }

    private void pushKey(KeyCode keyCode) {
        robot.push(keyCode);
    }


    private void testCurrentText(String expected) {
        assertEquals(expected, currentNumberText.getText());
    }

    private void testPaneVisible(boolean expected, Pane pane) {
        assertEquals(expected, pane.isVisible());
    }

    private void testButtonDisable(boolean expected, String buttonId) {
        Button button = buttons.get(buttonId);
        assertEquals(expected, button.isDisabled());
    }
}
