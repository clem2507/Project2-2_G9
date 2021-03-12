package GUI;

import backend.Assistant;
import backend.MessageType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.io.File;

public class Controller {

    private final Main window;
    private int historyCount = 0;
    Assistant assistant = new Assistant();
    private double heightScale = 1;
    private double widthScale = 1;


    /**
     * Constructor of the main possible control with the graphic interface
     * @param window
     */
    public Controller(Main window) {

        this.window = window;
    }

    /**
     * Method that activates the controls with the chat
     */
    public void setChatController() {

        window.scene.setOnKeyPressed(t -> {
            KeyCode key = t.getCode();
            switch (key) {
                case ESCAPE:
                    window.exitProgram();
                    break;
                case ENTER:
                    if (window.textField.getText().length() > 0) {
                        //window.isAgentFree = false;
                        //window.requestCounter++;
                        //window.sendUserText(window.textField.getText());
                        window.robotInteractionText.setText("...");

                        try {
                            window.pushMessageOrWait(new ConsoleOutput(window.textField.getText(), true, MessageType.STRING));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        window.assistant.processQuery(window.textField.getText());
                        window.textField.setText("");
                        historyCount = window.messageHistory.size()+1;
                        //window.createThread();
                    }
                    break;
            }
        });

        window.textField.setOnKeyPressed(t -> {
            KeyCode key = t.getCode();
            switch (key) {
                case DOWN:
                    System.out.println(window.messageHistory.size()-1);
                    if (historyCount < window.messageHistory.size()-1) {
                        window.textField.setText(window.messageHistory.get(historyCount+1));
                        historyCount++;
                    }
                    else {
                        window.textField.setText("");
                        if (historyCount == window.messageHistory.size()-1) {
                            historyCount++;
                        }
                    }
                    window.textField.positionCaret(window.textField.getText().length());
                    break;
                case UP:
                    if (historyCount > 0) {
                        window.textField.setText(window.messageHistory.get(historyCount-1));
                        historyCount--;
                    }
                    window.textField.positionCaret(window.textField.getText().length());
                    break;
            }
        });

        // automatic scroll down
        window.chatLayout.getChildren().addListener(
                (ListChangeListener.Change<? extends Node> c) -> {
                    window.chatLayout.layout();
                    window.scrollPane.setVvalue(1.0d);
                }
        );
    }

    /**
     * Method that allows the controls with the edit background function
     */
    public void setBackgroundController() {

        window.imgView.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg) {
                window.pane.getChildren().remove(window.imagesScrollPane);
                window.iv.setImage(window.bg);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.imgView1.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg1) {
                window.iv.setImage(window.bg1);
                window.pane.getChildren().remove(window.imagesScrollPane);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.imgView2.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg2) {
                window.iv.setImage(window.bg2);
                window.pane.getChildren().remove(window.imagesScrollPane);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.imgView3.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg3) {
                window.iv.setImage(window.bg3);
                window.pane.getChildren().remove(window.imagesScrollPane);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.imgView4.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg4) {
                window.iv.setImage(window.bg4);
                window.pane.getChildren().remove(window.imagesScrollPane);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.imgView5.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg5) {
                window.iv.setImage(window.bg5);
                window.pane.getChildren().remove(window.imagesScrollPane);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.editBgButton.setOnMousePressed(event -> {
            if (window.flag) {
                window.pane.getChildren().add(window.imagesScrollPane);
                window.flag = false;
            }
            else {
                window.pane.getChildren().remove(window.imagesScrollPane);
                window.flag = true;
            }
        });

        window.editBgButton.setOnMouseEntered(event -> {
            adaptButtonStyle(window.editBgButton, "black", window.editBgButtonFontSize);
        });

        window.editBgButton.setOnMouseExited(event -> {
            adaptButtonStyle(window.editBgButton, "white", window.editBgButtonFontSize);
        });

        window.emptyTemplate.setOnMouseEntered(event -> {
            adaptButtonStyle(window.emptyTemplate, "black", window.emptyTemplateFontSize);
        });

        window.emptyTemplate.setOnMouseExited(event -> {
            adaptButtonStyle(window.emptyTemplate, "white", window.emptyTemplateFontSize);
        });

        window.scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                //System.out.println("Width: " + newSceneWidth);

                window.iv.setFitWidth((Double) newSceneWidth);

                widthScale = (window.WINDOW_WIDTH / (double) newSceneWidth);

                window.editBgButton.setPrefWidth(window.editBgButtonWidth/widthScale);
                window.editBgButton.setTranslateX(window.editBgButtonX/widthScale);
                String editBgButtonFontSizeStr = "-fx-font-size: " + window.emptyTemplateFontSize/widthScale +"px;";
                window.editBgButton.setStyle(" -fx-background-color: #000000; -fx-background-color:rgba(0, 0, 0, 0.2); -fx-background-radius: 15px; -fx-background-insets: 0,1,1;" +
                        " -fx-text-fill: white; -fx-font-family: \"Gadugi\"; " + editBgButtonFontSizeStr + " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ); -fx-border-color: #F5F5F5; -fx-border-radius: 13;  -fx-border-width: 0.6 0.6 0.6 0.6; ");

                window.imagesScrollPane.setPrefWidth(window.imagesScrollPaneWidth/widthScale);
                window.imagesScrollPane.setTranslateX(window.imagesScrollPaneX/widthScale);

                window.chatWindow.setWidth(window.chatWindowWidth/widthScale);
                window.chatWindow.setTranslateX(window.chatWindowX/widthScale);

                window.chatInputWindow.setWidth(window.chatInputWindowWidth/widthScale);
                window.chatInputWindow.setTranslateX(window.chatInputWindowX/widthScale);

                window.textField.setPrefWidth(window.textFieldWidth/widthScale);
                window.textField.setTranslateX(window.textFieldX/widthScale);

                window.scrollPane.setPrefWidth(window.scrollPaneWidth/widthScale);
                window.scrollPane.setTranslateX(window.scrollPaneX/widthScale);

                window.robotViewer.setFitWidth(window.robotViewerWidth/widthScale);
                window.robotViewer.setTranslateX(window.robotViewerX/widthScale);

                window.robotInteractionViewer.setFitWidth(window.robotInteractionViewerWidth/widthScale);
                window.robotInteractionViewer.setTranslateX(window.robotInteractionViewerX/widthScale);

                window.robotInteractionText.setFont(Font.font("Gadugi", FontWeight.BOLD, FontPosture.REGULAR, window.robotInteractionTextFontSize/widthScale));
                window.robotInteractionText.setTranslateX(window.robotInteractionTextX/widthScale);

                window.weatherWidget.setWidth(window.weatherWidgetWidth/widthScale);
                window.weatherWidget.setTranslateX(window.weatherWidgetX/widthScale);

                window.weatherDegree.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, window.weatherDegreeFontSize/widthScale));
                window.weatherDegree.setTranslateX(window.weatherDegreeX/widthScale);

