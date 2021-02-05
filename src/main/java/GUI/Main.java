package GUI;

import Skills.SaySkill;
import backend.Assistant;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.*;
import javafx.stage.*;
import nlp.Pattern;
import nlp.Tokenizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Main extends Application {

    private Group pane = new Group();

    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 700;

    private TextField textField;

    private Rectangle chatWindow;
    private Rectangle chatInputWindow;

    private ScrollPane scrollPane;
    private Group chatLayout;

    private Button editBgButton;
    private ScrollPane imagesScrollPane;
    private Group imagesLayout;
    private boolean flag = true;

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
        assistant.addSkill(new SaySkill());

        //Default Background
        Image bg = new Image(new FileInputStream("src/assets/cliff-background.jpg"));
        ImageView iv = new ImageView(bg);
        iv.setFitHeight(WINDOW_HEIGHT);
        iv.setFitWidth(WINDOW_WIDTH);
        pane.getChildren().add(iv);

        //Creating image view files for background menu
        ImageView imgView = new ImageView(bg);
        imgView.setTranslateX(1000);
        imgView.setTranslateY(200);
        imgView.setFitWidth(210);
        imgView.setFitHeight(150);
        imgView.setOnMousePressed(event -> {
            iv.setImage(bg);
        });

        Image bg1 = new Image(new FileInputStream("src/assets/bg1.jpg"));
        ImageView imgView1 = new ImageView(bg1);
        imgView1.setTranslateX(1000);
        imgView1.setTranslateY(370);
        imgView1.setFitWidth(210);
        imgView1.setFitHeight(150);
        imgView1.setOnMousePressed(event -> {
            iv.setImage(bg1);
        });

        Image bg2 = new Image(new FileInputStream("src/assets/bg2.jpg"));
        ImageView imgView2 = new ImageView(bg2);
        imgView2.setTranslateX(1000);
        imgView2.setTranslateY(540);
        imgView2.setFitWidth(210);
        imgView2.setFitHeight(150);
        imgView2.setOnMousePressed(event -> {
            iv.setImage(bg2);
        });

        Image bg3 = new Image(new FileInputStream("src/assets/bg3.jpg"));
        ImageView imgView3 = new ImageView(bg3);
        imgView3.setTranslateX(1000);
        imgView3.setTranslateY(710);
        imgView3.setFitWidth(210);
        imgView3.setFitHeight(150);
        imgView3.setOnMousePressed(event -> {
            iv.setImage(bg3);
        });

        imagesLayout = new Group();
        imagesLayout.getChildren().addAll(imgView, imgView1, imgView2, imgView3);

        imagesScrollPane = new ScrollPane();
        imagesScrollPane.setContent(imagesLayout);
        imagesScrollPane.setPrefSize(228, 250);
        imagesScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; ");
        imagesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagesScrollPane.setFitToHeight(true);
        imagesScrollPane.setFitToWidth(true);

        editBgButton = new Button("Edit Background");
        editBgButton.setTranslateX(970);
        editBgButton.setTranslateY(40);
        imagesScrollPane.setTranslateX(editBgButton.getTranslateX()-55);
        imagesScrollPane.setTranslateY(editBgButton.getTranslateY()+40);
        editBgButton.setOnAction(event -> {
            if (flag) {
                pane.getChildren().add(imagesScrollPane);
                flag = false;
            }
            else {
                pane.getChildren().remove(imagesScrollPane);
                flag = true;
            }
        });
        pane.getChildren().add(editBgButton);

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

        chatLayout.getChildren().addListener(
                (ListChangeListener.Change<? extends  Node> c) -> {
                    chatLayout.layout();
                    scrollPane.setVvalue(1.0d);
                }
        );

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
        chatLayout.layout();
        scrollPane.setVvalue(1.0d);

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

        currentDate = new Date();
        Text messageTime = new Text(time.format(currentDate));
        messageTime.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 12));
        messageTime.setTranslateY(55*requestCounter);
        messageTime.setFill(Color.BLACK);

        Text userText = new Text("User: " + text);
        userText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 18));
        userText.setTranslateX(50);
        userText.setTranslateY(messageTime.getTranslateY()+2);
        userText.setFill(Color.WHITE);

        Text botText = new Text("Bot: bot's answer...");
        botText.setFont(Font.font("Calibri Light", FontWeight.BOLD, FontPosture.REGULAR, 18));
        botText.setTranslateX(50);
        botText.setTranslateY(userText.getTranslateY()+20);
        botText.setFill(Color.WHITE);

        chatLayout.getChildren().addAll(messageTime, userText, botText);
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