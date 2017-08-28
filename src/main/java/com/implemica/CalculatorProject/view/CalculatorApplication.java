package com.implemica.CalculatorProject.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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

import static com.implemica.CalculatorProject.model.calculation.EditOperation.CLEAN;
import static com.implemica.CalculatorProject.model.calculation.EditOperation.CLEAN_CURRENT;
import static com.implemica.CalculatorProject.model.calculation.EditOperation.LEFT_ERASE;
import static com.implemica.CalculatorProject.model.calculation.MathOperation.*;
import static com.implemica.CalculatorProject.model.validation.DataValidator.isDigit;
import static javafx.scene.text.FontWeight.*;

/**
 * The class is an entry point to the application.
 *
 * @author V. Kozina-Kravchenko
 */
public class CalculatorApplication extends Application {

    /**
     * A path to the view fxml file.
     */
//    private static final String CALCULATOR_VIEW_FILE = "/com/implemica/CalculatorProject/view/calculatorView.fxml";
    private static final String CALCULATOR_VIEW_FILE = "/com/implemica/CalculatorProject/view/calculatorView2.fxml";

    /**
     * A path to the file with stylesheets.
     */
    private static final String CSS_FILE = "com/implemica/CalculatorProject/view/winCalc.css";

    /**
     * A path to the icon image.
     */
    private static final String ICON_FILE = "com/implemica/CalculatorProject/view/icon.png";

    /**
     * The name of the application.
     */
    private static final String APPLICATION_NAME = "Calculator";

    /**
     * The minimum height of an application's window.
     */
    private static final double MIN_HEIGHT = 385.0;

    /**
     * The minimum width of an application's window.
     */
    private static final double MIN_WIDTH = 220.0;

    /**
     * The primary {@link Stage} for current application, onto which the application {@link Scene} is set.
     */
    private Stage currentStage;

    /**
     * The {@link Parent} from FXML-file that contains all application's UI elements.
     */
    private Parent root;

    /**
     * The value of pixel count from left screen border to application's left border.
     */
    private double deltaX;

    /**
     * The value of pixel count from top screen border to application's top border.
     */
    private double deltaY;

    /**
     * The X coordinate of mouse position where was generated the current {@link MouseEvent}.
     */
    private double currentMouseX;

    /**
     * The Y coordinate of mouse position where was generated the current {@link MouseEvent}.
     */
    private double currentMouseY;

    /**
     * The X coordinate of mouse position where was generated the previous {@link MouseEvent}.
     */
    private double previousMouseX;

    /**
     * The Y coordinate of mouse position where was generated the previous {@link MouseEvent}.
     */
    private double previousMouseY;

    /**
     * The X coordinate of mouse position which is the coordinate of left border of {@link Stage} with minimal width.
     */
    private double maxResizeX;

    /**
     * The Y coordinate of mouse position which is the coordinate of top border of {@link Stage} with minimal height.
     */
    private  double maxResizeY;

    /**
     * TODO javadoc
     */
    private static final int RESIZE_PADDING = 2;
    private boolean isWindowMoving = false;

    /**
     * The default font family name for textfield.
     */
    private static final String TEXTFIELD_FONT_NAME = "Segoe UI Semibold";

    /**
     * The value of id for the textfield with current number.
     */
    private static final String CURRENT_NUMBER_TEXTFIELD_ID = "#currentNumberText";

    /**
     * The string value of the font id for buttons with digits and decimal separator.
     */
    private static final String FONT_ID_FOR_NUMBERS = "numbers";

    /**
     * The string value of the id for button with decimal separator.
     */
    private static final String POINT_BUTTON_ID = "point";

    /**
     * The value of id for the {@link GridPane} with buttons with numbers and operations.
     */
    private static final String PANE_WITH_BUTTONS_ID = "#numbers_operations";

    /**
     * A reference to the text field with the current number.
     */
    private TextField currentNumberTextField;

    /**
     * The list of button ids associated with an arrays contains minimum, medium and maximum values
     * of an appropriate button font size.
     */
    private static final Map<String, Double[]> fontSizes = new LinkedHashMap<>();

