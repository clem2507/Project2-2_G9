package GUI;

import backend.Assistant;
import backend.MessageType;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.io.File;

public class Controller {

    private final Main window;
    private int historyCount = 0;

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
                window.right.getChildren().remove(window.imagesScrollPane);
                window.iv.setImage(window.bg);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.imgView1.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg1) {
                window.iv.setImage(window.bg1);
                window.right.getChildren().remove(window.imagesScrollPane);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.imgView2.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg2) {
                window.iv.setImage(window.bg2);
                window.right.getChildren().remove(window.imagesScrollPane);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.imgView3.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg3) {
                window.iv.setImage(window.bg3);
                window.right.getChildren().remove(window.imagesScrollPane);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.imgView4.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg4) {
                window.iv.setImage(window.bg4);
                window.right.getChildren().remove(window.imagesScrollPane);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.imgView5.setOnMousePressed(event -> {
            if (window.iv.getImage()!=window.bg5) {
                window.iv.setImage(window.bg5);
                window.right.getChildren().remove(window.imagesScrollPane);
                window.robotInteractionText.setText("Nice background,\nI like it!");
                window.flag = true;
            }
        });

        window.editBgButton.setOnMousePressed(event -> {
            if (window.flag) {
                window.right.getChildren().add(window.imagesScrollPane);
                window.flag = false;
            }
            else {
                window.right.getChildren().remove(window.imagesScrollPane);
                window.flag = true;
            }
        });

        Text target = window.target;

        target.setOnDragExited(new EventHandler <DragEvent>() {
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
    }
    private String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1) //if the name is not empty
            return fileName.substring(i + 1).toLowerCase();

        return extension;
    }

}