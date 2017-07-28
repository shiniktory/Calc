package com.implemica.CalculatorProject.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.implemica.CalculatorProject.calculation.MathOperation.ADD;
import static com.implemica.CalculatorProject.calculation.MathOperation.MULTIPLY;
import static com.implemica.CalculatorProject.util.OutputFormatter.EMPTY_VALUE;
import static com.implemica.CalculatorProject.util.OutputFormatter.POINT;
import static org.junit.Assert.assertEquals;


public class CalculatorControllerTest extends Application { // TODO use TestFX

    private FXMLLoader loader = new FXMLLoader();
    private Parent root = loader.load(getClass().getResourceAsStream("/calc.fxml"));
    private CalculatorController controller = loader.getController();

    public CalculatorControllerTest() throws IOException {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(root));
    }

    @BeforeClass
    public static void initApplication() {

        Thread applicationThread = new Thread(() -> Application.launch(CalculatorControllerTest.class));
        applicationThread.setDaemon(true);
        applicationThread.start();
    }

    @Test
    public void testEnterFromKeyboard() {
        // Enter the first number from keyboard
        testEnterFromKeyboard("1", KeyCode.DIGIT1.getName());
        testEnterFromKeyboard("12", KeyCode.DIGIT2.getName());
        testEnterFromKeyboard("123", KeyCode.DIGIT3.getName());
        testEnterFromKeyboard("1,234", KeyCode.DIGIT4.getName());
        testEnterFromKeyboard("12,345", KeyCode.DIGIT5.getName());
        testEnterFromKeyboard("123,456", KeyCode.DIGIT6.getName());
        testEnterFromKeyboard("1,234,567", KeyCode.DIGIT7.getName());
        testEnterFromKeyboard("12,345,678", KeyCode.DIGIT8.getName());
        testEnterFromKeyboard("123,456,789", KeyCode.DIGIT9.getName());
        testEnterFromKeyboard("1,234,567,890", KeyCode.DIGIT0.getName());
        testEnterFromKeyboard("1,234,567,890.", POINT);
        testEnterFromKeyboard("1,234,567,890.0", KeyCode.DIGIT0.getName());
        testEnterFromKeyboard("1,234,567,890.01", KeyCode.DIGIT1.getName());
        testEnterFromKeyboard("1,234,567,890.012", KeyCode.DIGIT2.getName());
        testEnterFromKeyboard("1,234,567,890.0123", KeyCode.DIGIT3.getName());
        testEnterFromKeyboard("1,234,567,890.01234", KeyCode.DIGIT4.getName());
        testEnterFromKeyboard("1,234,567,890.012345", KeyCode.DIGIT5.getName());
        testEnterFromKeyboard("1,234,567,890.012345", KeyCode.DIGIT6.getName());

        // Press an operation and check an expression
        testEnterFromKeyboard("1,234,567,890.012345", ADD.getCode());
        testExpression("1234567890.012345 " + ADD.getCode());

        // Enter the second number
        testEnterFromKeyboard("5", KeyCode.DIGIT5.getName());
        testEnterFromKeyboard("50", KeyCode.DIGIT0.getName());
        testEnterFromKeyboard("500", KeyCode.DIGIT0.getName());
        testEnterFromKeyboard("5,000", KeyCode.DIGIT0.getName());

        // Press another operation, check expression and current number text field
        testEnterFromKeyboard("1,234,572,890.012345", MULTIPLY.getCode());
        testExpression("1234567890.012345 " + ADD.getCode() + " 5000 " + MULTIPLY.getCode());

        // Enter the third number
        testEnterFromKeyboard("9", KeyCode.DIGIT9.getName());
        testEnterFromKeyboard("90", KeyCode.DIGIT0.getName());
        testEnterFromKeyboard("900",KeyCode.DIGIT0.getName());
        testEnterFromKeyboard("9,000", KeyCode.DIGIT0.getName());
        testEnterFromKeyboard("90,000", KeyCode.DIGIT0.getName());

        // press enter and check text fields
        testEnterFromKeyboard("111,111,560,101,111.1", "=");
        testExpression(EMPTY_VALUE);
    }

    private void testEnterFromKeyboard(String expectedText, String buttonCode) {
        controller.searchAndFireButton(buttonCode);
        assertEquals(expectedText, controller.getCurrentNumberText().getText());
    }

    private void testExpression(String expectedExpression) {
        assertEquals(expectedExpression.trim(), controller.getPrevOperationsText().getText());
    }
}