    /**
     * The list of buttons contains labels.
     */
    private List<String> labeledButtons = new ArrayList<>();

    {
        // add button's ids and its fonts
        fontSizes.put(PERCENT.name(), new Double[]{16.0, 19.0, 22.0});
        fontSizes.put(SQUARE_ROOT.name(), new Double[]{16.0, 17.0, 22.0});
        fontSizes.put(SQUARE.name(), new Double[]{16.0, 18.0, 22.0});
        fontSizes.put(REVERSE.name(), new Double[]{16.0, 18.0, 22.0});
        fontSizes.put(NEGATE.name(), new Double[]{25.0, 29.0, 36.0});
        fontSizes.put(DIVIDE.name(), new Double[]{24.0, 32.0, 42.0});
        fontSizes.put(MULTIPLY.name(), new Double[]{15.0, 19.0, 25.0});
        fontSizes.put(ADD.name(), new Double[]{24.0, 33.0, 42.0});
        fontSizes.put(SUBTRACT.name(), new Double[]{24.0, 35.0, 42.0});
        fontSizes.put(RESULT.name(), new Double[]{24.0, 34.0, 42.0});

        fontSizes.put("numbers", new Double[]{15.0, 20.0, 25.0});

        fontSizes.put(LEFT_ERASE.name(), new Double[]{15.0, 20.0, 25.0});
        fontSizes.put(CLEAN_CURRENT.name(), new Double[]{14.0, 15.0, 23.0});
        fontSizes.put(CLEAN.name(), new Double[]{14.0, 15.0, 23.0});

        // add main textfield and its fonts
        fontSizes.put(CURRENT_NUMBER_TEXTFIELD_ID, new Double[]{23.0, 42.0, 66.0});

        // add buttons with label ids
        labeledButtons.add(NEGATE.name());
        labeledButtons.add(DIVIDE.name());
        labeledButtons.add(MULTIPLY.name());
        labeledButtons.add(SUBTRACT.name());
        labeledButtons.add(ADD.name());
        labeledButtons.add(RESULT.name());
        labeledButtons.add(LEFT_ERASE.name());
    }

