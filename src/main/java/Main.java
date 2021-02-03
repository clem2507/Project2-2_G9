import backend.Assistant;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.*;
import javafx.stage.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main extends Application {

    private Group pane = new Group();

    private final int WINDOW_WIDTH = 600;
    private final int WINDOW_HEIGHT = 600;

    private TextField textField;
    private Button startButton;

    @Override
    public void start(Stage primaryStage)  throws FileNotFoundException {

        Assistant newAssistant = new Assistant();


        Image bg =new Image(new FileInputStream("src/assets/cliff-background.jpg"));
        ImageView iv = new ImageView(bg);
        iv.setFitHeight(600);
        iv.setFitWidth(600);
        pane.getChildren().add(iv);

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

//            newAssistant.processQuery(textField.getText());
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
