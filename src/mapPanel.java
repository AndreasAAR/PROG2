import javax.swing.*;
import java.awt.*;

class MapPanel extends JPanel {
    private ImageIcon map;

    public MapPanel(String s){
        map = new ImageIcon(s);
        setPreferredSize(new Dimension(map.getIconHeight(), map.getIconWidth()));
        setLayout(null);
    }

    @Override
    public void paintComponent(Graphics graf) {
        super.paintComponent(graf);
        graf.drawImage(map.getImage(), 0, 0, this);
    }

    public void addPlace(Place p){
        add(p);
    }
    public void removePlace(Place p){ remove(p);}

}
