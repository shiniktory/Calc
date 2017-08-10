package com.implemica.CalculatorProject;

import com.implemica.CalculatorProject.util.OutputFormatter;
import com.implemica.CalculatorProject.validation.DataValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.implemica.CalculatorProject.calculation.EditOperation.CLEAN;
import static com.implemica.CalculatorProject.calculation.EditOperation.CLEAN_CURRENT;
import static com.implemica.CalculatorProject.calculation.EditOperation.LEFT_ERASE;
import static com.implemica.CalculatorProject.calculation.MathOperation.*;

/**
 * The class is an entry point to the application.
 *
 * @author V. Kozina-Kravchenko
 */
public class CalcApplication extends Application {

    /**
     * A path to the view file.
     */
    private static final String CALCULATOR_VIEW_FILE = "/calc.fxml";

    private static final String CSS_FILE = "/winCalc.css";

    private static final String ICON_FILE = "/icon3.png";

    /**
     * The name of the application.
     */
    private static final String APPLICATION_NAME = "Calculator";

    private static final double MIN_HEIGHT = 385.0;
    private static final double MIN_WIDTH = 220.0;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = loadParent();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(CSS_FILE);
            primaryStage.setScene(scene);
            primaryStage.setTitle(APPLICATION_NAME);
            primaryStage.getIcons().add(new Image(ICON_FILE));
            primaryStage.setMinHeight(MIN_HEIGHT);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.initStyle(StageStyle.DECORATED);
            primaryStage.setResizable(true);

            primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
                scaleFontSize(root, primaryStage);
            });

            primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
                scaleFontSize(root, primaryStage);
            });

            primaryStage.fullScreenProperty().addListener((observable, oldValue, newValue) ->
                    scaleFontSize(root, primaryStage));

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and returns the root {@link Parent} for a view.
     *
     * @return the root {@link Parent} for a view
     * @throws IOException if something wrong with view file
     */
    private Parent loadParent() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        return loader.load(getClass().getResourceAsStream(CALCULATOR_VIEW_FILE));
    }

    private static final Map<String, Double[]> defaultFontSizes = new LinkedHashMap<>();

    static {
        defaultFontSizes.put(PERCENT.getCode(), new Double[]{16.0, 19.0, 25.0});  // 16-25
        defaultFontSizes.put(SQUARE_ROOT.getCode(), new Double[]{16.0, 17.0, 25.0}); // 16-25
        defaultFontSizes.put(SQUARE.getCode(), new Double[]{16.0,18.0,25.0}); // 16-25
        defaultFontSizes.put(REVERSE.getCode(), new Double[]{16.0,18.0, 25.0}); // 16-25
        defaultFontSizes.put(NEGATE.getCode(), new Double[]{24.0,29.0, 42.0});// 24-42
        defaultFontSizes.put(DIVIDE.getCode(), new Double[]{24.0,32.0, 42.0}); // 24-42
        defaultFontSizes.put(MULTIPLY.getCode(), new Double[]{15.0,19.0, 25.0}); //15-25
        defaultFontSizes.put(ADD.getCode(), new Double[]{24.0,33.0, 42.0});// 24-42
        defaultFontSizes.put(SUBTRACT.getCode(), new Double[]{24.0,35.0, 42.0});// 24-42
        defaultFontSizes.put(RESULT.getCode(), new Double[]{24.0,34.0, 42.0});// 24-42

        defaultFontSizes.put("numbers", new Double[]{15.0,20.0, 25.0}); // 15-25
        defaultFontSizes.put("point", new Double[]{15.0,23.0, 25.0}); // 15-25

        defaultFontSizes.put(LEFT_ERASE.getCode(), new Double[]{15.0,20.0, 25.0}); // 15-25
        defaultFontSizes.put(CLEAN_CURRENT.getCode(), new Double[]{14.0,15.0, 23.0}); // 14-23
        defaultFontSizes.put(CLEAN.getCode(), new Double[]{14.0,15.0, 23.0}); //14-23

        defaultFontSizes.put("current number tf", new Double[]{16.0,42.0, 66.0});
    }

    private void scaleFontSize(Parent root, Stage primaryStage) {
        GridPane pane = (GridPane) root.lookup("#numbers_operations");

        for (Node node : pane.getChildren()) {
            Button button = (Button) node;
            String buttonText = button.getText();
//
//            double defaultFontSize = getDefaultFontSize(buttonText);
//            setFontSize(button, defaultFontSize);

            double newFontSize = calculateNewFontSize(primaryStage, button);
            setFontSize(button, newFontSize);
        }
    }

    private double getDefaultFontSize(String buttonText, int boundIndex) {
        if (DataValidator.isDigit(buttonText) || OutputFormatter.POINT.equals(buttonText)) {
            return defaultFontSizes.get("numbers")[boundIndex];
        } else {
            return defaultFontSizes.get(buttonText)[boundIndex];
        }
    }

    private double calculateNewFontSize(Stage primaryStage, Button button) {
        /*
            if width and height in appropriate scopes -> get for this scope
            if in different scopes -> get for min parameter

                        min         middle      max
            width       200         280-        440+
            height      370         480-        640+
         */

        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();
        double sum = currentHeight + currentWidth; // TODO change not for sum. Do as in comment under
        int index = 1;
        if (sum < 760.0) {
            index = 0;
        } else if (sum > 1080.0) {
            index = 2;
        }

        return getDefaultFontSize(button.getText(), index);
//        String buttonText = button.getText();
//
//        Text text = new Text(buttonText);
//        text.setFont(button.getFont());
//        Bounds textBounds = text.getLayoutBounds();
//        Bounds buttonBounds = button.getBoundsInLocal();
//
//        double newFontSize = button.getFont().getSize();
//        double scale = (buttonBounds.getHeight() * 0.45) / textBounds.getHeight();
//        if (scale < 1.5) {
//            newFontSize *= scale;
//        }
//        text.setFont(new Font(text.getFont().getFamily(), newFontSize));
//        textBounds = text.getBoundsInLocal();
//
//        if (textBounds.getWidth() > (buttonBounds.getWidth() * 0.5)) {
//            scale = (buttonBounds.getWidth() * 0.5) / textBounds.getWidth();
//            newFontSize *= scale;
//        }

//        return newFontSize;
    }

    private List<String> labeledButtons = new ArrayList<>();

    {
        labeledButtons.add(NEGATE.getCode());
        labeledButtons.add(DIVIDE.getCode());
        labeledButtons.add(SUBTRACT.getCode());
        labeledButtons.add(ADD.getCode());
        labeledButtons.add(RESULT.getCode());
        labeledButtons.add(LEFT_ERASE.getCode());
    }

    private void setFontSize(Button button, double newFontSize) {
        String buttonText = button.getText();

        if (labeledButtons.contains(buttonText)) {
            Label buttonLabel = (Label) button.getChildrenUnmodifiable().get(0);
            buttonLabel.setFont(new Font(buttonLabel.getFont().getFamily(), newFontSize));
        } else {
            button.setFont(new Font(button.getFont().getFamily(), newFontSize));
        }
    }
}
