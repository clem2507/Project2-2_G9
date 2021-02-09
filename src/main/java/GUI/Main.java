package GUI;

import backend.AssistantMessage;
import domains.SayThis;
import backend.Assistant;
import domains.Location.CurrentLocation;
import domains.Weather.CurrentWeather;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.*;
import javafx.stage.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends Application {

    final int WINDOW_WIDTH = 1200;
    final int WINDOW_HEIGHT = 700;
    int requestCounter = 0;

    boolean flag = true;

    Group pane = new Group();
    Group imagesLayout;
    Group chatLayout;

    Scene scene;

    TextField textField;

    String city;

    Rectangle chatWindow;
    Rectangle chatInputWindow;
    Rectangle weatherWidget;

    ScrollPane scrollPane;
    ScrollPane imagesScrollPane;

    Button editBgButton;

    Text dateText;
    Text timeText;
    Text botText;
    Text userText;
    Text messageTime;
    Text robotInteractionText;
    Text weatherCity;
    Text weatherDegree;

    Image bg;
    Image bg1;
    Image bg2;
    Image bg3;
    Image bg4;
    Image bg5;
    Image robot;
    Image robotInteraction;
    Image GPS;

    ImageView iv;
    ImageView imgView;
    ImageView imgView1;
    ImageView imgView2;
    ImageView imgView3;
    ImageView imgView4;
    ImageView imgView5;
    ImageView robotViewer;
    ImageView robotInteractionViewer;
    ImageView GPSView;

    Date currentDate;

    SimpleDateFormat time;
    SimpleDateFormat date;

    Assistant assistant;

    @Override
    public void start(Stage primaryStage) throws IOException {
        assistant = new Assistant();

        //Default Background
        bg = new Image(new FileInputStream("src/assets/cliff-background.jpg"));
        iv = new ImageView(bg);
        iv.setFitHeight(WINDOW_HEIGHT);
        iv.setFitWidth(WINDOW_WIDTH);
        pane.getChildren().add(iv);

        //Creating image view files for background menu
        imgView = new ImageView(bg);
        imgView.setTranslateX(1000);
        imgView.setTranslateY(200);
        imgView.setFitWidth(210);
        imgView.setFitHeight(150);

        bg1 = new Image(new FileInputStream("src/assets/bg1.jpg"));
        imgView1 = new ImageView(bg1);
        imgView1.setTranslateX(1000);
        imgView1.setTranslateY(370);
        imgView1.setFitWidth(210);
        imgView1.setFitHeight(150);

        bg2 = new Image(new FileInputStream("src/assets/bg2.jpg"));
        imgView2 = new ImageView(bg2);
        imgView2.setTranslateX(1000);
        imgView2.setTranslateY(540);
        imgView2.setFitWidth(210);
        imgView2.setFitHeight(150);

        bg3 = new Image(new FileInputStream("src/assets/bg3.jpg"));
        imgView3 = new ImageView(bg3);
        imgView3.setTranslateX(1000);
        imgView3.setTranslateY(710);
        imgView3.setFitWidth(210);
        imgView3.setFitHeight(150);

        bg4 = new Image(new FileInputStream("src/assets/bg4.jpg"));
        imgView4 = new ImageView(bg4);
        imgView4.setTranslateX(1000);
        imgView4.setTranslateY(880);
        imgView4.setFitWidth(210);
        imgView4.setFitHeight(150);

        bg5 = new Image(new FileInputStream("src/assets/bg5.jpg"));
        imgView5 = new ImageView(bg5);
        imgView5.setTranslateX(1000);
        imgView5.setTranslateY(1050);
        imgView5.setFitWidth(210);
        imgView5.setFitHeight(150);

        imagesLayout = new Group();
        imagesLayout.getChildren().addAll(imgView, imgView1, imgView2, imgView3, imgView4, imgView5);

        imagesScrollPane = new ScrollPane();
        imagesScrollPane.setContent(imagesLayout);
        imagesScrollPane.setPrefSize(228, 290);
        imagesScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; ");
        imagesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagesScrollPane.setFitToHeight(true);
        imagesScrollPane.setFitToWidth(true);

        editBgButton = new Button("Edit Background");
        editBgButton.setTranslateX(970);
        editBgButton.setTranslateY(40);
        editBgButton.setStyle(" -fx-background-radius: 30; -fx-background-insets: 0,1,1; -fx-text-fill: black; -fx-font-family: \"Gadugi\"; -fx-font-size: 14px; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ) ");
        imagesScrollPane.setTranslateX(editBgButton.getTranslateX()-55);
        imagesScrollPane.setTranslateY(editBgButton.getTranslateY()+40);
        pane.getChildren().add(editBgButton);

        weatherWidget = new Rectangle(140, 80);
        weatherWidget.setTranslateX(28);
        weatherWidget.setTranslateY(130);
        weatherWidget.setStroke(Color.WHITESMOKE);
        weatherWidget.setStrokeWidth(0.6);
        weatherWidget.setArcWidth(30);
        weatherWidget.setArcHeight(30);
        Stop[] stops = new Stop[] { new Stop(0, Color.SKYBLUE), new Stop(1, Color.WHITESMOKE)};
        LinearGradient lg = new LinearGradient(0, 0, 0, 2.5, true, CycleMethod.NO_CYCLE, stops);
        weatherWidget.setFill(lg);
        pane.getChildren().add(weatherWidget);

        // TODO: Update the city based on the location of the user
        city = CurrentLocation.getLocation();
        weatherCity = new Text(city);
        weatherCity.setFont(Font.font("Calibri Light", FontWeight.BOLD, FontPosture.REGULAR, 20));
        weatherCity.setTranslateX(weatherWidget.getTranslateX()+12);
        weatherCity.setTranslateY(weatherWidget.getTranslateY()+26);
        weatherCity.setFill(Color.WHITE);
        pane.getChildren().add(weatherCity);

        String temp = CurrentWeather.getWeather(city);
        double d = Double.parseDouble(temp);
        double rounded = Math.round(d);
        int i = (int) rounded;
        String temperature = Integer.toString(i);
        System.out.println(temperature);
        weatherDegree = new Text(temperature + "'C");
        weatherDegree.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 29));
        weatherDegree.setTranslateX(weatherWidget.getTranslateX()+18);
        weatherDegree.setTranslateY(weatherWidget.getTranslateY()+60);
        weatherDegree.setFill(Color.WHITE);
        pane.getChildren().add(weatherDegree);

        GPS = new Image(new FileInputStream("src/assets/GPSpointer.png"));
        GPSView = new ImageView(GPS);
        GPSView.setTranslateX(weatherCity.getTranslateX()+95);
        GPSView.setTranslateY(weatherCity.getTranslateY()-18);
        GPSView.setFitWidth(30);
        GPSView.setFitHeight(20);
        pane.getChildren().add(GPSView);

        chatWindow = new Rectangle(500, 500);
        chatWindow.setTranslateX(350);
        chatWindow.setTranslateY(50);
        chatWindow.setFill(Color.rgb(160, 160, 160, 0.75));
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
        textField.setFont(Font.font("Calibri", FontPosture.REGULAR, 16));
        pane.getChildren().add(textField);

        chatLayout = new Group();

        scrollPane = new ScrollPane();
        scrollPane.setContent(chatLayout);
        scrollPane.setTranslateX(chatWindow.getTranslateX()+2);
        scrollPane.setTranslateY(chatWindow.getTranslateY()+2);
        scrollPane.setPrefSize(chatWindow.getWidth()-4, chatWindow.getHeight()-4);
        scrollPane.setStyle(" -fx-background: transparent; -fx-background-color: transparent; ");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        chatLayout.layout();
        scrollPane.setVvalue(1.0d);

        pane.getChildren().add(scrollPane);

        robot = new Image(new FileInputStream("src/assets/robot.png"));
        robotViewer = new ImageView(robot);
        robotViewer.setTranslateX(980);
        robotViewer.setTranslateY(440);
        robotViewer.setFitWidth(200);
        robotViewer.setFitHeight(220);
        pane.getChildren().add(robotViewer);

        robotInteraction = new Image(new FileInputStream("src/assets/speechBubbleBot.png"));
        robotInteractionViewer = new ImageView(robotInteraction);
        robotInteractionViewer.setTranslateX(880);
        robotInteractionViewer.setTranslateY(390);
        robotInteractionViewer.setFitWidth(150);
        robotInteractionViewer.setFitHeight(100);
        pane.getChildren().add(robotInteractionViewer);

        robotInteractionText = new Text("Hi DKE student,\nhow can I help?");
        robotInteractionText.setFont(Font.font("Gadugi", FontWeight.BOLD, FontPosture.REGULAR, 15));
        robotInteractionText.setTranslateX(robotInteractionViewer.getTranslateX()+20);
        robotInteractionText.setTranslateY(robotInteractionViewer.getTranslateY()+30);
        robotInteractionText.setFill(Color.BLACK);
        pane.getChildren().add(robotInteractionText);

        currentDate = new Date();

        time = new SimpleDateFormat("hh:mm:ss");
        timeText = new Text(time.format(currentDate));
        timeText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 22));
        timeText.setTranslateX(30);
        timeText.setTranslateY(50);
        timeText.setFill(Color.WHITE);
        pane.getChildren().add(timeText);

        date = new SimpleDateFormat("dd/MM/yyyy");
        dateText = new Text(date.format(currentDate));
        dateText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 22));
        dateText.setTranslateX(30);
        dateText.setTranslateY(90);
        dateText.setFill(Color.WHITE);
        pane.getChildren().add(dateText);

        scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);

        Controller controller = new Controller(this);
        controller.setChatController();
        controller.setBackgroundController();

        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
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

    /**
     * Display the request and the answer in the chat
     * @param text query
     */
    public void sendText(String text) {

        currentDate = new Date();
        messageTime = new Text(time.format(currentDate));
        messageTime.setFont(Font.font("Gadugi", FontWeight.BOLD,  FontPosture.REGULAR, 12));
        messageTime.setTranslateY(55*requestCounter);
        messageTime.setFill(Color.BLACK);

        userText = new Text("User: " + text);
        userText.setFont(Font.font("Gadugi", FontWeight.BOLD,  FontPosture.REGULAR, 16));
        userText.setTranslateX(50);
        userText.setTranslateY(messageTime.getTranslateY()+2);
        userText.setFill(Color.WHITE);

        try {
            botText = new Text(assistant.getOutputOrWait().getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        botText.setFont(Font.font("Gadugi", FontWeight.BOLD, FontPosture.REGULAR, 16));
        botText.setTranslateX(50);
        botText.setTranslateY(userText.getTranslateY()+20);
        botText.setFill(Color.WHITE);


        chatLayout.getChildren().addAll(messageTime, userText, botText);
    }

    /**
     * Update the seconds for the time display
     */
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