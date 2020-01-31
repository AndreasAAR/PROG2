
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class PlaceManager {
    private boolean dataSaved = true;
    private Map<Position, Place>  allPlaces = new HashMap<>();
    private Map<String, HashSet<Place>> nameMap = new HashMap<>();
    private Map<Category,HashSet<Place>> categoryMap = new HashMap<>();
    private Set<Place>  selectedList = new HashSet<Place>();
    private MainWindow mw;
    private FileManager fm;
    private MapPanel map;
    public PlaceMouse pmo = new PlaceMouse();

    public PlaceManager(MainWindow mw){
        this.mw = mw;
        this.fm = new FileManager(this);
    }

    public void invertSelection(Place p){
        if(isSelected(p)){
            unSelectSingle(p);
        }else{
           selectSingle(p);
        }
        p.repaint();
    }

    public void addMap(MapPanel map){
        this.map = map;
    }

    public void setSaved(boolean b){
        dataSaved = b;
    }

    public boolean isDataSaved() {
        return dataSaved;
    }
    public void addPlace(Place place){
        dataSaved = false;
        mw.addPlace(place);
        allPlaces.put(place.getPosition(), place);
        if(nameMap.get(place.getName()) != null){
          nameMap.get(place.getName()).add(place);}
        else{nameMap.put(place.getName(), new HashSet<>());
             nameMap.get(place.getName()).add(place); }
        if(categoryMap.get(place.getCategory()) != null){
            categoryMap.get(place.getCategory()).add(place);}
        else{categoryMap.put(place.getCategory(), new HashSet<>());
            categoryMap.get(place.getCategory()).add(place); }
    }

    private void showSet(Set<Place> toShow){
        for(Place p : toShow)
            p.setVisible(true);
    }

    public void showCategory(Category c){
        if(categoryMap != null)
        showSet(categoryMap.get(c));
    }

    private void removePlace(Place place){
        nameMap.remove(place.getName());
        categoryMap.remove(place.getCategory());
        allPlaces.remove(place.getPosition());
        dataSaved = false;
        map.removePlace(place);
    }

    public boolean isSelected(Place p){
        return selectedList.contains(p);
    }

    public void removeSelected(){
        removeSet(selectedList);
    }

    public void removeSet(Set<Place> hs){
        Set<Place> temp = new HashSet<>();
        temp.addAll(hs);
        selectedList.removeAll(hs);
        for(Place p: temp){
            removePlace(p);
            p.setVisible(false);
        }
        map.repaint();
    }

    public void removeAll(){
        HashSet<Place> temp = new HashSet<>();
        temp.addAll(allPlaces.values());
        removeSet(temp);
    }

    public void unselectAll(){
        HashSet<Place> temp = new HashSet<>();
        temp.addAll(selectedList);
        unSelectSet(temp);
    }

    public void unSelectSingle(Place p){
        Set<Place> temp = new HashSet();
        temp.add(p);
        unSelectSet(temp);
    }

    public void selectSingle(Place p){
        Set<Place> temp = new HashSet();
        temp.add(p);
        selectSet(temp);
    }

    private void unSelectSet(Set<Place> hs){
        if(hs != null) {
            hs.forEach(p -> {
                selectedList.remove(p);
            });
        }
        map.repaint();
        isAnySelected();
    }

    public void isAnySelected(){
            mw.needSelected(selectedList.size() > 0);
    }

    private void selectSet(Set<Place> hs){
        if(hs != null) {
            hs.forEach(p -> {
                p.setVisible(true);
            });
        }
        selectedList.addAll(hs);
        map.repaint();
        isAnySelected();
    }

    private void hideSet(Set<Place> hs){
        Set<Place> temp = new HashSet();
        temp.addAll(hs);
        if(hs != null) {
            hs.forEach(p -> {
                p.setVisible(false);
            });
        }
        selectedList.removeAll(hs);
        map.repaint();
    }

    public void hideSelected(){
        if(selectedList != null)
        hideSet(selectedList);
        map.repaint();
    }
    public void hideCategory(Category c){
        hideSet(categoryMap.get(c));
    }

    public void loadPlaces(String fileName){
        Set<Place> current = new HashSet<>();
        current.addAll(allPlaces.values());
        if(fileName == null){
            JOptionPane.showMessageDialog(mw,"Have you chosen a file?");
            return;
        }
        removeSet(current);
        int test = fm.loadPlaces(fileName);
        if(test == fm.FAILCODE){
            JOptionPane.showMessageDialog(mw,"File might be in wrong format or corrupt");
            return;
        }
        dataSaved  = true;
    }
    public void savePlaces(String fileName){
        if(fileName == null){
            JOptionPane.showMessageDialog(mw,"Something went wrong with the filename");
            return;
        }
        Set<Place> savePlaces = new HashSet<>();
        savePlaces.addAll(allPlaces.values());
        int test = fm.savePlaces(fileName, savePlaces);
        if(test == fm.FAILCODE){
            JOptionPane.showMessageDialog(mw,"Something went wrong with the file");
            return;
        }
        dataSaved = true;
    }

    public Place getPlace(Position p){
        return allPlaces.get(p);
    }

    public void nameSelect(String name){
        if(nameMap.get(name) != null){
            unselectAll();
            selectSet(nameMap.get(name));
        } else{
            JOptionPane.showMessageDialog(mw,"No place with that name");
        }
    }
    class PlaceMouse extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Place p = (Place)e.getSource();
            super.mouseClicked(e);
            if(e.isMetaDown()){
                p.showInfo();
                return;
            }
            invertSelection(p);
        }
    }
}


