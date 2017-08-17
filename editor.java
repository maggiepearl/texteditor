package editor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.util.List;

import java.io.*;


public class Editor extends Application {
    /** An EventHandler to handle keys that get pressed. */

    private final double WINDOW_WIDTH = 500;
    private final int WINDOW_HEIGHT = 500;

    private double windowWidth = WINDOW_WIDTH;
    private double windowHeight = WINDOW_HEIGHT;

    public final int defaultfontSize = 20;
    private LinkedListDeque<Event> u = new LinkedListDeque<Event>();
    private LinkedListDeque<Event> r = new LinkedListDeque<Event>();
    private HundStack undone = new HundStack(u, r);


    private LinkedListDeque<Text> b = new LinkedListDeque<Text>();




    private int getDimensionInsideMargin(int outsideDimension) {
        return outsideDimension - 2 * MARGIN;
    }
    private final int MARGIN = 5;

    private String fileName;


    public String fontName = "Verdana";
    public int fontSize = defaultfontSize;

    private Cursor cursor = new Cursor(b, fontHeight());
    private Group allText = new Group();

    public void fileSave(){

        try {
            FileWriter writer = new FileWriter(fileName);
            // Keep reading from the file input read() returns -1, which means the end of the file
            // was reached.
            for (int i = 0; i < b.size(); i++){
                // The integer read can be cast to a char, because we're assuming ASCII.
                String charRead = b.get(i).getText();
                writer.write(charRead);
            }
            writer.close();
        } catch (IOException i ){

        }

    }
    public void doUndo(){
        if(!undone.undoList.isEmpty()) {
            Event e = undone.undo();
            if(e.eventType.equals("add")){
                b.cursorPoint = e.node.prev;
                e.node.next.prev = e.node.prev;
                e.node.prev.next = e.node.next;
                b.size -= 1;

            }
            else if(e.eventType.equals("remove")){
                b.cursorPoint = e.node;
                e.node.prev.next = e.node;
                e.node.next.prev = e.node;
                if (!allText.getChildren().contains(e.node.item)) {
                    allText.getChildren().add((Text) e.node.item);
                }
                b.size += 1;
                b.printDeque();
            }

        }
    }
    public void doRedo(){
        if(!undone.redoList.isEmpty()) {
            Event e = undone.redo();
            if(e.eventType.equals("remove")){
                b.cursorPoint = e.node.prev;
                e.node.next.prev = e.node.prev;
                e.node.prev.next = e.node.next;
                b.size -= 1;

            }
            else if(e.eventType.equals("add")){
                b.cursorPoint = e.node;
                e.node.prev.next = e.node;
                e.node.next.prev = e.node;
                if (!allText.getChildren().contains(e.node.item)) {
                    allText.getChildren().add((Text) e.node.item);
                }
                b.size += 1;
                b.printDeque();
            }

        }
    }


    private double fontHeight() {
        Font f = new Font(fontName,fontSize);
        Text t = new Text("M");
        t.setFont(f);
        return t.getLayoutBounds().getHeight();
    }
    private void render() {
        int x = 5;
        int y = 0;
        boolean isSpace = false;
        allText.getChildren().clear();
        b.printDeque();
        for (int i = 0; i < b.size(); i++) {
            if (b.get(i).getText().equals("\n") || b.get(i).getText().equals("\r")) {
                y += b.get(i).getLayoutBounds().getHeight() / 2;
                x = 5;
                cursor.change(fontHeight());
            } else if (b.get(i).getText().equals(" ")) {
                isSpace = true;
            }
            b.get(i).setX(x);
            b.get(i).setY(y);
            b.get(i).setFont(Font.font(fontName, fontSize));
            b.get(i).setTextOrigin(VPos.TOP);
            x += b.get(i).getLayoutBounds().getWidth();

            if (x > windowWidth) {
                y += b.get(i).getLayoutBounds().getHeight();
                x = 5;
                if (isSpace) {
                    LinkedListDeque<Text> temp = new LinkedListDeque<Text>();
                    int ListLength = 0;
                    int temporary = i;
                    while (!b.get(temporary).getText().equals(" ")) {
                        temp.addFirst(b.get(temporary));
                        temporary -= 1;
                        ListLength += b.get(temporary).getLayoutBounds().getWidth();

                    }
                    if (ListLength > windowWidth) {
                        b.get(i).setX(x);
                        b.get(i).setY(y);
                        x += b.get(i).getLayoutBounds().getWidth();


                    } else {
                        for (int j = 0; j < temp.size(); j++) {
                            temp.get(j).setX(x);
                            temp.get(j).setY(y);
                            b.get(i).setFont(Font.font(fontName, fontSize));
                            b.get(i).setTextOrigin(VPos.TOP);
                            x += temp.get(j).getLayoutBounds().getWidth();
                        }
                        isSpace = false;
                    }
                } else {
                    b.get(i).setX(x);
                    b.get(i).setY(y);
                    b.get(i).setFont(Font.font(fontName, fontSize));
                    b.get(i).setTextOrigin(VPos.TOP);
                    x += b.get(i).getLayoutBounds().getWidth();

                }


            }

            allText.getChildren().add(b.get(i));
            cursor.change(fontHeight());
        }
    }








