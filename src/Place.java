
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


abstract class Place extends JComponent {
    private Position position;
    private Category category;
    private final int MARKER_SIZE = 20;
    private final int SELECTION_PADDING = 4; //space for selection border
    private String name;
    private PlaceManager pm;

    public Place(Position position, Category category, String name, PlaceManager pm){
        this.pm = pm;
        addMouseListener(pm.pmo);
        this.position = position;
        this.category = category;
        this.name = name;
        this.setBounds(position.getX()- MARKER_SIZE,
                position.getY()- MARKER_SIZE,
                MARKER_SIZE*2,MARKER_SIZE*2);
    }

    public Position getPosition() { return position; }
    public Category getCategory() {
        return category;
    }
    public String getName(){
        return name;
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(pm.isSelected(this)) {
            int[] xMark = {(int)( MARKER_SIZE*0.66)-SELECTION_PADDING,MARKER_SIZE,(int)(MARKER_SIZE*1.33)+SELECTION_PADDING};
            int[] yMark = {SELECTION_PADDING/2,MARKER_SIZE,SELECTION_PADDING/2};
            triangleDraw(g,xMark,yMark, Color.ORANGE);
        }
        int[] xCoor = {(int)( MARKER_SIZE*0.66),MARKER_SIZE,(int)(MARKER_SIZE*1.33)};
        int[] yCoor = {SELECTION_PADDING,MARKER_SIZE,SELECTION_PADDING};
        triangleDraw(g,xCoor,yCoor, category.getColor());
    }
    private void triangleDraw(Graphics g, int[]x, int[]y, Color c){
        g.setColor(c);
        g.fillPolygon(x,y,3);
        this.setVisible(true);
    }
    public abstract void showInfo();

    class Info extends JPanel{
        public Info(){
            JLabel namelaB = new JLabel("Name: ");
            JTextField name = new JTextField(Place.this.getName());
            name.setEnabled(false);
            add(namelaB);
            add(name);
        }
    }
}

class NamedPlace extends  Place{
    public NamedPlace(Position p, Category c, String name,PlaceManager pm){
        super(p,c,name, pm);
    }
    @Override
    public void showInfo(){
        Info temp = new Info();
        JOptionPane.showMessageDialog(this,temp);
        }
}

class DescribedPlace extends NamedPlace{

    public DescribedPlace(Position position, Category category, String name, String description,PlaceManager pm){
        super(position,category,name,pm);
        this.description = description;
    }
    private String description;
    public String getDescription() {
        return description;
    }
    @Override
    public void showInfo(){
        DescInfo temp = new DescInfo();
        JOptionPane.showMessageDialog(this,temp);
    }
    class DescInfo extends Info{
        public DescInfo(){
            JLabel descLab =  new JLabel("Description: ");
            JTextField description = new JTextField(DescribedPlace.this.getDescription());
            description.setEnabled(false);
            add(descLab);
            add(description);
        }
    }
}



