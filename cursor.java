package editor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.Group;

public class Cursor {
    public int xPos;
    public int yPos;

    public Rectangle pic;
    LinkedListDeque<Text> changed;

    public Cursor(LinkedListDeque b, double fontHeight) {
        xPos = 5;
        yPos = 0;
        changed = b;
        pic = new Rectangle(1, fontHeight);

    }
    public Rectangle returnPic(){
        return pic;
    }


    public void change(double fontHeight) {
        pic.setHeight(fontHeight);
        if(changed.cursorPoint == changed.sentinel){
            xPos = 5;
            yPos = 0;
            pic.setX(xPos);
            pic.setY(yPos);


        }
        else {
            xPos = (int) changed.returnCursorPoint().getX() + (int) changed.returnCursorPoint().getLayoutBounds().getWidth();
            yPos = (int) changed.returnCursorPoint().getY();
            pic.setX(xPos);
            pic.setY(yPos);

        }
    }
    public void lineReset(String fontName, int fontSize){
        xPos = 5;
        yPos = (int) changed.returnCursorPoint().getY();
        pic.setX(xPos);
        pic.setY(yPos);

    }

    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.WHITE};

        RectangleBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            pic.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }
    public void makeRectangleColorChange() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public void print() {
    }


}