    /**
     * Configures the {@link Stage} instance and shows the configured application window.
     *
     * @param primaryStage an instance to configure for the current application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            currentStage = primaryStage;
            root = loadParent();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(CSS_FILE);
            currentStage.setScene(scene);
            currentStage.setTitle(APPLICATION_NAME);
            currentStage.getIcons().add(new Image(ICON_FILE));
            currentStage.setMinHeight(MIN_HEIGHT);
            currentStage.setMinWidth(MIN_WIDTH);
            currentStage.initStyle(StageStyle.UNDECORATED);
            currentStage.setResizable(true);

            // add listeners
            addWindowMoveListener();
            addFullScreenListener();
            addControlButtonsListeners();
            addResizeListeners();

            currentStage.show();
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

    private void addWindowMoveListener() {
        Label titleLabel = (Label) root.lookup("#appTitle");
        titleLabel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                deltaX = currentStage.getX() - event.getScreenX();
                deltaY = currentStage.getY() + RESIZE_PADDING - event.getScreenY();
                isWindowMoving = true;
                event.consume();
            }
        });

        titleLabel.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getScreenY() > currentStage.getY() + RESIZE_PADDING && isWindowMoving) {
                    currentStage.setX(event.getScreenX() + deltaX);
                    currentStage.setY(event.getScreenY() + deltaY);
                }
            }
        });
    }

    private void addFullScreenListener() {
        Label titleLabel = (Label) root.lookup("#appTitle"); // TODO remove duplicates
        titleLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int clickCount = event.getClickCount();
                if (clickCount == 2) {
                    currentStage.setMaximized(!currentStage.isMaximized());
                }
            }
        });
    }

    private void addControlButtonsListeners() {
        Button expandButton = (Button) root.lookup("#expand");
        expandButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentStage.setMaximized(!currentStage.isMaximized()); // TODO task bar overlays
            }
        });

        Button hideButton = (Button) root.lookup("#hide");
        hideButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentStage.setIconified(!currentStage.isIconified());
            }
        });

        Button closeButton = (Button) root.lookup("#close");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentStage.close();
            }
        });
    }

    private void addResizeListeners() {
        // set cursor style change when mouse hover borders
        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentMouseX = event.getScreenX();
                currentMouseY = event.getScreenY();

                if (isTopLeftCorner() || isRightBottomCorner()) {
                    setCursor(Cursor.NW_RESIZE);

                } else if (isLeftEdge() || isRightEdge()) {
                    setCursor(Cursor.H_RESIZE);

                } else if (isBottomLeftCorner() || isTopRightCorner()) {
                    setCursor(Cursor.NE_RESIZE);

                } else if (isBottomEdge() || isTopEdge()) {
                    setCursor(Cursor.V_RESIZE);

                } else {
                    setCursor(Cursor.DEFAULT);
                }
            }
        });

        // remember start coordinates before resize
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rememberEventCoordinates(event);
                maxResizeX = currentStage.getX() + currentStage.getWidth() - currentStage.getMinWidth() - RESIZE_PADDING;
                maxResizeY = currentStage.getY() + currentStage.getHeight() - currentStage.getMinHeight() - RESIZE_PADDING;
                if (isTopLeftCorner() || isTopEdge()) {
                    isWindowMoving = false;
                }
                event.consume();
            }
        });

        // resize window
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleResizeEvent(event);
            }
        });

        // add listeners for window resizing to change content font sizes
        currentStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scaleButtonFontSize();
            }
        });

        currentStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scaleButtonFontSize();
            }
        });

        // add listeners for textfield and its content resizing
        currentNumberTextField = (TextField) root.lookup(CURRENT_NUMBER_TEXTFIELD_ID);
        currentNumberTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                scaleTextFieldFont(currentNumberTextField.getBoundsInLocal().getWidth());
            }
        });

        currentNumberTextField.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scaleTextFieldFont(newValue.doubleValue());
            }
        });
    }

    private void handleResizeEvent(MouseEvent event) {
        if (isTopLeftCorner() && !isWindowMoving) {
            double newHeight = currentStage.getHeight() - event.getScreenY() + previousMouseY;
            double newWidth = currentStage.getWidth() - event.getScreenX() + previousMouseX;

            if (event.getScreenX() <= maxResizeX &&
                    event.getScreenY() <= maxResizeY) {
                setNewWidth(newWidth);
                setNewHeight(newHeight);
                currentStage.setX(event.getScreenX());
                currentStage.setY(event.getScreenY());
            }

        } else if (isLeftEdge()) {
            double newWidth = currentStage.getWidth() - event.getScreenX() + previousMouseX;
            if (event.getScreenX() <= maxResizeX) {
                setNewWidth(newWidth);
                currentStage.setX(event.getScreenX());
            }

        } else if (isBottomLeftCorner()) {
            double newHeight = currentStage.getHeight() + event.getScreenY() - previousMouseY;
            double newWidth = currentStage.getWidth() - event.getScreenX() + previousMouseX;
            if (event.getScreenX() <= maxResizeX) {
                setNewWidth(newWidth);
                currentStage.setX(event.getScreenX());
            }
            setNewHeight(newHeight);

        } else if (isBottomEdge()) {
            double newHeight = currentStage.getHeight() + event.getScreenY() - previousMouseY;
            setNewHeight(newHeight);

        } else if (isRightBottomCorner()) {
            double newHeight = currentStage.getHeight() + event.getScreenY() - previousMouseY;
            double newWidth = currentStage.getWidth() + event.getScreenX() - previousMouseX;
            setNewWidth(newWidth);
            setNewHeight(newHeight);

        } else if (isRightEdge()) {
            double newWidth = currentStage.getWidth() + event.getScreenX() - previousMouseX;
            setNewWidth(newWidth);

        } else if (isTopRightCorner()) {

            double newHeight = currentStage.getHeight() - event.getScreenY() + previousMouseY;
            double newWidth = currentStage.getWidth() - event.getScreenX() + previousMouseX;

            if (event.getScreenY() <= maxResizeY) {
                setNewHeight(newHeight);
                currentStage.setY(event.getScreenY());
            }
            setNewWidth(newWidth);

        } else if (isTopEdge() && !isWindowMoving) {
            double newHeight = currentStage.getHeight() - event.getScreenY() + previousMouseY;

            if (event.getScreenY() <= maxResizeY) {
                setNewHeight(newHeight);
                currentStage.setY(event.getScreenY());
            }
        }
        rememberEventCoordinates(event);
        event.consume();
    }

    private boolean isTopLeftCorner() {
        double distanceToLeftEdge = currentMouseX - currentStage.getX();
        double distanceToTopEdge = currentMouseY - currentStage.getY();

        return distanceToLeftEdge <= RESIZE_PADDING &&
                distanceToTopEdge <= RESIZE_PADDING;
    }

    private boolean isLeftEdge() {
        double distanceToLeftEdge = currentMouseX - currentStage.getX();
        double distanceToTopEdge = currentMouseY - currentStage.getY();

        return distanceToTopEdge <= currentStage.getHeight() - RESIZE_PADDING &&
                distanceToLeftEdge <= RESIZE_PADDING;
    }

    private boolean isBottomLeftCorner() {
        double distanceToLeftEdge = currentMouseX - currentStage.getX();
        double distanceToBottomEdge = currentStage.getY() + currentStage.getHeight() - currentMouseY;

        return distanceToLeftEdge <= RESIZE_PADDING &&
                distanceToBottomEdge <= RESIZE_PADDING;
    }

    private boolean isBottomEdge() {
        double distanceToBottomEdge = currentStage.getY() + currentStage.getHeight() - currentMouseY;
        double distanceToLeftEdge = currentMouseX - currentStage.getX();

        return distanceToLeftEdge <=  currentStage.getWidth() - RESIZE_PADDING &&
                distanceToBottomEdge <= RESIZE_PADDING;
    }

    private boolean isRightBottomCorner() {
        double distanceToBottomEdge = currentStage.getY() + currentStage.getHeight() - currentMouseY;
        double distanceToRightEdge = currentStage.getX() + currentStage.getWidth() - currentMouseX;

        return distanceToRightEdge <= RESIZE_PADDING &&
                distanceToBottomEdge <= RESIZE_PADDING;
    }

    private boolean isRightEdge() {
        double distanceToTopEdge = currentMouseY - currentStage.getY();
        double distanceToRightEdge = currentStage.getX() + currentStage.getWidth() - currentMouseX;

        return distanceToTopEdge <= currentStage.getHeight() - RESIZE_PADDING &&
                distanceToRightEdge <= RESIZE_PADDING;
    }

    private boolean isTopRightCorner() {
        double distanceToTopEdge = currentMouseY - currentStage.getY();
        double distanceToRightEdge = currentStage.getX() + currentStage.getWidth() - currentMouseX;

        return distanceToTopEdge <= RESIZE_PADDING &&
                distanceToRightEdge <= RESIZE_PADDING;
    }

    private boolean isTopEdge() {
        double distanceToTopEdge = currentMouseY - currentStage.getY();
        double distanceToLeftEdge = currentMouseX - currentStage.getX();

        return distanceToTopEdge < RESIZE_PADDING &&
                distanceToLeftEdge <= currentStage.getWidth() - RESIZE_PADDING;
    }

    private void setCursor(Cursor cursor) {
        currentStage.getScene().setCursor(cursor);
    }

    private void rememberEventCoordinates(MouseEvent event) {
        previousMouseX = event.getScreenX();
        previousMouseY = event.getScreenY();
        currentMouseX = event.getScreenX();
        currentMouseY = event.getScreenY();
    }

    private void setNewWidth(double newWidth) {
        if (newWidth >= MIN_WIDTH && newWidth <= currentStage.getMaxWidth()) {
            currentStage.setWidth(newWidth);
        }
    }

    private void setNewHeight(double newHeight) {
        if (newHeight >= MIN_HEIGHT && newHeight <= currentStage.getMaxHeight()) {
            currentStage.setHeight(newHeight);
        }
    }

    /**
     * Changes font size on buttons depends on window size to avoid button text overflow.
     */
    private void scaleButtonFontSize() {
        GridPane pane = (GridPane) root.lookup(PANE_WITH_BUTTONS_ID);

        for (Node node : pane.getChildren()) {
            Button button = (Button) node;
            String buttonId = button.getId();

            double newFontSize = getFontSize(buttonId, getFontBoundIndex());
            setButtonFontSize(button, newFontSize);
        }
    }

