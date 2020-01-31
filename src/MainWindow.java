import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame{
    private PlaceManager pm;
    private JButton hideCategoryButton;
    private JList<String> categoryList;
    private JScrollPane categoryBox;
    private JButton newButton;
    private JRadioButton namedButton;
    private JRadioButton describedButton;
    private JTextField searchField;
    private JButton searchButton;
    private JButton hideButton;
    private JButton removeButton;
    private JButton coordinateButton;
    private JScrollPane mapScroller;
    private MapPanel map;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem newImage = new JMenuItem("New Map");
    private JMenuItem loadPlaces = new JMenuItem("Load Places");
    private JMenuItem savePlaces = new JMenuItem("Save");
    private JMenuItem exit = new JMenuItem("Exit");
    private JFileChooser fileChooser = new JFileChooser("./jarvafaltet.png");
    public MainWindow(){
        super("Map");
        this.pm = new PlaceManager(this);
        menu = new JMenu("Archive");
        newImage.addActionListener(a -> {
            String filename = openFileChooser();
            if(filename != null)
                newMap(filename);
            });
        menu.add(newImage);
        menu.add(loadPlaces);
        loadPlaces.addActionListener(a -> {
            if(!saveCheck()){
                return;
            }
            if(map == null){ JOptionPane.showMessageDialog(this,"You need to load a map first"); return;}
            String filename = openFileChooser();
            if(filename != null)
                pm.loadPlaces(filename);
        });
        menu.add(savePlaces);
        savePlaces.addActionListener(e -> {
            if(map == null){ JOptionPane.showMessageDialog(this,"You need to load a map first"); return;}
            String filename = openFileChooser();
            if(filename != null)
                pm.savePlaces(filename);
        });
        menu.add(exit);
        exit.addActionListener(a -> {
            exitControl();
        });
        menuBar = new JMenuBar();
        menuBar.add(menu);
        setLayout(new BorderLayout());
        this.setSize(new Dimension(1000,600));
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new FlowLayout());
        hideCategoryButton =  new JButton("Hide Category");
        hideCategoryButton.setEnabled(true);
        JPanel east = new JPanel();
        east.setLayout(new BoxLayout(east, BoxLayout.PAGE_AXIS));
        eastPanel.add(east);
        add(eastPanel, BorderLayout.EAST);
        String[] categories = {"Bus","Underground","Train"};
        categoryList = new JList(categories);
        categoryList.setEnabled(true);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryBox =  new JScrollPane(categoryList);
        categoryList.getSelectionModel().addListSelectionListener(
                a -> {
                    hideCategoryButton.addActionListener(c -> {
                    pm.hideCategory(Category.valueOf((categoryList.getSelectedValue())));
                }); }
        );
        categoryList.getSelectionModel().addListSelectionListener(e -> {
            if(!categoryList.isSelectionEmpty())
                pm.showCategory(Category.valueOf((categoryList.getSelectedValue())));
        });
        east.add(categoryBox);
        east.add(hideCategoryButton);
        JPanel northPanel = new JPanel();
        setJMenuBar(menuBar);
        add(northPanel, BorderLayout.NORTH);
        newButton = new JButton("New");
        newButton.setEnabled(false); //Need to have a map first
        northPanel.add(newButton);
        newButton.addActionListener(e -> {
            newButton.setEnabled(false);
            setCursor(Cursor.CROSSHAIR_CURSOR);
            map.addMouseListener(new placeAdder());
        });
        ButtonGroup describeButtons =  new ButtonGroup();
        describeButtons.add(describedButton = new JRadioButton("Described"));
        describeButtons.add(namedButton =  new JRadioButton("Named"));
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));
        radioPanel.add(describedButton);
        radioPanel.add(namedButton);
        northPanel.add(radioPanel);
        northPanel.add(searchField  = new JTextField("Search"));
        searchField.setPreferredSize(new Dimension(100,30));
        northPanel.add(searchButton = new JButton("Search"));
        searchButton.addActionListener(a -> {
            nameSearch();
        });
        northPanel.add(hideButton = new JButton("Hide"));
         hideButton.addActionListener(a -> {
            pm.hideSelected();
        });
        northPanel.add(removeButton = new JButton("Remove"));
         removeButton.addActionListener(a -> {
              pm.removeSelected();
        } );
        northPanel.add(coordinateButton = new JButton("Coordinates"));
        coordinateButton.addActionListener(a -> {
            searchCoordinates();
        });
        namedButton.setSelected(true);
        this.addWindowListener(new ExitListener());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public void needSelected(boolean b){
        if(b){
            hideButton.setEnabled(true);
            return;
        }
        hideButton.setEnabled(false);
    }

    private void searchCoordinates(){
        try{
            TwoFieldPrompt prompt =  new TwoFieldPrompt("x", "y");
            int answer = JOptionPane.showConfirmDialog(this,prompt,"Coordinage Search",JOptionPane.OK_CANCEL_OPTION);
            Position position = new Position(Integer.parseInt(prompt.getField1()) , Integer.parseInt(prompt.getField2()) );
            if(pm.getPlace(position) != null){
                pm.unselectAll();
               pm.selectSingle(pm.getPlace(position));
            } if(pm.getPlace(position) == null){
                JOptionPane.showMessageDialog(this,"Could not find coordinates");
            }
        }
        catch(NumberFormatException nf){
            JOptionPane.showMessageDialog(this,"Incorrect Numberformat");
        }
    }

    private class ExitListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            exitControl();
        }
    }

    private void exitControl(){
        if(!saveCheck()){
            return;
        }
        else {
            System.exit(0);
        }
    }

    private boolean saveCheck(){
        if(pm.isDataSaved()){
            return true;
        }
        else{
            int result = JOptionPane.showConfirmDialog(MainWindow.this,
                    "Unsaved data, sure you want to continue?","",JOptionPane.OK_CANCEL_OPTION);
            if(result == JOptionPane.OK_OPTION){
                return true;
            }
        }
        return false;
    }

    class TwoFieldPrompt extends JPanel{
        JTextField field1;
        JTextField field2;
        private TwoFieldPrompt(String fieldName1, String fieldName2){
            add(new JLabel(fieldName1));
            add(field1 = new JTextField(3));
            add(new JLabel(fieldName2));
            add(field2 = new JTextField(3));
        }
        private String getField1() {
            return field1.getText();
        }
        private String getField2() {
            return field2.getText();
        }
    }

    private String openFileChooser(){
        int resultat = fileChooser.showOpenDialog(this);
        if(resultat ==  JFileChooser.CANCEL_OPTION|| fileChooser.getSelectedFile() == null){
            JOptionPane.showMessageDialog(this,"You have to choose a file");
            return null;}
        return fileChooser.getSelectedFile().getAbsolutePath();
    }

    private void nameSearch(){
        String name = searchField.getText();
        if(name != null && !name.isEmpty()){
             pm.nameSelect(name);
        }else{
            JOptionPane.showMessageDialog(this,"You have to write a name!");
        }
    }

    public void addPlace(Place p){
        newButton.setEnabled(true);
        map.addPlace(p);
        p.setVisible(true);
        validate();
        repaint();
    }


    private void newMap(String fileName){
      if(saveCheck()){
        if(fileName != null){
          try{
              pm.removeAll();
              remove(mapScroller);
          }catch(NullPointerException npe){
          }
          map = new MapPanel(fileName);
          pm.addMap(map);
          newButton.setEnabled(true);
          mapScroller = new JScrollPane(map);
          mapScroller.setPreferredSize(map.getPreferredSize());
          add(mapScroller, BorderLayout.CENTER);
          map.setVisible(true);
          validate();
          repaint();
          pm.setSaved(true);
          newButton.setEnabled(true); //Can place points now
        }
      }
    }

    class placeAdder extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            setCursor(Cursor.DEFAULT_CURSOR);
            String name = null;
            Place newPlace = null;
            Position newP = new Position(e.getX(),e.getY());
            map.removeMouseListener(this);
            if(pm.getPlace(newP) != null){
                JOptionPane.showMessageDialog(MainWindow.this,"Positions cant be shared");
                return;
            }
            Category cat;
            try{
            cat = Category.valueOf((categoryList.getSelectedValue()).toString());
            categoryList.clearSelection();}
            catch(NullPointerException ne){ cat = Category.valueOf("None");  }
            if(namedButton.isSelected()){
                name = JOptionPane.showInputDialog("Enter name");
                newPlace = new NamedPlace(newP,cat,name, pm);
            }
            if(describedButton.isSelected()){
               TwoFieldPrompt prompt = new TwoFieldPrompt("Name","Description");
               JOptionPane.showMessageDialog(MainWindow.this,prompt);
               name = prompt.getField1();
               String description = prompt.getField2();
                if( description == null ||  description.isEmpty()){
                    JOptionPane.showMessageDialog(MainWindow.this,"You have to enter a description!");
                    newButton.setEnabled(true);
                    return;
                }
                newPlace = new DescribedPlace(newP,cat,name, description,pm);
            }
            if(name == null || name.isEmpty()) {

                JOptionPane.showMessageDialog(MainWindow.this, "You have to enter a name!");
                newButton.setEnabled(true);
                return;
            }
            if(newPlace != null)
              pm.addPlace(newPlace);
              newButton.setEnabled(true);
            }
        }
    }
