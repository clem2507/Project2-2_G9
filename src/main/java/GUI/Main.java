package GUI;

import backend.Assistant;
import backend.AssistantMessage;
import backend.MessageType;
import backend.common.CurrentTime;
import backend.common.Quote;
import backend.common.WeatherObject;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    final double WINDOW_WIDTH = screenSize.getWidth() - 5;
    final double WINDOW_HEIGHT = screenSize.getHeight() - 70;

    int requestCounter = 0;

    boolean flag = true;
    boolean isAgentFree = true;

    Pane pane = new Pane();

    Group imagesLayout;
    Group chatLayout;

    Scene scene;

    TextField textField;

    String city;
    String time1;
    String time2;
    String time3;
    String time4;
    String time5;

    ArrayList<String> messageHistory;

    Rectangle chatWindow;
    Rectangle chatInputWindow;
    Rectangle weatherWidget;
    Rectangle timezones;
    Rectangle dropFile;
    Rectangle quoteOutline;

    ScrollPane scrollPane;
    ScrollPane imagesScrollPane;

    Button editBgButton;

    Text dateText;
    Text timeText;
    Text timeText1;
    Text timeText2;
    Text timeText3;
    Text timeText4;
    Text timeText5;
    Text time1city;
    Text time2city;
    Text time3city;
    Text time4city;
    Text time5city;
    Text userText;
    Text botText;
    Text robotInteractionText;
    Text messageTime;
    Text weatherCity;
    Text weatherDegree;
    Text quote;
    Text quoteHeading;

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
    ImageView webcam;

    int editBgButtonWidth = 130;
    int editBgButtonHeight = 20;
    int editBgButtonX = (int) (WINDOW_WIDTH-170);
    int editBgButtonY = 40;

    int imagesScrollPaneWidth = 228;
    int imagesScrollPaneHeight = 310;
    int imagesScrollPaneX = editBgButtonX-55;
    int imagesScrollPaneY = editBgButtonY+40;

    int chatWindowWidth = 700;
    int chatWindowHeight = 500;
    int chatWindowX = 490;
    int chatWindowY = 90;

    int chatInputWindowWidth = 700;
    int chatInputWindowHeight = 100;
    int chatInputWindowX = chatWindowX;
    int chatInputWindowY = chatWindowHeight+chatWindowY;

    int textFieldWidth = 600;
    int textFieldHeight = 40;
    int textFieldFontSize = 16;
    int textFieldX = chatInputWindowX+45;
    int textFieldY = chatInputWindowY+30;

    int scrollPaneWidth = chatWindowWidth-4;
    int scrollPaneHeight = chatWindowHeight-4;
    int scrollPaneX = chatWindowX+2;
    int scrollPaneY = chatWindowY+2;

    int robotViewerWidth = 200;
    int robotViewerHeight = 220;
    int robotViewerX = chatInputWindowX+825;
    int robotViewerY = chatInputWindowY-110;

    int robotInteractionViewerWidth = 150;
    int robotInteractionViewerHeight = 100;
    int robotInteractionViewerX = robotViewerX-105;
    int robotInteractionViewerY = robotViewerY-55;

    int robotInteractionTextFontSize = 15;
    int robotInteractionTextX = robotInteractionViewerX+20;
    int robotInteractionTextY = robotInteractionViewerY+30;

    double weatherWidgetWidth;
    int weatherWidgetHeight = 80;
    int weatherWidgetX = 28;
    int weatherWidgetY = 130;

    int weatherCityFontSize = 20;
    int weatherCityX = weatherWidgetX+17;
    int weatherCityY = weatherWidgetY+26;

    int weatherDegreeFontSize = 29;
    int weatherDegreeX = weatherWidgetX+18;
    int weatherDegreeY = weatherWidgetY+60;

    int gpsViewerWidth = 30;
    int gpsViewerHeight = 20;
    double gpsViewerX;
    int gpsViewerY = weatherCityY-18;

    int timeTextFontSize = 22;
    int timeTextX = 30;
    int timeTextY = 50;

    int dateTextFontSize = 22;
    int dateTextX = 30;
    int dateTextY = 90;

    int timezonesWidth = 425;
    int timezonesHeight = 65;
    int timezonesX = 30;
    int timezonesY = 240;

    int timeText1FontSize = 17;
    int timeText1X = timezonesX+15;
    int timeText1Y = timezonesY+30;

    int time1cityFontSize = 16;
    int time1cityX = timezonesX+18;
    int time1cityY = timezonesY+50;

    int timeText2FontSize = 17;
    int timeText2X = timezonesX+95;
    int timeText2Y = timezonesY+30;

    int time2cityFontSize = 16;
    int time2cityX = timezonesX+100;
    int time2cityY = timezonesY+50;

    int timeText3FontSize = 17;
    int timeText3X = timezonesX+175;
    int timeText3Y = timezonesY+30;

    int time3cityFontSize = 16;
    int time3cityX = timezonesX+180;
    int time3cityY = timezonesY+50;

    int timeText4FontSize = 17;
    int timeText4X = timezonesX+260;
    int timeText4Y = timezonesY+30;

    int time4cityFontSize = 16;
    int time4cityX = timezonesX+260;
    int time4cityY = timezonesY+50;

    int timeText5FontSize = 17;
    int timeText5X = timezonesX+345;
    int timeText5Y = timezonesY+30;

    int time5cityFontSize = 16;
    int time5cityX = timezonesX+349;
    int time5cityY = timezonesY+50;

    int dropFileWidth = 143;
    int dropFileHeight = 35;
    int dropFileX = 30;
    int dropFileY = 440;

    int targetFontSize = 17;
    int targetX = dropFileX+10;
    int targetY = dropFileY+22;

    int quoteOutlineFontSize = 19;
    int quoteOutlineX = 30;
    int quoteOutlineY = 340;

    int quoteFontSize = 16;
    int quoteX = quoteOutlineX + 10;
    int quoteY = quoteOutlineY + 50;

    int quoteRectangleWidth = timezonesWidth;
    int quoteRectangleHeightIndicator;
    int quoteRectangleHeight = timezonesHeight;
    int quoteRectangleX = quoteOutlineX;
    int quoteRectangleY = quoteOutlineY;

    Date currentDate;

    SimpleDateFormat time;
    SimpleDateFormat date;

    Text target;

    Assistant assistant = new Assistant();
    BlockingQueue<ConsoleOutput> consoleOutput = new LinkedBlockingQueue<>();
    private int outputMessageHeight = 0;

    Thread queryThread;

    @Override
    public void start(Stage primaryStage) throws IOException {

        String quoteText = Quote.getQuote();
        quoteText = processText(quoteText);

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
        imagesScrollPane.setPrefSize(228, 310);
        imagesScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; ");
        imagesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagesScrollPane.setFitToHeight(true);
        imagesScrollPane.setFitToWidth(true);

        editBgButton = new Button("Edit Background");
        editBgButton.setPrefWidth(editBgButtonWidth);
        editBgButton.setPrefHeight(editBgButtonHeight);
        editBgButton.setTranslateX(editBgButtonX);
        editBgButton.setTranslateY(editBgButtonY);
        editBgButton.setStyle(" -fx-background-radius: 30; -fx-background-insets: 0,1,1; -fx-text-fill: black; -fx-font-family: \"Gadugi\"; -fx-font-size: 14px; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ) ");
        imagesScrollPane.setTranslateX(imagesScrollPaneX);
        imagesScrollPane.setTranslateY(imagesScrollPaneY);
        pane.getChildren().add(editBgButton);

        //weatherWidget = new Rectangle(140, 80);
        weatherWidget = new Rectangle();
        weatherWidget.setTranslateX(weatherWidgetX);
        weatherWidget.setTranslateY(weatherWidgetY);
        weatherWidget.setStroke(Color.WHITESMOKE);
        weatherWidget.setStrokeWidth(0.6);
        weatherWidget.setArcWidth(30);
        weatherWidget.setArcHeight(30);
        Stop[] stops = new Stop[] { new Stop(0, Color.SKYBLUE), new Stop(1, Color.WHITESMOKE)};
        LinearGradient lg = new LinearGradient(0, 0, 0, 2.5, true, CycleMethod.NO_CYCLE, stops);
        weatherWidget.setFill(lg);
        //pane.getChildren().add(weatherWidget);

        // TODO: Change when submitting the project
        /**
        try {
            city = CurrentLocation.getLocation();
        } catch (IOException e){
            e.printStackTrace();
            city = "Unknown";
        }**/

        city = "Maastricht";
        weatherCity = new Text(city);
        weatherCity.setFont(Font.font("Calibri Light", FontWeight.BOLD, FontPosture.REGULAR, weatherCityFontSize));
        weatherWidgetWidth = findBestWeatherWidgetWidth(city);
        weatherWidget.setWidth(weatherWidgetWidth);
        weatherWidget.setHeight(weatherWidgetHeight);
        weatherCity.setTranslateX(weatherCityX);
        weatherCity.setTranslateY(weatherCityY);
        weatherCity.setFill(Color.WHITE);
        pane.getChildren().addAll(weatherWidget, weatherCity);

        String temp = "1000";

        try{
            WeatherObject currentweather = new WeatherObject(city);
            temp = currentweather.getTemp();
        } catch (IOException e){
            e.printStackTrace();
        }
        String temperature = "";
        if(!WeatherObject.isNumeric(temp)){
            weatherDegree = new Text("Error");
        }else{
            double d = Double.parseDouble(temp);
            double rounded = Math.round(d);
            int i = (int) rounded;
            temperature = Integer.toString(i);
            weatherDegree = new Text(temperature + "'C");

        }
        weatherDegree.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, weatherDegreeFontSize));
        weatherDegree.setTranslateX(weatherDegreeX);
        weatherDegree.setTranslateY(weatherDegreeY);
        weatherDegree.setFill(Color.WHITE);
        pane.getChildren().add(weatherDegree);

        GPS = new Image(new FileInputStream("src/assets/GPSpointer.png"));
        GPSView = new ImageView(GPS);
        gpsViewerX = weatherWidgetX+weatherWidgetWidth-30;
        GPSView.setTranslateX(gpsViewerX);
        GPSView.setTranslateY(gpsViewerY);
        GPSView.setFitWidth(gpsViewerWidth);
        GPSView.setFitHeight(gpsViewerHeight);
        pane.getChildren().add(GPSView);

        chatWindow = new Rectangle(chatWindowWidth, chatWindowHeight);
        chatWindow.setTranslateX(chatWindowX);
        chatWindow.setTranslateY(chatWindowY);
        chatWindow.setFill(Color.rgb(160, 160, 160, 0.75));
        chatWindow.setStroke(Color.WHITE);
        chatWindow.setStrokeWidth(1);
        pane.getChildren().add(chatWindow);

        chatInputWindow = new Rectangle(chatInputWindowWidth, chatInputWindowHeight);
        chatInputWindow.setTranslateX(chatInputWindowX);
        chatInputWindow.setTranslateY(chatInputWindowY);
        chatInputWindow.setFill(Color.rgb(200, 200, 200, 0.8));
        chatInputWindow.setStroke(Color.WHITE);
        chatInputWindow.setStrokeWidth(1);
        pane.getChildren().add(chatInputWindow);

        textField = new TextField();
        textField.setPromptText("Input...");
        textField.setFocusTraversable(false);
        textField.setTranslateX(textFieldX);
        textField.setTranslateY(textFieldY);
        textField.setPrefSize(textFieldWidth, textFieldHeight);
        textField.setFont(Font.font("Calibri", FontPosture.REGULAR, 16));
        pane.getChildren().add(textField);

        chatLayout = new Group();

        scrollPane = new ScrollPane();
        scrollPane.setContent(chatLayout);
        scrollPane.setTranslateX(scrollPaneX);
        scrollPane.setTranslateY(scrollPaneY);
        scrollPane.setPrefSize(scrollPaneWidth, scrollPaneHeight);
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
        robotViewer.setTranslateX(robotViewerX);
        robotViewer.setTranslateY(robotViewerY);
        robotViewer.setFitWidth(robotViewerWidth);
        robotViewer.setFitHeight(robotViewerHeight);
        pane.getChildren().add(robotViewer);

        robotInteraction = new Image(new FileInputStream("src/assets/speechBubbleBot.png"));
        robotInteractionViewer = new ImageView(robotInteraction);
        robotInteractionViewer.setTranslateX(robotInteractionViewerX);
        robotInteractionViewer.setTranslateY(robotInteractionViewerY);
        robotInteractionViewer.setFitWidth(robotInteractionViewerWidth);
        robotInteractionViewer.setFitHeight(robotInteractionViewerHeight);
        pane.getChildren().add(robotInteractionViewer);

        robotInteractionText = new Text("Hi DKE student,\nhow can I help?");
        robotInteractionText.setFont(Font.font("Gadugi", FontWeight.BOLD, FontPosture.REGULAR, robotInteractionTextFontSize));
        robotInteractionText.setTranslateX(robotInteractionTextX);
        robotInteractionText.setTranslateY(robotInteractionTextY);
        robotInteractionText.setFill(Color.BLACK);
        pane.getChildren().add(robotInteractionText);

        currentDate = new Date();

        time = new SimpleDateFormat("hh:mm:ss");
        timeText = new Text(time.format(currentDate));
        timeText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, timeTextFontSize));
        timeText.setTranslateX(timeTextX);
        timeText.setTranslateY(timeTextY);
        timeText.setFill(Color.WHITE);
        pane.getChildren().add(timeText);

        date = new SimpleDateFormat("dd/MM/yyyy");
        dateText = new Text(date.format(currentDate));
        dateText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, dateTextFontSize));
        dateText.setTranslateX(dateTextX);
        dateText.setTranslateY(dateTextY);
        dateText.setFill(Color.WHITE);
        pane.getChildren().add(dateText);

        timezones = new Rectangle(timezonesWidth, timezonesHeight);
        timezones.setTranslateX(timezonesX);
        timezones.setTranslateY(timezonesY);
        timezones.setArcWidth(20);
        timezones.setArcHeight(20);
        timezones.setStroke(Color.WHITESMOKE);
        timezones.setStrokeWidth(0.6);
        Stop[] stops1 = new Stop[] { new Stop(0, Color.TRANSPARENT), new Stop(1, Color.BLACK)};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1.9, true, CycleMethod.NO_CYCLE, stops1);
        timezones.setFill(lg1);
        pane.getChildren().add(timezones);

        time1 = CurrentTime.getTime("Europe", "Istanbul");
        timeText1 = new Text(time1);
        timeText1.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, timeText1FontSize));
        timeText1.setTranslateX(timeText1X);
        timeText1.setTranslateY(timeText1Y);
        timeText1.setFill(Color.WHITE);
        time1city = new Text("Istanbul");
        time1city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, time1cityFontSize));
        time1city.setTranslateX(time1cityX);
        time1city.setTranslateY(time1cityY);
        time1city.setFill(Color.WHITE);
        pane.getChildren().add(time1city);
        pane.getChildren().add(timeText1);

        time2 = CurrentTime.getTime("Asia", "Tokyo");
        timeText2 = new Text(time2);
        timeText2.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, timeText2FontSize));
        timeText2.setTranslateX(timeText2X);
        timeText2.setTranslateY(timeText2Y);
        timeText2.setFill(Color.WHITE);
        time2city = new Text("Tokyo");
        time2city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, time2cityFontSize));
        time2city.setTranslateX(time2cityX);
        time2city.setTranslateY(time2cityY);
        time2city.setFill(Color.WHITE);
        pane.getChildren().add(time2city);
        pane.getChildren().add(timeText2);

        time3 = CurrentTime.getTime("Australia", "Sydney");
        timeText3 = new Text(time3);
        timeText3.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, timeText3FontSize));
        timeText3.setTranslateX(timeText3X);
        timeText3.setTranslateY(timeText3Y);
        timeText3.setFill(Color.WHITE);
        time3city = new Text("Sydney");
        time3city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, time3cityFontSize));
        time3city.setTranslateX(time3cityX);
        time3city.setTranslateY(time3cityY);
        time3city.setFill(Color.WHITE);
        pane.getChildren().add(timeText3);
        pane.getChildren().add(time3city);

        time4 = CurrentTime.getTime("Africa", "Khartoum");
        timeText4 = new Text(time4);
        timeText4.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, timeText4FontSize));
        timeText4.setTranslateX(timeText4X);
        timeText4.setTranslateY(timeText4Y);
        timeText4.setFill(Color.WHITE);
        time4city = new Text("Khartoum");
        time4city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, time4cityFontSize));
        time4city.setTranslateX(time4cityX);
        time4city.setTranslateY(time4cityY);
        time4city.setFill(Color.WHITE);
        pane.getChildren().add(time4city);
        pane.getChildren().add(timeText4);

        time5 = CurrentTime.getTime("America", "Jamaica");
        timeText5 = new Text(time5);
        timeText5.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, timeText5FontSize));
        timeText5.setTranslateX(timeText5X);
        timeText5.setTranslateY(timeText5Y);
        timeText5.setFill(Color.WHITE);
        time5city = new Text("Jamaica");
        time5city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, time5cityFontSize));
        time5city.setTranslateX(time5cityX);
        time5city.setTranslateY(time5cityY);
        time5city.setFill(Color.WHITE);
        pane.getChildren().add(time5city);
        pane.getChildren().add(timeText5);

        dropFile = new Rectangle(dropFileWidth, dropFileHeight);
        dropFileY+=quoteRectangleHeightIndicator*18;
        dropFile.setTranslateX(dropFileX);
        dropFile.setTranslateY(dropFileY);
        dropFile.setArcWidth(20);
        dropFile.setArcHeight(20);
        dropFile.setStroke(Color.WHITESMOKE);
        dropFile.setStrokeWidth(0.6);
        dropFile.setFill(lg1);
        pane.getChildren().add(dropFile);

        target = new Text("DROP FILE HERE");
        targetY+=quoteRectangleHeightIndicator*18;
        target.setTranslateX(targetX);
        target.setTranslateY(targetY);
        target.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, targetFontSize));
        target.setFill(Color.WHITE);
        pane.getChildren().add(target);

        quoteRectangleHeight+=quoteRectangleHeightIndicator*18;
        quoteOutline = new Rectangle(quoteRectangleWidth, quoteRectangleHeight);
        quoteOutline.setTranslateX(quoteRectangleX);
        quoteOutline.setTranslateY(quoteRectangleY);
        quoteOutline.setArcWidth(20);
        quoteOutline.setArcHeight(20);
        quoteOutline.setStroke(Color.WHITESMOKE);
        quoteOutline.setStrokeWidth(0.6);
        quoteOutline.setFill(lg1);
        pane.getChildren().add(quoteOutline);

        quoteHeading = new Text("Quote of the day");
        quoteHeading.setFont(Font.font("Calibri", FontWeight.BOLD,  FontPosture.REGULAR, quoteOutlineFontSize));
        quoteHeading.setTranslateX(quoteX);
        quoteHeading.setTranslateY(quoteY - 25);
        quoteHeading.setFill(Color.WHITE);
        pane.getChildren().add(quoteHeading);

        quote = new Text(quoteText);
        quote.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, quoteFontSize));
        quote.setTranslateX(quoteX);
        quote.setTranslateY(quoteY);
        quote.setFill(Color.WHITE);
        pane.getChildren().add(quote);

        scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);

        this.messageHistory = new ArrayList<>();

        Controller controller = new Controller(this);
        controller.setChatController();
        controller.setBackgroundController();

        primaryStage.setOnCloseRequest(t -> {
            exitProgram();
        });

        Thread mainThread = new Thread(() -> {
            while (true) {
                updateTime();
            }
        });
        mainThread.setDaemon(false);
        mainThread.start();

        // Here we start the tick() process - think of it as the main
        // loop.
        // NOTE: It is asynchronous but sill runs in the main thread.
        // If this function blocks or delays, then the entire GuI will be delayed.
        // IMPORTANT: This code repeats on every frame/tick
        AnimationTimer tickTimer = new AnimationTimer(){

            @Override
            public void handle(long now) {
                try {
                    tick();
                } catch (InterruptedException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        tickTimer.start();


        primaryStage.setResizable(true);
        primaryStage.setTitle("Digital Assistant");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Display the request and the answer in the chat
     * @param text query
     */
    public void sendUserText(String text) {

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

        chatLayout.getChildren().addAll(messageTime, userText);
    }

    /**
     *
     * Update the seconds for the time display
     */
    public void updateTime() {

        wait(1000);
        currentDate = new Date();
        timeText.setText(time.format(currentDate));

        time1 = CurrentTime.getTime("Europe", "Istanbul");
        timeText1.setText(time1);
        time2 = CurrentTime.getTime("Asia", "Tokyo");
        timeText2.setText(time2);
        time3 = CurrentTime.getTime("Australia", "Sydney");
        timeText3.setText(time3);
        time4 = CurrentTime.getTime("Africa", "Khartoum");
        timeText4.setText(time4);
        time5 = CurrentTime.getTime("America", "Jamaica");
        timeText5.setText(time5);
    }

    /**
     * Method the makes the program wait "time" seconds
     * @param time to wait
     */
    public void wait(int time) {

        try {
            Thread.sleep(time);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Pushes a message into the waiting queue.
     * If another thread is currently pushing a message, this thread
     * will wait and then push.
     * @param newOutput output to be displayed
     * @throws InterruptedException
     */
    public void pushMessageOrWait(final ConsoleOutput newOutput) throws InterruptedException {
        consoleOutput.put(newOutput);
    }

    /**
     * Reads from the waiting queue and handles the output accordingly.
     * @throws InterruptedException
     */
    public void moveFromQueueToConsole() throws InterruptedException, FileNotFoundException {
        ConsoleOutput output = consoleOutput.poll(0, TimeUnit.MILLISECONDS); // Get the message or null

        if(output != null){ // If there is a message

            if(output.getMessageType().equals(MessageType.STRING)){ // If the message is a string
                String prefix = output.isBot()? "[DKE Assistant]: ":"[User]: ";

                outputMessageHeight += 20;
                Date currentDate = new Date();
                Text msgTime = new Text(time.format(currentDate));
                msgTime.setFont(Font.font("Gadugi", FontWeight.BOLD,  FontPosture.REGULAR, 12));
                msgTime.setTranslateY(outputMessageHeight);
                msgTime.setFill(Color.BLACK);

                Text msgText = new Text(prefix + output.getContent());
                msgText.setFont(Font.font("Gadugi", FontWeight.BOLD,  FontPosture.REGULAR, 16));
                msgText.setTranslateX(50);
                msgText.setTranslateY(msgTime.getTranslateY());
                msgText.setFill(Color.WHITE);
                if (prefix.equals("[User]: ")) {
                    messageHistory.add(output.getContent());
                }

                chatLayout.getChildren().addAll(msgTime, msgText);
            }

            else if(output.getMessageType().equals(MessageType.IMAGE)){ // If the message is an image
                Image imageFile = new Image(new FileInputStream(output.getContent()));
                ImageView outputImage = new ImageView(imageFile);
                outputImage.setScaleX(0.3);
                outputImage.setScaleY(0.3);
                outputImage.setScaleZ(0.3);
                outputImage.setTranslateX(-172);
                outputImage.setPreserveRatio(true);
                outputImage.setTranslateY(outputMessageHeight - 160);
                outputImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    File file = new File(output.getContent());
                    Desktop dt = Desktop.getDesktop();
                    try {
                        dt.open(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    event.consume();
                });

                Date currentDate = new Date();
                Text msgTime = new Text(time.format(currentDate));
                msgTime.setFont(Font.font("Gadugi", FontWeight.BOLD,  FontPosture.REGULAR, 12));
                msgTime.setTranslateY(outputMessageHeight + 20);
                msgTime.setFill(Color.BLACK);

                chatLayout.getChildren().addAll(msgTime, outputImage);
                outputMessageHeight += outputImage.getImage().getHeight()*outputImage.getScaleY() + 5;
            }

            else if (output.getMessageType().equals(MessageType.EXIT)) {
                exitProgram();
            }
        }

    }

    /**
     * Receives messages piling up in the assistant's output queue and transfers
     * them to the output queue in the main thread.
     * @throws InterruptedException
     */
    public void moveFromAssistantToQueue() throws InterruptedException {
        //System.out.println("Moving message from assistant to queue");
        Optional<AssistantMessage> container = assistant.getOutputOrContinue(); // Get message or empty optional

        if(container.isPresent()){ // If there is a message
            AssistantMessage msg = container.get();
            pushMessageOrWait(new ConsoleOutput(msg.getMessage(), false, msg.getMessageType()));
        }

    }

    public void tick() throws InterruptedException, FileNotFoundException {
        moveFromAssistantToQueue(); // Get assistant messages from the assistant queue to the queue in the main thread
        moveFromQueueToConsole(); // Move stacking messages from the queue in the main thread to the output console
        // NOTE: This code will breaks if
        //      a) A skill spams with messages every cycle
        //      b) The user spams with messages every frame
        // Since both cases are expected to be avoided (if we code carefully), I do not see a reason to
        // work around them. In other words, they are very very unlikely to happen, so let them be.
    }

    public void createThread() {

        queryThread = new Thread(() -> {
            try {
                boolean image = false;
                botText = new Text();
                Text text = new Text();
                AssistantMessage hi = assistant.getOutputOrWait();
                if(hi.getMessage().split("-")[0].equals("image"))
                {
                    image = true;
                    System.out.println(hi.getMessage().split("-")[1]);
                    FileInputStream input = new FileInputStream("src/assets/PhotoTaken/" + hi.getMessage().split("-")[1]);
                    Image imageFile = new Image(input);
                    webcam = new ImageView(imageFile);
                    Platform.runLater(() -> {

                        text.setText("Bot: Smile!");
                        text.setFont(Font.font("Gadugi", FontWeight.BOLD, FontPosture.REGULAR, 16));
                        text.setFill(Color.WHITE);
                        text.setTranslateX(50);
                        text.setTranslateY(userText.getTranslateY() + 20);

                        chatLayout.getChildren().add(text);

                        webcam.setScaleX(0.3);
                        webcam.setScaleY(0.3);
                        webcam.setScaleZ(0.3);
                        webcam.setTranslateX(-172);
                        webcam.setPreserveRatio(true);

                        webcam.setTranslateY(text.getTranslateY() - 160);


                        webcam.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent event) {
                                File file = new File("src/assets/PhotoTaken/" + hi.getMessage().split("-")[1]);
                                Desktop dt = Desktop.getDesktop();
                                try {
                                    dt.open(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                event.consume();
                            }
                        });

                        robotInteractionText.setText("Damn\nYou are hot!");
                        chatLayout.getChildren().add(webcam);

                        botText.setText("Click on the image to have full resolution!");
                        botText.setFont(Font.font("Gadugi", FontWeight.BOLD, FontPosture.REGULAR, 16));
                        botText.setFill(Color.WHITE);
                        botText.setTranslateX(50);
                        botText.setTranslateY(text.getTranslateY() + 170);
                        requestCounter += 3;
                        chatLayout.getChildren().add(botText);

                    });
                }
                else if (hi.getMessage().length() > 0) {
                    botText.setText("Bot: " + hi.getMessage());
                }
                else {
                    botText.setText("Bot: query not understood");
                }
                if(!image) {
                    Platform.runLater(() -> {
                        botText.setFont(Font.font("Gadugi", FontWeight.BOLD, FontPosture.REGULAR, 16));
                        botText.setFill(Color.WHITE);
                        botText.setTranslateX(50);
                        botText.setTranslateY(userText.getTranslateY() + 20);
                        robotInteractionText.setText("Anything else?\nI'm free");
                        chatLayout.getChildren().add(botText);
                    });
                }
                isAgentFree = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        queryThread.setDaemon(false);
        queryThread.start();
    }

    /**
     * Method used to find the best size of the weather widget
     * @param city where to find weather
     * @return size in pixels
     */
    public double findBestWeatherWidgetWidth(String city) {

        return (Math.sqrt(city.length())*50) - 5*(countCharsUsingRegex(city, 'i')+countCharsUsingRegex(city, 'I')
                +countCharsUsingRegex(city, 'j')+countCharsUsingRegex(city, 'J')
                +countCharsUsingRegex(city, 'l')+countCharsUsingRegex(city, 'L')
                +countCharsUsingRegex(city, 'f')+countCharsUsingRegex(city, 'F')
                +countCharsUsingRegex(city, 'r')+countCharsUsingRegex(city, 'R')
                +countCharsUsingRegex(city, 't')+countCharsUsingRegex(city, 'T'));
    }

    /**
     * Method used to cut the quote text in different lines based on the length
     * @param text current quote text
     */
    public String processText(String text) {

        int cutSize = 55;
        int count = 1;
        quoteRectangleHeightIndicator = (int) Math.floor((double) text.length()/cutSize);
        String newLine = "\n";
        String newString = "";
        if (quoteRectangleHeightIndicator > 0) {
            for (int i = 0; i < text.length(); i++) {
                newString += text.charAt(i);
                if (i > (cutSize*count) && (text.charAt(i)==' ')) {
                    newString+=newLine;
                    count++;
                }
            }
        }
        else {
            newString = text;
        }
        return newString;
    }

    /**
     * Method that computes the occurrence of a character in a string
     * @param str to look in
     * @param ch to find in string
     * @return the count occurrences
     */
    private int countCharsUsingRegex(String str, char ch) {

        Pattern pattern = Pattern.compile("[^" + ch + "]*" + ch + "");
        Matcher matcher = pattern.matcher(str);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Method that interrupts the threads and then exit the program
     */
    public void exitProgram() {

        assistant.interruptAndWait();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}