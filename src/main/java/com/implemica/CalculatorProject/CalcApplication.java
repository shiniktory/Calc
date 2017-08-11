package com.implemica.CalculatorProject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
import static com.implemica.CalculatorProject.util.OutputFormatter.POINT;
import static com.implemica.CalculatorProject.validation.DataValidator.isDigit;

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

    private static final String ICON_FILE = "/icon.png";

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
                scaleButtonFontSize(root, primaryStage);
            });

            primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
                scaleButtonFontSize(root, primaryStage);
            });

            currentNumberTextField = (TextField) root.lookup("#currentNumberText");

            currentNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                scaleTextFieldFont(primaryStage, currentNumberTextField.getBoundsInLocal().getWidth());
            });

            currentNumberTextField.widthProperty().addListener((observable, oldValue, newValue) -> {
                scaleTextFieldFont(primaryStage, newValue.doubleValue());
            });


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

    private static final Map<String, Double[]> fontSizes = new LinkedHashMap<>();

    private List<String> labeledButtons = new ArrayList<>();

    {
        fontSizes.put(PERCENT.id(), new Double[]{16.0, 19.0, 22.0});
        fontSizes.put(SQUARE_ROOT.id(), new Double[]{16.0, 17.0, 22.0});
        fontSizes.put(SQUARE.id(), new Double[]{16.0, 18.0, 22.0});
        fontSizes.put(REVERSE.id(), new Double[]{16.0, 18.0, 22.0});
        fontSizes.put(NEGATE.id(), new Double[]{25.0, 29.0, 36.0});
        fontSizes.put(DIVIDE.id(), new Double[]{24.0, 32.0, 42.0});
        fontSizes.put(MULTIPLY.id(), new Double[]{15.0, 19.0, 25.0});
        fontSizes.put(ADD.id(), new Double[]{24.0, 33.0, 42.0});
        fontSizes.put(SUBTRACT.id(), new Double[]{24.0, 35.0, 42.0});
        fontSizes.put(RESULT.id(), new Double[]{24.0, 34.0, 42.0});

        fontSizes.put("numbers", new Double[]{15.0, 20.0, 25.0});
        fontSizes.put("point", new Double[]{15.0, 23.0, 25.0});

        fontSizes.put(LEFT_ERASE.id(), new Double[]{15.0, 20.0, 25.0});
        fontSizes.put(CLEAN_CURRENT.id(), new Double[]{14.0, 15.0, 23.0});
        fontSizes.put(CLEAN.id(), new Double[]{14.0, 15.0, 23.0});

        fontSizes.put("currentNumberTF", new Double[]{23.0, 42.0, 66.0});

        labeledButtons.add(NEGATE.id());
        labeledButtons.add(DIVIDE.id());
        labeledButtons.add(MULTIPLY.id());
        labeledButtons.add(SUBTRACT.id());
        labeledButtons.add(ADD.id());
        labeledButtons.add(RESULT.id());
        labeledButtons.add(LEFT_ERASE.id());
    }

    private void scaleButtonFontSize(Parent root, Stage primaryStage) {
        GridPane pane = (GridPane) root.lookup("#numbers_operations");

        for (Node node : pane.getChildren()) {
            Button button = (Button) node;
            String buttonId = button.getId();

            double newFontSize = getFontSize(buttonId, getFontBoundIndex(primaryStage));
            setButtonFontSize(button, newFontSize);
        }
    }

    private void scaleTextFieldFont(Stage primaryStage, double currentWidth) {
        double defaultFontSize = fontSizes.get("currentNumberTF")[getFontBoundIndex(primaryStage)];
        currentNumberTextField.setFont(
                Font.font("Segoe UI Semibold", FontWeight.BOLD, defaultFontSize));

        Text text = new Text(currentNumberTextField.getText());
        text.setFont(currentNumberTextField.getFont());

        double textWidth = text.getLayoutBounds().getWidth();
        double scale = currentWidth / textWidth - 0.1;

        if (scale < 1.0) {
            double newFontSize = currentNumberTextField.getFont().getSize() * scale;
            currentNumberTextField.setFont(new Font(currentNumberTextField.getFont().getFamily(),
                    newFontSize));
        }
        currentNumberTextField.end();
    }

    private TextField currentNumberTextField;

    private double getFontSize(String buttonId, int boundIndex) {
        if (isDigit(buttonId) || POINT.equals(buttonId)) {
            return fontSizes.get("numbers")[boundIndex];
        } else {
            return fontSizes.get(buttonId)[boundIndex];
        }
    }

    private int getFontBoundIndex(Stage primaryStage) {
        /*
            if width and height in appropriate scopes -> get for this scope
            if in different scopes -> get for min parameter

                        min         middle      max
            width       200         280+        550+
            height      370         480+        640+
         */

        double currentWidth = primaryStage.getWidth();
        double currentHeight = primaryStage.getHeight();

        if (currentWidth < 280 || currentHeight < 480) {
            return 0;
        }
        if (currentWidth > 550 && currentHeight > 640) {
            return 2;
        }
        return 1;
    }

    private void setButtonFontSize(Button button, double newFontSize) {
        String buttonText = button.getText();

        if (labeledButtons.contains(buttonText)) {
            Label buttonLabel = (Label) button.getChildrenUnmodifiable().get(0);
            Font newFont = new Font(buttonLabel.getFont().getFamily(), newFontSize);
            buttonLabel.setFont(newFont);
        } else {
            Font newFont = new Font(button.getFont().getFamily(), newFontSize);
            button.setFont(newFont);
        }
    }
}