                window.weatherCity.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, window.weatherCityFontSize/widthScale));
                window.weatherCity.setTranslateX(window.weatherCityX/widthScale);

                window.GPSView.setFitWidth(window.gpsViewerWidth/widthScale);
                window.GPSView.setTranslateX(window.gpsViewerX/widthScale);

                window.timeText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeTextFontSize/widthScale));
                window.timeText.setTranslateX(window.timeTextX/widthScale);

                window.dateText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.dateTextFontSize/widthScale));
                window.dateText.setTranslateX(window.dateTextX/widthScale);

                window.timezones.setWidth(window.timezonesWidth/widthScale);
                window.timezones.setTranslateX(window.timezonesX/widthScale);

                window.dropFile.setWidth(window.dropFileWidth/widthScale);
                window.dropFile.setTranslateX(window.dropFileX/widthScale);

                window.target.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.targetFontSize/widthScale));
                window.target.setTranslateX(window.targetX/widthScale);

                window.timeText1.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeText1FontSize/widthScale));
                window.timeText1.setTranslateX(window.timeText1X/widthScale);

                window.timeText2.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeText2FontSize/widthScale));
                window.timeText2.setTranslateX(window.timeText2X/widthScale);

                window.timeText3.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeText3FontSize/widthScale));
                window.timeText3.setTranslateX(window.timeText3X/widthScale);

                window.timeText4.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeText4FontSize/widthScale));
                window.timeText4.setTranslateX(window.timeText4X/widthScale);

                window.timeText5.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeText5FontSize/widthScale));
                window.timeText5.setTranslateX(window.timeText5X/widthScale);

                window.time1city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.time1cityFontSize/widthScale));
                window.time1city.setTranslateX(window.time1cityX/widthScale);

                window.time2city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.time2cityFontSize/widthScale));
                window.time2city.setTranslateX(window.time2cityX/widthScale);

                window.time3city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.time3cityFontSize/widthScale));
                window.time3city.setTranslateX(window.time3cityX/widthScale);

                window.time4city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.time4cityFontSize/widthScale));
                window.time4city.setTranslateX(window.time4cityX/widthScale);

                window.time5city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.time5cityFontSize/widthScale));
                window.time5city.setTranslateX(window.time5cityX/widthScale);

                window.quoteOutline.setWidth(window.quoteRectangleWidth/widthScale);
                window.quoteOutline.setTranslateX(window.quoteRectangleX/widthScale);

                window.quoteHeading.setFont(Font.font("Calibri", FontWeight.BOLD,  FontPosture.REGULAR, window.quoteOutlineFontSize/widthScale));
                window.quoteHeading.setTranslateX(window.quoteX/widthScale);

                window.quote.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.quoteFontSize/widthScale));
                window.quote.setTranslateX(window.quoteX/widthScale);

                window.emptyTemplate.setPrefWidth(window.emptyTemplateWidth/widthScale);
                window.emptyTemplate.setTranslateX(window.emptyTemplateX/widthScale);
                String emptyTemplateFontSizeStr = "-fx-font-size: " + window.emptyTemplateFontSize/widthScale +"px;";
                window.emptyTemplate.setStyle(" -fx-background-color: #000000; -fx-background-color:rgba(0, 0, 0, 0.2); -fx-background-radius: 15px; -fx-background-insets: 0,1,1;" +
                        " -fx-text-fill: white; -fx-font-family: \"Gadugi\"; " + emptyTemplateFontSizeStr + " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ); -fx-border-color: #F5F5F5; -fx-border-radius: 13;  -fx-border-width: 0.6 0.6 0.6 0.6; ");
            }
        });

        window.scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                //System.out.println("Height: " + newSceneHeight);

                window.iv.setFitHeight((Double) newSceneHeight);

                heightScale = (window.WINDOW_HEIGHT / (double) newSceneHeight);

                window.editBgButton.setPrefHeight(window.editBgButtonHeight/heightScale);
                window.editBgButton.setTranslateY(window.editBgButtonY/heightScale);
                String editBgButtonFontSizeStr = "-fx-font-size: " + window.editBgButtonFontSize/heightScale +"px;";
                window.editBgButton.setStyle(" -fx-background-color: #000000; -fx-background-color:rgba(0, 0, 0, 0.2); -fx-background-radius: 15px; -fx-background-insets: 0,1,1;" +
                        " -fx-text-fill: white; -fx-font-family: \"Gadugi\"; " + editBgButtonFontSizeStr + " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ); -fx-border-color: #F5F5F5; -fx-border-radius: 13;  -fx-border-width: 0.6 0.6 0.6 0.6; ");

                window.imagesScrollPane.setPrefHeight(window.imagesScrollPaneHeight/heightScale);
                window.imagesScrollPane.setTranslateY(window.imagesScrollPaneY/heightScale);

                window.chatWindow.setHeight(window.chatWindowHeight/heightScale);
                window.chatWindow.setTranslateY(window.chatWindowY/heightScale);

                window.chatInputWindow.setHeight(window.chatInputWindowHeight/heightScale);
                window.chatInputWindow.setTranslateY(window.chatInputWindowY/heightScale);

                window.textField.setFont(Font.font("Calibri", FontPosture.REGULAR, window.textFieldFontSize/heightScale));
                window.textField.setPrefHeight(window.textFieldHeight/heightScale);
                window.textField.setTranslateY(window.textFieldY/heightScale);

                window.scrollPane.setPrefHeight(window.scrollPaneHeight/heightScale);
                window.scrollPane.setTranslateY(window.scrollPaneY/heightScale);

                window.robotViewer.setFitHeight(window.robotViewerHeight/heightScale);
                window.robotViewer.setTranslateY(window.robotViewerY/heightScale);

                window.robotInteractionViewer.setFitHeight(window.robotInteractionViewerHeight/heightScale);
                window.robotInteractionViewer.setTranslateY(window.robotInteractionViewerY/heightScale);

                window.robotInteractionText.setFont(Font.font("Gadugi", FontWeight.BOLD, FontPosture.REGULAR, window.robotInteractionTextFontSize/heightScale));
                window.robotInteractionText.setTranslateY(window.robotInteractionTextY/heightScale);

                window.weatherWidget.setHeight(window.weatherWidgetHeight/heightScale);
                window.weatherWidget.setTranslateY(window.weatherWidgetY/heightScale);

                window.weatherDegree.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, window.weatherDegreeFontSize/heightScale));
                window.weatherDegree.setTranslateY(window.weatherDegreeY/heightScale);

                window.weatherCity.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, window.weatherCityFontSize/heightScale));
                window.weatherCity.setTranslateY(window.weatherCityY/heightScale);

                window.GPSView.setFitHeight(window.gpsViewerHeight/heightScale);
                window.GPSView.setTranslateY(window.gpsViewerY/heightScale);

                window.timeText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeTextFontSize/heightScale));
                window.timeText.setTranslateY(window.timeTextY/heightScale);

                window.dateText.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.dateTextFontSize/heightScale));
                window.dateText.setTranslateY(window.dateTextY/heightScale);

                window.timezones.setHeight(window.timezonesHeight/heightScale);
                window.timezones.setTranslateY(window.timezonesY/heightScale);

                window.dropFile.setHeight(window.dropFileHeight/heightScale);
                window.dropFile.setTranslateY(window.dropFileY/heightScale);

                window.target.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.targetFontSize/heightScale));
                window.target.setTranslateY(window.targetY/heightScale);

                window.timeText1.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeText1FontSize/heightScale));
                window.timeText1.setTranslateY(window.timeText1Y/heightScale);

                window.timeText2.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeText2FontSize/heightScale));
                window.timeText2.setTranslateY(window.timeText2Y/heightScale);

                window.timeText3.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeText3FontSize/heightScale));
                window.timeText3.setTranslateY(window.timeText3Y/heightScale);

                window.timeText4.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeText4FontSize/heightScale));
                window.timeText4.setTranslateY(window.timeText4Y/heightScale);

                window.timeText5.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.timeText5FontSize/heightScale));
                window.timeText5.setTranslateY(window.timeText5Y/heightScale);

                window.time1city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.time1cityFontSize/heightScale));
                window.time1city.setTranslateY(window.time1cityY/heightScale);

                window.time2city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.time2cityFontSize/heightScale));
                window.time2city.setTranslateY(window.time2cityY/heightScale);

                window.time3city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.time3cityFontSize/heightScale));
                window.time3city.setTranslateY(window.time3cityY/heightScale);

                window.time4city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.time4cityFontSize/heightScale));
                window.time4city.setTranslateY(window.time4cityY/heightScale);

                window.time5city.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.time5cityFontSize/heightScale));
                window.time5city.setTranslateY(window.time5cityY/heightScale);

                window.quoteOutline.setHeight(window.quoteRectangleHeight/heightScale);
                window.quoteOutline.setTranslateY(window.quoteRectangleY/heightScale);

                window.quoteHeading.setFont(Font.font("Calibri", FontWeight.BOLD,  FontPosture.REGULAR, window.quoteOutlineFontSize/heightScale));
                window.quoteHeading.setTranslateY((window.quoteY-25)/heightScale);

                window.quote.setFont(Font.font("Calibri Light", FontWeight.BOLD,  FontPosture.REGULAR, window.quoteFontSize/heightScale));
                window.quote.setTranslateY(window.quoteY/heightScale);

                window.emptyTemplate.setPrefHeight(window.emptyTemplateHeight/heightScale);
                window.emptyTemplate.setTranslateY(window.emptyTemplateY/heightScale);
                String emptyTemplateFontSizeStr = "-fx-font-size: " + window.emptyTemplateFontSize/heightScale +"px;";
                window.emptyTemplate.setStyle(" -fx-background-color: #000000; -fx-background-color:rgba(0, 0, 0, 0.2); -fx-background-radius: 15px; -fx-background-insets: 0,1,1;" +
                        " -fx-text-fill: white; -fx-font-family: \"Gadugi\"; " + emptyTemplateFontSizeStr + " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ); -fx-border-color: #F5F5F5; -fx-border-radius: 13;  -fx-border-width: 0.6 0.6 0.6 0.6; ");
            }
        });

        Text target = window.target;

        target.setOnDragEntered(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {

                Dragboard db = event.getDragboard();

                File filepath = null;
                if(getExtension(db.getFiles().get(0).toString()).equals("txt")){
                    filepath = db.getFiles().get(0);
                    window.assistant.notifyOfNewPath(filepath.toString());
                    System.out.println(filepath);
                }else{
                    System.out.println("This is not a text file");
                }
                target.setFill(Color.BLACK);
                event.consume();
            }
        });

        target.setOnDragExited(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {

                target.setFill(Color.WHITE);
                event.consume();
            }
        });

        Button clearTemplate = window.emptyTemplate;

        clearTemplate.setOnAction(actionEvent ->  {
                assistant.forgetTemplates();
        });
    }

    private String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1) //if the name is not empty
            return fileName.substring(i + 1).toLowerCase();

        return extension;
    }

    private void adaptButtonStyle(Button button, String color, int fontSize) {

        if (widthScale > heightScale) {
            String fontSizeStr = "-fx-font-size: " + fontSize/widthScale +"px;";
            button.setStyle(" -fx-background-color: #000000; -fx-background-color:rgba(0, 0, 0, 0.2); -fx-background-radius: 15px; -fx-background-insets: 0,1,1;" +
                    " -fx-text-fill: " + color + "; -fx-font-family: \"Gadugi\"; " + fontSizeStr + " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ); -fx-border-color: #F5F5F5; -fx-border-radius: 13;  -fx-border-width: 0.6 0.6 0.6 0.6; ");
        }
        else {
            String fontSizeStr = "-fx-font-size: " + fontSize/heightScale +"px;";
            button.setStyle(" -fx-background-color: #000000; -fx-background-color:rgba(0, 0, 0, 0.2); -fx-background-radius: 15px; -fx-background-insets: 0,1,1;" +
                    " -fx-text-fill: " + color + "; -fx-font-family: \"Gadugi\"; " + fontSizeStr + " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 ); -fx-border-color: #F5F5F5; -fx-border-radius: 13;  -fx-border-width: 0.6 0.6 0.6 0.6; ");
        }
    }
}