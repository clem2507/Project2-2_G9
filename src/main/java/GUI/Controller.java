package GUI;

import backend.MessageType;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class Controller {

    private Main window;
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

        window.textField.setOnKeyPressed(t -> {
            KeyCode key = t.getCode();
            switch (key) {
                case ESCAPE:
                    System.exit(0);
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
                case DOWN:
                    if (historyCount < window.messageHistory.size()-1) {
                        window.textField.setText(window.messageHistory.get(historyCount+1));
                        historyCount++;
                    }
                    else {
                        window.textField.setText("");
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

        // TODO: EDIT DRAG AND DROP EVENT HANDLER
        Text target = window.target;

        target.setOnDragOver(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                System.out.println("onDragOver");

                /* accept it only if it is  not dragged from the same node
                 * and if it has a string data */
                if (event.getGestureSource() != target &&
                        event.getDragboard().hasString()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        target.setOnDragDropped(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {

                System.out.println("onDragDropped");
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    target.setText(db.getString());
                    success = true;
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);
                event.consume();
            }
        });

        /**
        target.setOnDragEntered(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {
                // the drag-and-drop gesture entered the target
                System.out.println("onDragEntered");
                // show to the user that it is an actual gesture target
                if (event.getGestureSource() != target &&
                        event.getDragboard().hasString()) {
                    target.setFill(Color.GREEN);
                }

                event.consume();
            }
        });**/

        target.setOnDragExited(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {
                /* mouse moved away, remove the graphical cues */
                System.out.println("hi Zein idk what happens in this event handler lmao");
                target.setFill(Color.BLACK);
                event.consume();
            }
        });
    }
}
