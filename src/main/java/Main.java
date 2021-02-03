import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.*;
import javafx.stage.*;

import java.io.IOException;

public class Main extends Application {

    private Group pane = new Group();

    private final int WINDOW_WIDTH = 600;
    private final int WINDOW_HEIGHT = 600;

    private TextField textField;
    private Button startButton;

    @Override
    public void start(Stage primaryStage) {

        textField = new TextField();
        textField.setPromptText("Input...");
        textField.setTranslateX(205);
        textField.setTranslateY(250);
        textField.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 15));
        pane.getChildren().add(textField);

        startButton = new Button("start");
        startButton.setTranslateX(270);
        startButton.setTranslateY(300);
        startButton.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 17));
        startButton.setOnAction(event -> {
            System.out.println("Text: " + textField.getText());
            // Add an action when the button is pressed
        });
        pane.getChildren().add(startButton);

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Digital Assistant");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
