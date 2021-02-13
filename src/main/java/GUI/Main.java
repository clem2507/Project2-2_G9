package GUI;

import backend.Assistant;
import backend.AssistantMessage;
import backend.MessageType;
import domains.Location.CurrentLocation;
import domains.Time.FindTime;
import domains.Weather.CurrentWeather;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    final double WINDOW_WIDTH = screenSize.getWidth();
    final double WINDOW_HEIGHT = screenSize.getHeight();
    int requestCounter = 0;

    boolean flag = true;
    boolean isAgentFree = true;

    Group pane = new Group();
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

    Rectangle chatWindow;
    Rectangle chatInputWindow;
    Rectangle weatherWidget;

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
    Text userText;
    Text botText;
    Text robotInteractionText;
    Text messageTime;
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
    ImageView webcam;

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

    Assistant assistant = new Assistant();
    BlockingQueue<ConsoleOutput> consoleOutput = new LinkedBlockingQueue<>();

    Thread queryThread;

    @Override
    public void start(Stage primaryStage) throws IOException {

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
        editBgButton.setTranslateX(WINDOW_WIDTH-170);
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
        chatWindow.setTranslateX(490);
        chatWindow.setTranslateY(80);
        chatWindow.setFill(Color.rgb(160, 160, 160, 0.75));
        chatWindow.setStroke(Color.WHITE);
        chatWindow.setStrokeWidth(1);
        pane.getChildren().add(chatWindow);

        chatInputWindow = new Rectangle(500, 100);
        chatInputWindow.setTranslateX(490);
        chatInputWindow.setTranslateY(580);
        chatInputWindow.setFill(Color.rgb(200, 200, 200, 0.8));
        chatInputWindow.setStroke(Color.WHITE);
        chatInputWindow.setStrokeWidth(1);
        pane.getChildren().add(chatInputWindow);

        textField = new TextField();
        textField.setPromptText("Input...");
        textField.setFocusTraversable(false);
        textField.setTranslateX(540);
        textField.setTranslateY(610);
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
        robotViewer.setTranslateX(1100);
        robotViewer.setTranslateY(470);
        robotViewer.setFitWidth(200);
        robotViewer.setFitHeight(220);
        pane.getChildren().add(robotViewer);

        robotInteraction = new Image(new FileInputStream("src/assets/speechBubbleBot.png"));
        robotInteractionViewer = new ImageView(robotInteraction);
        robotInteractionViewer.setTranslateX(1000);
        robotInteractionViewer.setTranslateY(420);
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

        Rectangle timezones = new Rectangle(420, 65);
        timezones.setTranslateX(30);
        timezones.setTranslateY(240);
        timezones.setArcWidth(20);
        timezones.setArcHeight(20);
        timezones.setStroke(Color.WHITESMOKE);
        timezones.setStrokeWidth(0.6);
        Stop[] stops1 = new Stop[] { new Stop(0, Color.TRANSPARENT), new Stop(1, Color.BLACK)};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1.9, true, CycleMethod.NO_CYCLE, stops1);
        timezones.setFill(lg1);
        pane.getChildren().add(timezones);

        time1 = FindTime.getTime("Europe", "Istanbul");
        timeText1 = new Text(time1);
        timeText1.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 17));
        timeText1.setTranslateX(timezones.getTranslateX() + 15);
        timeText1.setTranslateY(timezones.getTranslateY() + 30);
        timeText1.setFill(Color.WHITE);
        Text time1city = new Text("Istanbul");
        time1city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 16));
        time1city.setTranslateX(timezones.getTranslateX() + 18);
        time1city.setTranslateY(timezones.getTranslateY() + 50);
        time1city.setFill(Color.WHITE);
        pane.getChildren().add(time1city);
        pane.getChildren().add(timeText1);

        time2 = FindTime.getTime("Asia", "Tokyo");
        timeText2 = new Text(time2);
        timeText2.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 17));
        timeText2.setTranslateX(timezones.getTranslateX() + 95);
        timeText2.setTranslateY(timezones.getTranslateY() + 30);
        timeText2.setFill(Color.WHITE);
        Text time2city = new Text("Tokyo");
        time2city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 16));
        time2city.setTranslateX(timezones.getTranslateX() + 100);
        time2city.setTranslateY(timezones.getTranslateY() + 50);
        time2city.setFill(Color.WHITE);
        pane.getChildren().add(time2city);
        pane.getChildren().add(timeText2);

        time3 = FindTime.getTime("Australia", "Sydney");
        timeText3 = new Text(time3);
        timeText3.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 17));
        timeText3.setTranslateX(timezones.getTranslateX() + 175);
        timeText3.setTranslateY(timezones.getTranslateY() + 30);
        timeText3.setFill(Color.WHITE);
        Text time3city = new Text("Sydney");
        time3city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 16));
        time3city.setTranslateX(timezones.getTranslateX() + 180);
        time3city.setTranslateY(timezones.getTranslateY() + 50);
        time3city.setFill(Color.WHITE);
        pane.getChildren().add(time3city);
        pane.getChildren().add(timeText3);

        time4 = FindTime.getTime("Africa", "Khartoum");
        timeText4 = new Text(time4);
        timeText4.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 17));
        timeText4.setTranslateX(timezones.getTranslateX() + 260);
        timeText4.setTranslateY(timezones.getTranslateY() + 30);
        timeText4.setFill(Color.WHITE);
        Text time4city = new Text("Khartoum");
        time4city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 16));
        time4city.setTranslateX(timezones.getTranslateX() + 260);
        time4city.setTranslateY(timezones.getTranslateY() + 50);
        time4city.setFill(Color.WHITE);
        pane.getChildren().add(time4city);
        pane.getChildren().add(timeText4);

        time5 = FindTime.getTime("America", "Jamaica");
        timeText5 = new Text(time5);
        timeText5.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 17));
        timeText5.setTranslateX(timezones.getTranslateX() + 345);
        timeText5.setTranslateY(timezones.getTranslateY() + 30);
        timeText5.setFill(Color.WHITE);
        Text time5city = new Text("Jamaica");
        time5city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, 16));
        time5city.setTranslateX(timezones.getTranslateX() + 349);
        time5city.setTranslateY(timezones.getTranslateY() + 50);
        time5city.setFill(Color.WHITE);
        pane.getChildren().add(time5city);
        pane.getChildren().add(timeText5);

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

        primaryStage.setResizable(false);
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

        time1 = FindTime.getTime("Europe", "Istanbul");
        timeText1.setText(time1);
        time2 = FindTime.getTime("Asia", "Tokyo");
        timeText2.setText(time2);
        time3 = FindTime.getTime("Australia", "Sydney");
        timeText3.setText(time3);
        time4 = FindTime.getTime("Africa", "Khartoum");
        timeText4.setText(time4);
        time5 = FindTime.getTime("America", "Jamaica");
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
        //System.out.println("Pushing message " + newOutput.getContent());
        consoleOutput.put(newOutput);
        //System.out.println("Pushed message " + newOutput.getContent());
    }

    /**
     * Reads from the waiting queue and handles the output accordingly.
     * @throws InterruptedException
     */
    public void moveFromQueueToConsole() throws InterruptedException, FileNotFoundException {
        //System.out.println("Moving message from queue to console");
        ConsoleOutput output = consoleOutput.poll(0, TimeUnit.MILLISECONDS); // Get the message or null

        if(output != null){ // If there is a message

            if(output.getMessageType().equals(MessageType.STRING)){ // If the message is a string
                String prefix = output.isBot()? "[DKE Assistant]: ":"[User]: ";

                requestCounter++;
                currentDate = new Date();
                messageTime = new Text(time.format(currentDate));
                messageTime.setFont(Font.font("Gadugi", FontWeight.BOLD,  FontPosture.REGULAR, 12));
                messageTime.setTranslateY(20*requestCounter);
                messageTime.setFill(Color.BLACK);

                userText = new Text(prefix + output.getContent());
                userText.setFont(Font.font("Gadugi", FontWeight.BOLD,  FontPosture.REGULAR, 16));
                userText.setTranslateX(50);
                userText.setTranslateY(messageTime.getTranslateY()+2);
                userText.setFill(Color.WHITE);

                chatLayout.getChildren().addAll(messageTime, userText);
            }

            else if(output.getMessageType().equals(MessageType.IMAGE)){ // If the message is an image
                botText = new Text();
                Text text = new Text();
                FileInputStream input = new FileInputStream("src/assets/ProjectData/PhotoTaken/" + output.getContent());
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
                            File file = new File("src/assets/ProjectData/PhotoTaken/" + output.getContent());
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
                    requestCounter += 10;
                    chatLayout.getChildren().add(botText);

                });
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

    public static void main(String[] args) {
        launch(args);
    }
}