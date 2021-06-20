package GUI;

import backend.*;
import backend.common.OS.UnsupportedOSException;
import backend.common.Quote;
import backend.common.WeatherObject;
import backend.common.camera.Camera;
import backend.common.face_detection_api.DetectionHandler;
import backend.common.face_detection_api.DetectionResults;
import domains.Search.Search;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class Main extends Application {
    DetectionHandler detectionHandler;
    private boolean isHidden = false;
    private int lastSwitchState = 0;

    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    final double WINDOW_WIDTH = screenSize.getWidth() - 5;
    final double WINDOW_HEIGHT = screenSize.getHeight() - 70;

    boolean flag = true;

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
    Button emptyTemplate;

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
    Text robotInteractionText;
    Text weatherCity;
    Text weatherDegree;
    Text target;
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
    Image detectorImage;

    ImageView iv;
    ImageView detectorView;
    ImageView imgView;
    ImageView imgView1;
    ImageView imgView2;
    ImageView imgView3;
    ImageView imgView4;
    ImageView imgView5;
    ImageView robotViewer;
    ImageView robotInteractionViewer;
    ImageView GPSView;

    int editBgButtonWidth = 130;
    int editBgButtonHeight = 20;
    int editBgButtonX = (int) (WINDOW_WIDTH-170);
    int editBgButtonY = 40;

    int imagesScrollPaneWidth = 228;
    int imagesScrollPaneHeight = 310;
    int imagesScrollPaneX = editBgButtonX-55;
    int imagesScrollPaneY = editBgButtonY+40;

    int chatWindowWidth = 600;
    int chatWindowHeight = 550;
    int chatWindowX = (int) ((WINDOW_WIDTH/2)-(chatWindowWidth/2))+100;
    int chatWindowY = 90;

    int chatInputWindowWidth = chatWindowWidth;
    int chatInputWindowHeight = 100;
    int chatInputWindowX = chatWindowX;
    int chatInputWindowY = chatWindowHeight+chatWindowY;

    int textFieldWidth = 500;
    int textFieldHeight = 40;
    int textFieldFontSize = 16;
    int textFieldX = chatInputWindowX+45;
    int textFieldY = chatInputWindowY+30;

    int scrollPaneWidth = chatWindowWidth-4;
    int scrollPaneHeight = chatWindowHeight-4;
    int scrollPaneX = chatWindowX+2;
    int scrollPaneY = chatWindowY+2;

    int robotViewerWidth = 170;
    int robotViewerHeight = 200;
    int robotViewerX = chatInputWindowX+730;
    int robotViewerY = chatInputWindowY-90;

    int robotInteractionViewerWidth = 130;
    int robotInteractionViewerHeight = 85;
    int robotInteractionViewerX = robotViewerX-105;
    int robotInteractionViewerY = robotViewerY-55;

    int robotInteractionTextFontSize = 12;
    int robotInteractionTextX = robotInteractionViewerX+20;
    int robotInteractionTextY = robotInteractionViewerY+27;

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
    int dropFileY = 425;

    int targetFontSize = 15;
    int targetX = dropFileX+18;
    int targetY = dropFileY+23;

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

    int emptyTemplateWidth = 160;
    int emptyTemplateHeight = dropFileHeight;
    int emptyTemplateX = dropFileX + 170;
    int emptyTemplateY = dropFileY;

    int menubarX = dropFileX + 2;
    int menubarY = dropFileY + 55;

    int detectorViewX = menubarX + 120;
    int detectorViewY = menubarY + 15;

    int detectorTextX = detectorViewX;
    int detectorTextY = menubarY + 11;

    int editBgButtonFontSize = 14;
    int emptyTemplateFontSize = 15;

    boolean isFaceRecognitionDone = true;
    boolean isFaceRecognitionPushed = false;
    Thread faceRecognitionThread;

    Date currentDate;

    SimpleDateFormat time;
    SimpleDateFormat date;

    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    Assistant assistant = new Assistant();
    BlockingQueue<ConsoleOutput> consoleOutput = new LinkedBlockingQueue<>();
    private int outputMessageHeight = 0;

    Menu menu;
    Menu interpretersMenu;
    Menu faceDetectorMenu;
    RadioMenuItem faceDetectionMenuItem1;
    RadioMenuItem faceDetectionMenuItem2;
    RadioMenuItem faceRecognitionMenuItem;
    MenuBar menuBar;
    CheckBox checkBox1;

    Stage primStage;

    @Override
    public void start(Stage primaryStage) throws IOException {

        primStage = primaryStage;
        String quoteText = Quote.getQuote();
        quoteText = processText(quoteText);

        this.detectorViewY +=quoteRectangleHeightIndicator*18;
        this.detectorTextY+=quoteRectangleHeightIndicator*18;

        //Default Background
        bg = new Image(new FileInputStream("src/assets/cliff-background.jpg"));
        iv = new ImageView(bg);
        iv.setFitHeight(WINDOW_HEIGHT);
        iv.setFitWidth(WINDOW_WIDTH);
        pane.getChildren().add(iv);

        // BACKGROUND OPTION
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

        emptyTemplate = new Button("CLEAR TEMPLATES");
        emptyTemplateY+=quoteRectangleHeightIndicator*18;
        emptyTemplate.setPrefWidth(emptyTemplateWidth);
        emptyTemplate.setPrefHeight(emptyTemplateHeight);
        emptyTemplate.setTranslateX(emptyTemplateX);
        emptyTemplate.setTranslateY(emptyTemplateY);
        emptyTemplate.setStyle("-fx-background-color: #000000; -fx-background-color:rgba(0, 0, 0, 0.2); -fx-background-radius: 15px; -fx-background-insets: 0,1,1;" +
                " -fx-text-fill: white; -fx-font-family: \"Gadugi\"; -fx-font-size: 15px; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ); -fx-border-color: #F5F5F5; -fx-border-radius: 10; -fx-border-width: 0.6 0.6 0.6 0.6; ");
        pane.getChildren().add(emptyTemplate);

        editBgButton = new Button("Edit Background");
        editBgButton.setPrefWidth(editBgButtonWidth);
        editBgButton.setPrefHeight(editBgButtonHeight);
        editBgButton.setTranslateX(editBgButtonX);
        editBgButton.setTranslateY(editBgButtonY);
        editBgButton.setStyle(" -fx-background-color: #000000; -fx-background-color:rgba(0, 0, 0, 0.2); -fx-background-radius: 15px; -fx-background-insets: 0,1,1;" +
                " -fx-text-fill: white; -fx-font-family: \"Gadugi\"; -fx-font-size: 14px; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ); -fx-border-color: #F5F5F5; -fx-border-radius: 10; -fx-border-width: 0.6 0.6 0.6 0.6; ");
        imagesScrollPane.setTranslateX(imagesScrollPaneX);
        imagesScrollPane.setTranslateY(imagesScrollPaneY);
        pane.getChildren().add(editBgButton);

        // CHAT
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
        robotInteractionText.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, robotInteractionTextFontSize));
        robotInteractionText.setTranslateX(robotInteractionTextX);
        robotInteractionText.setTranslateY(robotInteractionTextY);
        robotInteractionText.setFill(Color.BLACK);
        pane.getChildren().add(robotInteractionText);

        // WIDGETS
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

        //TODO: Comment this out when submitting
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
            WeatherObject currentWeather = new WeatherObject(city);
            temp = currentWeather.getTemp();
        } catch (IOException e){
            e.printStackTrace();
        }
        String temperature;
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

        currentDate = new Date();
        time = new SimpleDateFormat("HH:mm:ss");
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

        time1 = "";
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

        time2 = "";
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

        time3 = "";
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

        time4 = "";
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

        time5 = "";
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

        menu = new Menu("MENU");
        menu.setStyle("-fx-font-family: \"Gadugi\"; -fx-font-size: 15px;");
        ToggleGroup toggleGroup = new ToggleGroup();

        interpretersMenu = new Menu("Interpreters");
        interpretersMenu.setStyle("-fx-text-fill: black;");

        List<String> interpreterNames = assistant.listInterpreterNames();

        for(int i = 0; i <  interpreterNames.size(); i++) {

            /*String tempName = interpreterNames.get(i);
            String name = tempName.toLowerCase();*/

            String name = interpreterNames.get(i);

            //IMPORTANT: If you want to show these names in lower case, you can't just make them lower case like that.
            // You can create a dictionary and store the original name in there while adding the lower case name
            // in the drop-down menu. Then, whenever the user clicks on the drop-down menu, you read the lower
            // case name and lookup the original name in the dictionary and THEN select the interpreter by name.

            RadioMenuItem choiceItem = new RadioMenuItem(name);
            toggleGroup.getToggles().add(choiceItem);
            interpretersMenu.getItems().add(choiceItem);

            choiceItem.setOnAction(event -> {
                System.out.println("Selected this: " + name);
                assistant.selectInterpreter(name);
            });
            choiceItem.setStyle("-fx-text-fill: black;");

            if(i==0){
                choiceItem.setSelected(true);
            }
        }

        faceDetectorMenu = new Menu("Face Detection & Recognition");
        faceDetectorMenu.setStyle("-fx-text-fill: black;");

        ToggleGroup toggleGroup1 = new ToggleGroup();

        faceDetectionMenuItem1 = new RadioMenuItem("HOG: Detection");
        faceDetectionMenuItem1.setStyle("-fx-text-fill: black;");
        faceDetectionMenuItem2 = new RadioMenuItem("Haar Cascade: Detection");
        faceDetectionMenuItem2.setStyle("-fx-text-fill: black;");
        faceRecognitionMenuItem = new RadioMenuItem("PCA & MLP: Recognition");
        faceRecognitionMenuItem.setStyle("-fx-text-fill: black;");

        faceDetectionMenuItem1.setSelected(true);

        toggleGroup1.getToggles().addAll(faceDetectionMenuItem1, faceDetectionMenuItem2, faceRecognitionMenuItem);
        faceDetectorMenu.getItems().addAll(faceDetectionMenuItem1, faceDetectionMenuItem2, faceRecognitionMenuItem);

        menu.getItems().addAll(interpretersMenu, faceDetectorMenu);

        menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        menubarY+=quoteRectangleHeightIndicator*18;
        menuBar.setTranslateX(menubarX);
        menuBar.setTranslateY(menubarY);
        menuBar.setStyle(" -fx-background-color: #000000; -fx-background-color:rgba(0, 0, 0, 0.2); -fx-background-radius: 15px; -fx-background-insets: 0,1,1;" +
                " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ); -fx-border-color: #F5F5F5; -fx-border-radius: 10; -fx-border-width: 0.6 0.6 0.6 0.6; -fx-selection-bar: #636b69;");
        pane.getChildren().add(menuBar);

        checkBox1 = new CheckBox("Greet When Sees Face  ");
        checkBox1.setStyle(" -fx-background-color: #000000; -fx-background-color:rgba(0, 0, 0, 0.2); -fx-background-radius: 15px; -fx-background-insets: 0,1,1;" +
                " -fx-text-fill: white; -fx-font-family: \"Gadugi\"; -fx-font-size: 14px; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ); -fx-border-color: #F5F5F5; -fx-border-radius: 10; -fx-border-width: 0.6 0.6 0.6 0.6; ");
        HBox hbox = new HBox(checkBox1);
        hbox.setTranslateX(editBgButtonX - 180);
        hbox.setTranslateY(editBgButtonY + 3);
        pane.getChildren().add(hbox);

        scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        this.messageHistory = new ArrayList<>();

        Controller controller = new Controller(this);
        controller.setChatController();
        controller.setBackgroundController();

        primaryStage.setOnCloseRequest(t -> exitProgram());

        // Here we start the tick() process - think of it as the main
        // loop.
        // NOTE: It is asynchronous but sill runs in the main thread.
        // If this function blocks or delays, then the entire GUI will be delayed.
        // IMPORTANT: This code repeats on every frame/tick

        detectionHandler = new DetectionHandler(0, 0);

        if ((faceDetectionMenuItem1.isSelected() || faceDetectionMenuItem2.isSelected()) && !System.getProperty("os.name").equals("Mac OS X")) {
            Camera.openCamera(detectionHandler.getChannel());
        }

        AnimationTimer tickTimer = new AnimationTimer(){

            @Override
            public void handle(long now) {
                try {
                    tick();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        tickTimer.start();

        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        primaryStage.setResizable(true);
        primaryStage.setTitle("Digital Assistant");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
        Platform.setImplicitExit(false);
    }

    public boolean getCheckboxState(){
        return this.checkBox1.isSelected();
    }

    public void dispDetectorImage(Image img){
        detectorImage = img;
        detectorView = new ImageView(detectorImage);
        detectorView.setTranslateX(detectorViewX);
        detectorView.setTranslateY(detectorViewY );
        detectorView.setFitWidth(155);
        detectorView.setFitHeight(155);
        pane.getChildren().add(detectorView);

        Text detectorText = new Text("Face Detection");
        detectorText.setTranslateX(detectorTextX);
        detectorText.setTranslateY(detectorTextY);
        detectorText.setFill(Color.WHITE);
        detectorText.setStyle(" -fx-font: 11 Calibri; ");
        pane.getChildren().add(detectorText);
    }

    public void hideWindow() { primStage.hide(); }

    public void showWindow() { primStage.show(); }

    /**
     * Update the seconds for the time display
     */
    public void updateTime() {

        currentDate = new Date();
        timeText.setText(time.format(currentDate));

        TimeZone timezoneIstanbul = TimeZone.getTimeZone("GMT" + "+3");
        Calendar calendarIstanbul = Calendar.getInstance(timezoneIstanbul);
        dateFormat.setCalendar(calendarIstanbul);
        dateFormat.setTimeZone(timezoneIstanbul);
        time1 = dateFormat.format(calendarIstanbul.getTime());
        timeText1.setText(time1);

        TimeZone timezoneTokyo = TimeZone.getTimeZone("GMT" + "+9");
        Calendar calendarTokyo = Calendar.getInstance(timezoneTokyo);
        dateFormat.setCalendar(calendarTokyo);
        dateFormat.setTimeZone(timezoneTokyo);
        time2 = dateFormat.format(calendarTokyo.getTime());
        timeText2.setText(time2);

        TimeZone timezoneSydney = TimeZone.getTimeZone("GMT" + "+11");
        Calendar calendarSydney = Calendar.getInstance(timezoneSydney);
        dateFormat.setCalendar(calendarSydney);
        dateFormat.setTimeZone(timezoneSydney);
        time3 = dateFormat.format(calendarSydney.getTime());
        timeText3.setText(time3);

        TimeZone timezoneKhartoum = TimeZone.getTimeZone("GMT" + "+2");
        Calendar calendarKhartoum = Calendar.getInstance(timezoneKhartoum);
        dateFormat.setCalendar(calendarKhartoum);
        dateFormat.setTimeZone(timezoneKhartoum);
        time4 = dateFormat.format(calendarKhartoum.getTime());
        timeText4.setText(time4);

        TimeZone timezoneJamaica = TimeZone.getTimeZone("GMT" + "-5");
        Calendar calendarJamaica = Calendar.getInstance(timezoneJamaica);
        dateFormat.setCalendar(calendarJamaica);
        dateFormat.setTimeZone(timezoneJamaica);
        time5 = dateFormat.format(calendarJamaica.getTime());
        timeText5.setText(time5);
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
                if (!output.isBot()) {
                    messageHistory.add(output.getContent());
                }

                chatLayout.getChildren().addAll(msgTime, msgText);
            }

            else if(output.getMessageType().equals(MessageType.IMAGE)){ // If the message is an image
                Image imageFile = new Image(new FileInputStream(output.getContent()));
                ImageView outputImage = new ImageView(imageFile);
                outputImage.setScaleX(0.6);
                outputImage.setScaleY(0.6);
                outputImage.setScaleZ(0.6);
                outputImage.setTranslateX(15);
                outputImage.setPreserveRatio(true);
                outputImage.setTranslateY(outputMessageHeight - 15);
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
                outputMessageHeight += outputImage.getImage().getHeight()*outputImage.getScaleY() + 20;
            }

            else if(output.getMessageType().equals(MessageType.HYPER_LINK)){ // If the message is a string
                outputMessageHeight += 20;
                Date currentDate = new Date();
                Text msgTime = new Text(time.format(currentDate));
                msgTime.setFont(Font.font("Gadugi", FontWeight.BOLD,  FontPosture.REGULAR, 12));
                msgTime.setTranslateY(outputMessageHeight);
                msgTime.setFill(Color.BLACK);

                Hyperlink link = new Hyperlink(output.getContent());
                link.setFont(Font.font("Gadugi", FontWeight.BOLD,  FontPosture.REGULAR, 16));
                link.setTranslateX(50);
                link.setTranslateY(msgTime.getTranslateY() - 20);


                link.setOnAction(t -> {
                    try {
                        Search.open(link.getText());
                    } catch (IOException | UnsupportedOSException e) {
                        e.printStackTrace();
                    }
                });

                chatLayout.getChildren().addAll(msgTime, link);
            }

            else if (output.getMessageType().equals(MessageType.EXIT)) { // If the message ask to exit the program
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
        Optional<AssistantMessage> container = assistant.getOutputOrContinue(); // Get message or empty optional

        if(container.isPresent()){ // If there is a message
            AssistantMessage msg = container.get();
            pushMessageOrWait(new ConsoleOutput(msg.getMessage(), false, msg.getMessageType()));
        }

    }

    public void tick() throws InterruptedException, IOException {
        moveFromAssistantToQueue(); // Get assistant messages from the assistant queue to the queue in the main thread
        moveFromQueueToConsole(); // Move stacking messages from the queue in the main thread to the output console
        // NOTE: This code will breaks if
        //      a) A skill spams with messages every cycle
        //      b) The user spams with messages every frame
        // Since both cases are expected to be avoided (if we code carefully), I do not see a reason to
        // work around them. In other words, they are very very unlikely to happen, so let them be.
        updateTime();

        if (!System.getProperty("os.name").equals("Mac OS X")) {
            if (faceDetectionMenuItem1.isSelected() || faceDetectionMenuItem2.isSelected()) {
                pullAndProcessFaceDetectionResults();
            }
        }
        if (faceRecognitionMenuItem.isSelected()) {
            pullAndProcessFaceRecognitionResults();
        }
    }

    private void pullAndProcessFaceDetectionResults() throws IOException {

        Camera.openCamera(detectionHandler.getChannel());
        if(detectionHandler.isReady()) {
            final Optional<DetectionResults> results = detectionHandler.getResults();

            if(results.isPresent()) {
                final BufferedImage cameraFrame = results.get().getImage();

                if (!results.get().getAABBs().isEmpty()) {

                    for(var aabb : results.get().getAABBs()) {
                        aabb.draw(cameraFrame);
                    }

                    System.out.println("Face Detected!");

                    if(isHidden) {
                        isHidden = false;

                        if(getCheckboxState()) {
                            try {
                                pushMessageOrWait(new ConsoleOutput(
                                        "Hello, wonderful person.",
                                        false,
                                        MessageType.STRING
                                ));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        //showWindow();
                    }

                }

                if (results.get().getAABBs().isEmpty()) {
                    System.out.println("No Face Detected!");

                    if(!isHidden) {
                        isHidden = true;

                        /*if(getCheckboxState()) {
                            try {
                                pushMessageOrWait(new ConsoleOutput(
                                        "See you soon!",
                                        false,
                                        MessageType.STRING
                                ));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }*/

                        //hideWindow();
                    }

                }

               // dispDetectorImage(SwingFXUtils.toFXImage(cameraFrame, null));
            }

            detectionHandler.setDetector(getSwitchState());
            detectionHandler.detect();
        }

    }

    private void pullAndProcessFaceRecognitionResults() {

        if (isFaceRecognitionPushed) {
            faceRecognitionThread = new Thread(() -> {
                isFaceRecognitionPushed = false;
                isFaceRecognitionDone = false;
                PythonInterpreter interpreter = new PythonInterpreter();
                interpreter.execfile("src/main/java/face_recognition/main.py");
                PyObject pyObj = interpreter.get("main");
                PyObject result = pyObj.__call__();
                String name = (String) result.__tojava__(String.class);
                try {
                    pushMessageOrWait(new ConsoleOutput(
                            "Hello, " + name + "! How can I help you?",
                            false,
                            MessageType.STRING
                    ));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isFaceRecognitionDone = true;
            });
            faceRecognitionThread.start();
        }
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

        int cutSize = 50;
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
     * Methode that updates the robot icon text
     * @param text to change
     */
    public void setRobotText(String text) {

        robotInteractionText.setText(text);
    }

    /**
     * Method that interrupts the threads and then exit the program
     */
    public void exitProgram() {
        assistant.interruptAndWait();
        Platform.exit();
    }

    public int getSwitchState() {
        //NOTE: I took the liberty to change the return values 1 and 2 with 0 and 1.
        // This will make my life a lot easier.
        // -Dennis

        if (faceDetectionMenuItem1.isSelected()) {
            return 0;
        }
        else {
            return 1;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}