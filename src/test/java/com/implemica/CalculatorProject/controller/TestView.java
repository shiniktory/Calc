package com.implemica.CalculatorProject.controller;

import com.implemica.CalculatorProject.model.calculation.*;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.implemica.CalculatorProject.model.calculation.MemoryOperation.*;
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

    private static final Map<String, Button> buttons = new LinkedHashMap<>();

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
            String operationCode = operation.name();
            addButton(operationCode, operationCode.toLowerCase());
        }
        addButton(".", "point");

        // init memory operations
        for (MemoryOperation operation : MemoryOperation.values()) {
            String operationCode = operation.name();
            addButton(operationCode, operationCode.toLowerCase());
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
        typesList = find("#viewTypesList");
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
        testIsPaneVisible(false, viewPanel);

        // show view panel
        clickButton("mode");
        WaitForAsyncUtils.waitForFxEvents();
        testIsPaneVisible(true, viewPanel);

        // Verify is selected item changed and panel is still visible
        Label firstSelected = (Label) typesList.getSelectionModel().getSelectedItem();
        robot.clickOn(typesList);
        testIsPaneVisible(true, viewPanel);
        Label secondSelected = (Label) typesList.getSelectionModel().getSelectedItem();
        assertNotEquals(firstSelected, secondSelected);
        testIsPaneVisible(true, viewPanel);

        // scroll and click button and verify that panel is still visible
        robot.scroll(DOWN);
        testIsPaneVisible(true, viewPanel);
        clickButton("infoButton");
        testIsPaneVisible(true, viewPanel);

        // hide calculator view panel
        clickButton("modeClose");
        WaitForAsyncUtils.waitForFxEvents();
        testIsPaneVisible(false, viewPanel);
    }

    @Test
    public void testMemoryPanel() {
        // by default memory panel must be invisible
        testIsPaneVisible(false, memoryStorage);

        // show memory panel
        clickButton(MEMORY_STORE.name());
        clickButton(MEMORY_SHOW.name());
        WaitForAsyncUtils.waitForFxEvents();
        testIsPaneVisible(true, memoryStorage);

        // try to press any digit or operation button
        for (int i = 0; i <= 9; i++) {
            String digit = String.valueOf(i);
            testIsButtonEnable(false, digit);

            KeyCode digitKey = getKeyCode(digit);
            pushKey(digitKey);
            testCurrentText("0");
        }
        testIsPaneVisible(true, memoryStorage);

        for (MathOperation operation : MathOperation.values()) {
            testIsButtonEnable(false, operation.name());
        }
        testIsPaneVisible(true, memoryStorage);

        pushKey(BACK_SPACE);
        testCurrentText("0");
        testIsPaneVisible(true, memoryStorage);
        WaitForAsyncUtils.waitForFxEvents();
        // hide panel
        clickButton(MEMORY_SHOW.name());
        WaitForAsyncUtils.waitForFxEvents();
        testIsPaneVisible(false, memoryStorage);
    }

    @Test
    public void testFontResize() {
        double initialFontSize = currentNumberText.getFont().getSize();

        for (int i = 0; i < 15; i++) { // If text in field is too large font size must become smaller
            pushKey(KeyCode.DIGIT5);
        }
        double currentFontSize = currentNumberText.getFont().getSize();

        // test font size become smaller
        assertTrue(initialFontSize > currentFontSize);
        assertTrue((initialFontSize - currentFontSize) > 5);
        assertEquals(32.0, currentFontSize, 2);
        assertTrue(currentFontSize > MIN_FONT_SIZE);
        assertTrue(currentFontSize < MAX_FONT_SIZE);

        // test text width and height are less than text field parameters
        Text currentText = new Text(currentNumberText.getText());
        currentText.setFont(currentNumberText.getFont());
        double textWidth = currentText.getBoundsInLocal().getWidth();
        double textHeight = currentText.getBoundsInLocal().getHeight();

        assertTrue(currentNumberText.getWidth() > textWidth);
        assertTrue(currentNumberText.getHeight() > textHeight);

        // test font size after reset. expected returned to initial size
        pushKey(ESCAPE);
        currentFontSize = currentNumberText.getFont().getSize();
        assertEquals(initialFontSize, currentFontSize, 0.0001);
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
        testWindowResize(-100, -100);
        testWindowResize(50, 60);
        testWindowResize(150, 260);
        testWindowResize(150, 260);
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
        double windowWidth = window.getWidth();
        double windowHeight = window.getHeight();
        Stage currentStage = FxToolkit.toolkitContext().getRegisteredStage();

        boolean doesParametersChanged = startWidth != windowWidth || startHeight != windowHeight;
        boolean isMinBoundParameter = windowWidth == currentStage.getMinWidth() || windowHeight == currentStage.getMinHeight();
        boolean isMaxBoundParameter = windowWidth == currentStage.getMaxWidth() || windowHeight == currentStage.getMaxHeight();

        assertTrue(doesParametersChanged || isMinBoundParameter || isMaxBoundParameter);
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

    private void testIsPaneVisible(boolean expected, Pane pane) {
        assertEquals(expected, pane.isVisible());
    }

    private void testIsButtonEnable(boolean expected, String buttonId) {
        Button button = buttons.get(buttonId);
        assertEquals(expected, !button.isDisabled());
    }
}
