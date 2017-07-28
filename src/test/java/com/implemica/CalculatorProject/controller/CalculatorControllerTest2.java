package com.implemica.CalculatorProject.controller;

import com.implemica.CalculatorProject.CalcApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class CalculatorControllerTest2 extends GuiTest{


    @Test
    public void testController() throws Throwable {
        click("#result_button");

    }

    @Override
    protected Parent getRootNode() {
        return null;
    }

    @Override
    public void setupStage() throws Throwable {
        new Thread(() -> Application.launch(CalcApplication.class))
                .start();
    }
}
