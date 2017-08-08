package com.implemica.CalculatorProject;

import com.implemica.CalculatorProject.calculation.EditOperation;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

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
                if (Double.isNaN(oldValue.doubleValue()) || Double.isNaN(newValue.doubleValue())) {
                    return;
                }
                double scale = newValue.doubleValue() / oldValue.doubleValue();
                changeFontSize(root, scale);
            });

            primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> changeFontSizeForHeight(root));

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


    private void changeFontSize(Parent root, double scale) {
        GridPane pane = (GridPane) root.lookup("#numbers_operations");

        for (Node node : pane.getChildren()) {
            Button button = (Button) node;
            String buttonText = button.getText();

            double minFontSize = 16.0;
            double maxFontSize = 25.0;


            if (ADD.getCode().equals(buttonText) ||
                    SUBTRACT.getCode().equals(buttonText) ||
                    DIVIDE.getCode().equals(buttonText) ||
                    RESULT.getCode().equals(buttonText) ||
                    NEGATE.getCode().equals(buttonText)) {

                Label buttonLabel = (Label) button.getChildrenUnmodifiable().get(0);
                double newFontSize = buttonLabel.getFont().getSize() * scale;

                if (newFontSize > 42) {
                    newFontSize = 42;
                }
                if (newFontSize < 24) {
                    newFontSize = 24;
                }
                buttonLabel.setFont(new Font(buttonLabel.getFont().getFamily(), newFontSize));
            }

            if (MULTIPLY.getCode().equals(buttonText) ||
                    LEFT_ERASE.getCode().equals(buttonText)) {
                Label buttonLabel = (Label) button.getChildrenUnmodifiable().get(0);
                double newFontSize = buttonLabel.getFont().getSize() * scale;

                if (newFontSize > 25) {
                    newFontSize = 25;
                }
                if (newFontSize < 15) {
                    newFontSize = 15;
                }
                buttonLabel.setFont(new Font(buttonLabel.getFont().getFamily(), newFontSize));
            }

            if (EditOperation.CLEAN.getCode().equals(button.getText()) ||
                    EditOperation.CLEAN_CURRENT.getCode().equals(button.getText())) {

                minFontSize = 14.0;
                maxFontSize = 23.0;
            }

            double newFontSize = button.getFont().getSize() * scale;
            Text buttonContent = new Text(button.getText());
            buttonContent.setFont(button.getFont());

            if (newFontSize > maxFontSize) {
                newFontSize = maxFontSize;
            }

            if (buttonContent.getLayoutBounds().getWidth() > (button.getBoundsInLocal().getWidth() * 0.7)) {
                newFontSize = newFontSize * ((button.getBoundsInLocal().getWidth() * 0.7) /
                        buttonContent.getLayoutBounds().getWidth());
            }

            if (newFontSize < minFontSize) {
                newFontSize = minFontSize;
            }

            if (buttonContent.getLayoutBounds().getHeight() > (button.getBoundsInLocal().getHeight() * 0.7)) {
                newFontSize = newFontSize * ((button.getBoundsInLocal().getHeight() * 0.65) /
                        buttonContent.getLayoutBounds().getHeight());
            }


            button.setFont(new Font(button.getFont().getFamily(), newFontSize));
        }

        TextField currentNumberText = (TextField) root.lookup("#currentNumberText");
        double textFontSize =  currentNumberText.getFont().getSize() * scale; // currentNumberText.getHeight() * scale * 0.42;

        Text text = new Text(currentNumberText.getText());
        text.setFont(currentNumberText.getFont());

        if (text.getLayoutBounds().getWidth() > (currentNumberText.getBoundsInLocal().getWidth())) {
            textFontSize = textFontSize * ((currentNumberText.getBoundsInLocal().getWidth()) /
                    text.getLayoutBounds().getWidth()) - 0.05;
        }
        if (textFontSize > 66) {
            textFontSize = 66;
        }

        if (textFontSize < 16) {
            textFontSize = 16;
        }
        currentNumberText.setFont(new Font(currentNumberText.getFont().getFamily(), textFontSize));
        currentNumberText.end();
    }

    private void changeFontSizeForHeight(Parent root) {
        GridPane pane = (GridPane) root.lookup("#numbers_operations");

        for (Node node : pane.getChildren()) {
            Button button = (Button) node;
            String buttonText = button.getText();

            double newFontSize = button.getFont().getSize();
            Text buttonContent = new Text(buttonText);
            buttonContent.setFont(button.getFont());

            if (buttonContent.getLayoutBounds().getHeight() > (button.getBoundsInLocal().getHeight() * 0.7)) {
                newFontSize = newFontSize * ((button.getBoundsInLocal().getHeight() * 0.65) /
                        buttonContent.getLayoutBounds().getHeight());
            }
            button.setFont(new Font(button.getFont().getFamily(), newFontSize));
        }

        TextField currentNumberText = (TextField) root.lookup("#currentNumberText");
        double textFontSize = currentNumberText.getFont().getSize();

        Text text = new Text(currentNumberText.getText());
        text.setFont(currentNumberText.getFont());

        if (text.getLayoutBounds().getHeight() > (currentNumberText.getBoundsInLocal().getHeight())) {
            textFontSize = textFontSize * ((currentNumberText.getBoundsInLocal().getHeight()) /
                    text.getLayoutBounds().getHeight()) - 0.05;
        }
        if (textFontSize > 66) {
            textFontSize = 66;
        }

        if (textFontSize < 16) {
            textFontSize = 16;
        }
        currentNumberText.setFont(new Font(currentNumberText.getFont().getFamily(), textFontSize));
        currentNumberText.end();
        // TODO FONT SCALING!!!!!
    }
}