    /**
     * Changes font size in the text field with current entered number depends on window and content sizes
     * to avoid text truncating.
     *
     * @param currentWidth current window width value
     */
    private void scaleTextFieldFont(double currentWidth) {
        Font font = getFontForTextField(currentWidth);
        currentNumberTextField.setFont(font);
        Platform.runLater(() -> currentNumberTextField.end());
    }

    /**
     * Returns a {@link Font} for the textfield with current number with calculated font size to fit text to
     * the textfield.
     *
     * @param currentWidth current window width value
     * @return a {@link Font} for the textfield with current number with calculated font size to fit text to
     * the textfield
     */
    private Font getFontForTextField(double currentWidth) {
        int defaultFontSizeIndex = getFontBoundIndex();
        double defaultFontSize = fontSizes.get(CURRENT_NUMBER_TEXTFIELD_ID)[defaultFontSizeIndex];
        Font defaultFont = findFontForTextField(defaultFontSize);

        Text text = new Text(currentNumberTextField.getText());
        text.setFont(defaultFont);

        // calculate scale
        double newFontSize = defaultFontSize;
        double textWidth = text.getLayoutBounds().getWidth();
        double scale = currentWidth / textWidth - 0.1;
        if (scale < 1.0) {
            newFontSize = defaultFont.getSize() * scale;
        }

        return findFontForTextField(newFontSize);
    }

