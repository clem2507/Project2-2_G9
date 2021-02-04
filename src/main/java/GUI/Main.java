package GUI;

import Skills.PrintSkill;
import backend.Assistant;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.*;
import javafx.stage.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends Application {

    private Group pane = new Group();

    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 700;

    private TextField textField;

    private Rectangle chatWindow;
    private Rectangle chatInputWindow;
    private ScrollPane scrollPane;
    private ScrollPane scrollPane2;
    private Group chatLayout;

    private int requestCounter = 0;

    private Text dateText;
    private Text timeText;
    private Date currentDate;
    private SimpleDateFormat time;
    private SimpleDateFormat date;

    private Assistant assistant;

    @Override
    public void start(Stage primaryStage)  throws FileNotFoundException {
        assistant = new Assistant();
        assistant.addSkill(new PrintSkill());

        //Default Background
        Image bg = new Image(new FileInputStream("src/assets/cliff-background.jpg"));
        ImageView iv = new ImageView(bg);
        iv.setFitHeight(WINDOW_HEIGHT);
        iv.setFitWidth(WINDOW_WIDTH);
        pane.getChildren().add(iv);

        //Creating image view files for background menu
        Image bg1 = new Image(new FileInputStream("src/assets/bg1.jpg"));
        ImageView imgView1 = new ImageView(bg1);
        imgView1.setFitWidth(200);
        imgView1.setFitHeight(200);
        Image bg2 = new Image(new FileInputStream("src/assets/bg2.jpg"));
        ImageView imgView2 = new ImageView(bg2);
        imgView2.setFitWidth(200);
        imgView2.setFitHeight(200);
        Image bg3 = new Image(new FileInputStream("src/assets/bg3.jpg"));
        ImageView imgView3 = new ImageView(bg3);
        imgView3.setFitWidth(200);
        imgView3.setFitHeight(200);

        Menu fileMenu = new Menu("Edit Background");

        MenuItem item1 = new MenuItem("",imgView1);
        MenuItem item2 = new MenuItem("", imgView2);
        MenuItem item3 = new MenuItem("", imgView3);

        fileMenu.getItems().addAll(item1, item2, item3);
        //Creating a menu bar and adding menu to it.
        MenuBar menuBar = new MenuBar(fileMenu);
        menuBar.setTranslateX(1000);
        menuBar.setTranslateY(40);
        menuBar.setOpacity(0.7);
        pane.getChildren().add(menuBar);

        //TODO: Make it change the background according on selected item
        item1.setOnAction(e -> {
            System.out.println("Menu Item 1 Selected");
        });

        chatWindow = new Rectangle(500, 500);
        chatWindow.setTranslateX(350);
        chatWindow.setTranslateY(50);
        chatWindow.setFill(Color.rgb(160, 160, 160, 0.7));
        chatWindow.setStroke(Color.WHITE);
        chatWindow.setStrokeWidth(1);
        pane.getChildren().add(chatWindow);

        chatInputWindow = new Rectangle(500, 100);
        chatInputWindow.setTranslateX(350);
        chatInputWindow.setTranslateY(550);
        chatInputWindow.setFill(Color.rgb(200, 200, 200, 0.8));
        chatInputWindow.setStroke(Color.WHITE);
        chatInputWindow.setStrokeWidth(1);
        pane.getChildren().add(chatInputWindow);

        textField = new TextField();
        textField.setPromptText("Input...");
        textField.setFocusTraversable(false);
        textField.setTranslateX(400);
        textField.setTranslateY(580);
        textField.setPrefSize(400, 40);
        textField.setFont(Font.font("Calibri Light", FontPosture.REGULAR, 16));
        pane.getChildren().add(textField);

        chatLayout = new Group();

        scrollPane = new ScrollPane();
        scrollPane.setContent(chatLayout);
        scrollPane.setTranslateX(chatWindow.getTranslateX()+2);
        scrollPane.setTranslateY(chatWindow.getTranslateY()+2);
        scrollPane.setPrefSize(chatWindow.getWidth()-4, chatWindow.getHeight()-4);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; ");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        pane.getChildren().add(scrollPane);

        currentDate = new Date();

        time = new SimpleDateFormat("hh:mm:ss");
        timeText = new Text(time.format(currentDate));
        timeText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 21));
        timeText.setTranslateX(30);
        timeText.setTranslateY(50);
        timeText.setFill(Color.WHITE);
        pane.getChildren().add(timeText);

        date = new SimpleDateFormat("dd/MM/yyyy");
        dateText = new Text(date.format(currentDate));
        dateText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 21));
        dateText.setTranslateX(30);
        dateText.setTranslateY(90);
        dateText.setFill(Color.WHITE);
        pane.getChildren().add(dateText);

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.setOnKeyPressed(t -> {
            KeyCode key = t.getCode();
            switch (key) {
                case ESCAPE:
                    System.exit(0);
                    break;
                case ENTER:
                    if (textField.getText().length() > 0) {
                        assistant.processQuery(textField.getText());
                        requestCounter++;
                        sendText(textField.getText());
                        textField.setText("");
                    }
                    break;
            }
        });

        primaryStage.setResizable(false);
        primaryStage.setTitle("Digital Assistant");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();

        Thread mainThread = new Thread(() -> {
            while (true) {
                updateTime();
            }
        });
        mainThread.setDaemon(false);
        mainThread.start();
    }

    public void sendText(String text) {

        Text userText = new Text("User: " + text);
        userText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 18));
        userText.setTranslateX(30);
        userText.setTranslateY(50*requestCounter);
        userText.setFill(Color.WHITE);

        Text botText = new Text("Bot: bot's answer...");
        botText.setFont(Font.font("Calibri Light", FontWeight.BOLD, FontPosture.REGULAR, 18));
        botText.setTranslateX(30);
        botText.setTranslateY(userText.getTranslateY()+20);
        botText.setFill(Color.WHITE);

        chatLayout.getChildren().addAll(userText, botText);
    }

    public void updateTime() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        currentDate = new Date();
        timeText.setText(time.format(currentDate));
    }

    public static void main(String[] args) {
        launch(args);
    }
}