    private class KeyEventHandler implements EventHandler<KeyEvent> {
        int textCenterX;
        int textCenterY;

        /**
         * The Text to display on the screen.
         */



        private KeyEventHandler(final Group root) {
            textCenterX = 0;
            textCenterY = 0;

            // Initialize some empty text and add it to root so that it will be displayed.
            // Always set the text origin to be VPos.TOP! Setting the origin to be VPos.TOP means
            // that when the text is assigned a y-position, that position corresponds to the
            // highest position across all letters (for example, the top of a letter like "I", as
            // opposed to the top of a letter like "e"), which makes calculating positions much
            // simpler!
            root.getChildren().add(allText);

            // All new Nodes need to be added to the root in order to be displayed.

        }

        @Override
        public void handle(KeyEvent keyEvent) {
            allText.getChildren().remove(cursor.returnPic());
            if(keyEvent.isShortcutDown()){
                KeyCode code = keyEvent.getCode();
                if(code == KeyCode.EQUALS){
                    fontSize += 4;
                    render();
                }
                if(code == KeyCode.MINUS){
                    fontSize -= 4;
                    render();
                }
                if(code == KeyCode.S){
                        fileSave();
                }
                if(code == KeyCode.Z){
                    doUndo();
                    render();
                }
                if(code == KeyCode.Y){
                    doRedo();
                    render();
                }
            }
            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8 && !keyEvent.isShortcutDown()) {
                    // Ignore control keys, which have non-zero length,
                    // as well as the backspace key, which is
                    // represented as a character of value = 8 on Windows.

                    Text t = new Text(characterTyped);
                    t.setFont(Font.font(fontName, fontSize));
                    t.setTextOrigin(VPos.TOP);
                    b.cursorPoint.next = b.createNode(t,b.cursorPoint, b.cursorPoint.next);
                    b.updateCursorForwards();

                    b.size += 1;
                    render();

                    undone.addToUndo(new Event(b.cursorPoint, "add"));

                    keyEvent.consume();
                }
//                cursor.change();
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.LEFT) {

                    if (!b.isEmpty()) {
                        if(b.cursorPoint.prev != b.sentinel) {
                            b.updateCursorBackwards();
                        }
                        keyEvent.consume();
                        render();

                    }
                } else if (code == KeyCode.RIGHT) {
                    if (!b.isEmpty()) {
                        if(b.cursorPoint.next != b.sentinel) {
                            b.updateCursorForwards();
                        }
                        keyEvent.consume();
                        render();
                    }
                } else if (code == KeyCode.BACK_SPACE && !b.isEmpty() && b.cursorPoint.item != null) {
                    allText.getChildren().remove(b.returnCursorPoint().getText());
                    undone.addToUndo(new Event(b.cursorPoint, "remove"));
                    b.cursorPoint.next.prev = b.cursorPoint.prev;
                    b.cursorPoint.prev.next = b.cursorPoint.next;
                    b.updateCursorBackwards();
                    b.size -= 1;
                    render();


                if (!b.isEmpty()  && b.cursorPoint.item != null && b.returnCursorPoint().getText().equals("/n")) {
                    allText.getChildren().remove(b.cursorPoint.item);
                    b.cursorPoint.prev = b.cursorPoint.next;
                    b.cursorPoint.next = b.cursorPoint.prev;
                    b.cursorPoint = b.cursorPoint.prev;
                    b.updateCursorBackwards();
                    undone.addToUndo(new Event(b.cursorPoint, "add"));
                    render();
                }

                keyEvent.consume();


                }
                else if(code == KeyCode.DOWN){
                    if(b.cursorPoint.prev != b.sentinel) {

                        int x = (int) b.returnCursorPoint().getX();
                        int y = (int) b.returnCursorPoint().getY();
                        int counter = 0;
                        int findUp = y + (int) b.returnCursorPoint().getLayoutBounds().getHeight();
                        b.cursorPoint = b.sentinel.next;
                        while (b.returnCursorPoint().getY() < findUp && b.cursorPoint.next != b.sentinel) {
                            b.updateCursorForwards();
                            counter++;
                        }
                        if (b.cursorPoint.next != b.sentinel) {
                            while (b.returnCursorPoint().getX() < x && b.cursorPoint.next != b.sentinel) {
                                b.updateCursorForwards();
                            }

                            render();
                        } else {
                            for (int i = 0; i < counter; i++) {
                                b.updateCursorBackwards();
                            }
                        }
                    }

                }
                else if(code == KeyCode.UP) {
                    if(b.cursorPoint != b.sentinel) {

                        int x = (int) b.returnCursorPoint().getX();
                        int y = (int) b.returnCursorPoint().getY();
                        int findUp = y - (int) b.returnCursorPoint().getLayoutBounds().getHeight();
                        if (findUp >= 0) {
                            b.cursorPoint = b.sentinel.next;
                            while (b.returnCursorPoint().getY() != findUp) {
                                b.updateCursorForwards();
                            }
                            while (b.returnCursorPoint().getX() < x && b.returnCursorPointNext().getY() < y) {
                                b.updateCursorForwards();
                            }

                            render();
                        }
                    }
                }
            }
            allText.getChildren().add(cursor.returnPic());

        }

    }

    @Override
    public void start(Stage primaryStage) {
        // Create a Node that will be the parent of all things displayed on the screen.
        Group root = new Group();
        allText.getChildren().add(cursor.returnPic());
        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);
        scene.setOnMouseClicked(new MouseClickEventHandler(root));

        // To get information about what keys the user is pressing, create an EventHandler.
        // EventHandler subclasses must override the "handle" function, which will be called
        // by javafx.
        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root);
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);

        cursor.makeRectangleColorChange();

        primaryStage.setTitle("Out of ConText");
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                // Re-compute Allen's width.
                int newAllenWidth = getDimensionInsideMargin(newScreenWidth.intValue());
                windowWidth= newAllenWidth;
                render();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                int newAllenHeight = getDimensionInsideMargin(newScreenHeight.intValue());
                windowHeight = newAllenHeight;
                render();
            }
        });

        List<String> args = getParameters().getRaw();
        fileName = args.get(0);

        try {
            File inputFile = new File(args.get(0));
            // Check to make sure that the input file exists!

            FileReader reader = new FileReader(inputFile);
            // It's good practice to read files using a buffered reader.  A buffered reader reads
            // big chunks of the file from the disk, and then buffers them in memory.  Otherwise,
            // if you read one character at a time from the file using FileReader, each character
            // read causes a separate read from disk.  You'll learn more about this if you take more
            // CS classes, but for now, take our word for it!
            BufferedReader bufferedReader = new BufferedReader(reader);

            // Create a FileWriter to write to outputFilename. FileWriter will overwrite any data
            // already in outputFilename.

            int intRead = -1;
            // Keep reading from the file input read() returns -1, which means the end of the file
            // was reached.
            while ((intRead = bufferedReader.read()) != -1) {
                // The integer read can be cast to a char, because we're assuming ASCII.
                char charRead = (char) intRead;
                b.addLast(new Text(Character.toString(charRead)));
            }

            System.out.println("Successfully saved file " + args );

            // Close the reader and writer.
            bufferedReader.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }


    // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
        render();
    }

    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        /**
         * A Text object that will be used to print the current mouse position.
         */
        Text positionText;

        MouseClickEventHandler(Group root) {
            // For now, since there's no mouse position yet, just create an empty Text object.
            positionText = new Text("");
            // We want the text to show up immediately above the position, so set the origin to be
            // VPos.BOTTOM (so the x-position we assign will be the position of the bottom of the
            // text).
            positionText.setTextOrigin(VPos.BOTTOM);

            // Add the positionText to root, so that it will be displayed on the screen.
            root.getChildren().add(positionText);
        }


        @Override
        public void handle(MouseEvent mouseEvent) {
            // Because we registered this EventHandler using setOnMouseClicked, it will only called
            // with mouse events of type MouseEvent.MOUSE_CLICKED.  A mouse clicked event is
            // generated anytime the mouse is pressed and released on the same JavaFX node.
            allText.getChildren().remove(cursor.returnPic());
            if(b.cursorPoint == b.sentinel){
                b.cursorPoint = b.cursorPoint.next;
            }
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();

            // Display text right above the click.
            positionText.setX(mousePressedX);
            positionText.setY(mousePressedY);
            int x = (int) b.returnCursorPoint().getX();
            int y = (int) b.returnCursorPoint().getY();
            if (positionText.getY() < y) {
                while (positionText.getY() - (int) b.returnCursorPoint().getLayoutBounds().getHeight() <= y) {
                    y -= (int) b.returnCursorPoint().getLayoutBounds().getHeight();
                }
                y += (int) b.returnCursorPoint().getLayoutBounds().getHeight();
            }
            if (positionText.getY() > y) {
                while (positionText.getY() + (int) b.returnCursorPoint().getLayoutBounds().getHeight() >= y) {
                    y += (int) b.returnCursorPoint().getLayoutBounds().getHeight();
                }
                y -= (int) b.returnCursorPoint().getLayoutBounds().getHeight();
            }


            positionText.setX(mousePressedX);
            positionText.setY(y);
            System.out.println(b.returnCursorPoint().getLayoutBounds().getHeight());
            System.out.println(y);
            b.cursorPoint = b.sentinel.next;
            while((int)b.returnCursorPoint().getLayoutBounds().getHeight() + b.returnCursorPoint().getY() != y && b.cursorPoint.next != b.sentinel){
                b.updateCursorForwards();
            }
            while(b.returnCursorPoint().getX() < mousePressedX && b.cursorPoint.next != b.sentinel){
                b.updateCursorForwards();
            }
            render();
            allText.getChildren().add(cursor.returnPic());

        }
    }



    public static void main(String[] args) {
        launch(args);
    }

}