    /**
     * Finds and returns {@link Font} instance that has {@link CalculatorApplication#TEXTFIELD_FONT_NAME} family,
     * {@link FontWeight#BOLD} and the given size.
     *
     * @param fontSize a value of font size to apply to the current font
     * @return {@link Font} instance that has {@link CalculatorApplication#TEXTFIELD_FONT_NAME} family,
     * {@link FontWeight#BOLD} and the given size
     */
    private Font findFontForTextField(double fontSize) {
        return Font.font(TEXTFIELD_FONT_NAME, BOLD, fontSize);
    }

    /**
     * Returns an index of bound calculated based on window width and height.
     * Example: 0 - smallest window size bound, 1 - medium, 2 - largest.
     *
     * @return an index of bound calculated based on window width and height
     */
    private int getFontBoundIndex() {
        /*
            if width and height in appropriate scopes -> get for this scope
            if in different scopes -> get for min parameter

                        min         middle      max
            width       200         280+        550+
            height      370         480+        640+
         */

        double currentWidth = currentStage.getWidth();
        double currentHeight = currentStage.getHeight();

        if (currentWidth < 280 || currentHeight < 480) {
            return 0;
        }
        if (currentWidth > 550 && currentHeight > 640) {
            return 2;
        }
        return 1;
    }

    /**
     * Returns font size for the element with specified id and index of current window size bound.
     *
     * @param elementId  a value of element id to get font size for
     * @param boundIndex an index of current window size bound
     * @return font size for the element with specified id and index of current window size bound
     */
    private double getFontSize(String elementId, int boundIndex) {
        if (isDigit(elementId) || POINT_BUTTON_ID.equals(elementId)) {
            return fontSizes.get(FONT_ID_FOR_NUMBERS)[boundIndex];
        } else {
            return fontSizes.get(elementId.toUpperCase())[boundIndex];
        }
    }

    /**
     * Sets the given font size for the specified button.
     *
     * @param button      a button to change font size
     * @param newFontSize a value of a new font size
     */
    private void setButtonFontSize(Button button, double newFontSize) {
        String buttonId = button.getId();

        if (labeledButtons.contains(buttonId.toUpperCase())) {
            Label buttonLabel = (Label) button.getChildrenUnmodifiable().get(0);
            Font newFont = new Font(buttonLabel.getFont().getFamily(), newFontSize);
            buttonLabel.setFont(newFont);
        } else {
            Font newFont = new Font(button.getFont().getFamily(), newFontSize);
            button.setFont(newFont);
        }
    }
}
