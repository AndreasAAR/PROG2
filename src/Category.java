import java.awt.*;

public enum Category {

    None ("None", Color.BLACK),
    Bus ("Bus", Color.RED),
    Underground("Underground", Color.BLUE),
    Train("Train", Color.GREEN);

    private String text;
    private Color color;

    Color getColor() {
        return color;
    }
   String getText (){return text;}

    Category(String text, Color c){
        this.text = text;
        this.color = c;
    }
}